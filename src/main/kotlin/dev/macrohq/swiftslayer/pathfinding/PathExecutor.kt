package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PathExecutor {
    private var path = listOf<BlockPos>()
    private var current: BlockPos? = null
    private var pathFailCounter = 0
    private var done = false
    private var aotving = false
    var running = false
    var directionYaw = 0f
    var state: State = State.NONE

    enum class State{
        STARTING,
        CALCULATING,
        WALKING,
        AOTV,
        STOPPING,
        NONE
    }

    fun executePath(inputPath: List<BlockPos>) {
        disable()
        done = false
        if (running || inputPath.isEmpty()) return
        state = State.STARTING
        path = inputPath
        current = path[0]
        running = true
        directionYaw = player.rotationYaw
    }

    fun disable() {
        pathFailCounter = 0
        RotationUtil.stop()
        path = listOf()
        current = null
        directionYaw = 0f
        done = true
        running = false
        aotving = false
        gameSettings.keyBindSprint.setPressed(false)
        gameSettings.keyBindForward.setPressed(false)
        gameSettings.keyBindJump.setPressed(false)
        state = State.NONE
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!running) return
        if(aotving && sqrt(player.lastTickPosition().distanceSq(player.getStandingOnCeil()))>4){aotving = false;}
        when (state) {
            State.STARTING -> {
                state = State.CALCULATING
            }

            State.CALCULATING -> {
                if (path.any { it.x == player.getStandingOnCeil().x && it.z == player.getStandingOnCeil().z && (player.getStandingOnCeil().y - it.y) in 0..10 }) {
                    if (player.getStandingOnCeil().x == path[path.size - 1].x && player.getStandingOnCeil().z == path[path.size - 1].z) {
                        state = State.STOPPING
                        return
                    }
                    pathFailCounter = 0
                    current =
                        path[path.indexOf(path.find { it.x == player.getStandingOnCeil().x && it.z == player.getStandingOnCeil().z }) + 1]
                    RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(current!!).yaw, 20f), 500)

                    RenderUtil.markers.clear()
                    current?.let { RenderUtil.markers.add(it) }

                } else if (player.onGround) pathFailCounter++
                if ((pathFailCounter >= 40 && player.onGround)) {
                    pathFailCounter = 0
                    running = false
                    PathingUtil.goto(path[path.size - 1])
                    return
                }
                state = if(sqrt(player.getDistanceSqToCenter(current)) > 9 && !aotving && path.indexOf(current) != path.size-1) {
                    InventoryUtil.holdItem("of the void")
                    State.AOTV
                }
                else State.WALKING
                return
            }
            State.AOTV -> {
                val yp = AngleUtil.getAngles(current!!.up().up())
                RotationUtil.ease(yp, 200)
                val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw)-AngleUtil.yawTo360(yp.yaw))
                val pitchDiff = abs(mc.thePlayer.rotationPitch - yp.pitch)
                if(yawDiff < 5 && pitchDiff < 2){
                    aotving = true
                    KeyBindUtil.rightClick()
                    RotationUtil.stop()
                }
                state = State.WALKING
            }
            State.WALKING -> {
//                info("jumpDist: ${sqrt((player.posX - current!!.x).pow(2.0) + (player.posZ - current!!.z).pow(2.0))}")
                val jump = player.onGround && (current!!.y +0.5 - player.posY) >= 0.5
                        && (sqrt((player.posX - current!!.x).pow(2.0) + (player.posZ - current!!.z).pow(2.0)) < 2)
                val rotation = RotationUtil.Rotation(AngleUtil.getAngles(current!!.toVec3Top()).yaw, 0f)
                directionYaw = rotation.yaw
                gameSettings.keyBindSprint.setPressed(true)
                gameSettings.keyBindForward.setPressed(true)
                gameSettings.keyBindJump.setPressed(jump)
                state = State.CALCULATING
            }

            State.STOPPING -> {
                disable()
            }

            else -> {}
        }
    }
}
