package dev.macrohq.swiftslayer.pathfinding

import cc.polyfrost.oneconfig.utils.Multithreading.runAsync
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.block.BlockSlab
import net.minecraft.block.BlockStairs
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PathExecutor {
    private var path = listOf<BlockPos>()
    private var current: BlockPos? = null
    private var pathFailCounter = 0;
    var running = false
    private var done = false
    private var aotving = false
    var directionYaw = 0f
    var state: State = State.NONE

    enum class State{
        STARTING,
        CALCULATING,
        WALKING,
        AOTV,
        AOTV_VERIFY,
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
        info("started. aotving: $aotving")
    }

    fun disable() {
        info("disabling")
        state = State.NONE
        pathFailCounter = 0
        RotationUtil.stop()
        running = false
        path = listOf()
        aotving = false
        current = null
        directionYaw = 0f
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!running) return
        if(aotving && sqrt(player.lastTickPosition().distanceSq(player.getStandingOn()))>4){aotving = false; info("aotv'd")}

        when (state) {
            State.STARTING -> {
                state = State.CALCULATING;
            }

            State.CALCULATING -> {
                if (path.any { it.x == player.getStandingOn().x && it.z == player.getStandingOn().z && (player.getStandingOn().y - it.y) in 0..10 }) {
                    if (player.getStandingOn().x == path[path.size - 1].x && player.getStandingOn().z == path[path.size - 1].z) {
                        state = State.STOPPING
                        return
                    }
                    pathFailCounter = 0
                    current =
                        path[path.indexOf(path.find { it.x == player.getStandingOn().x && it.z == player.getStandingOn().z }) + 1]
                    RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(current!!).yaw, 0f), 500)

                    RenderUtil.markers.clear();
                    current?.let { RenderUtil.markers.add(it) };

                } else if (player.onGround) pathFailCounter++
                if (pathFailCounter >= 100 && player.onGround) {
                    pathFailCounter = 0
                    running = false
                    PathingUtil.goto(path[path.size - 1])
                    return;
                }
                state = if(sqrt(player.getDistanceSqToCenter(current)) > 12 && !aotving) State.AOTV
                else State.WALKING
                return
            }
            State.AOTV -> {
                RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(current!!).yaw, 0f), 500)
                val yp = RotationUtil.Rotation(AngleUtil.getAngles(current!!).yaw, 0f)
                val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw)-AngleUtil.yawTo360(yp.yaw));
                val pitchDiff = abs(mc.thePlayer.rotationPitch - yp.pitch);
                if(yawDiff < 5 && pitchDiff < 2){
                    aotving = true
                    KeyBindUtil.rightClick()
                }
                state = State.WALKING
            }
            State.WALKING -> {
                val jumpp = player.onGround && current!!.y > player.posY - 1 && !BlockUtil.isStairSlab(current!!)
                        && (sqrt((player.getStandingOn().x - current!!.x).toDouble().pow(2.0) +
                        (player.getStandingOn().z - current!!.z).toDouble().pow(2.0)) < 1.5) && !BlockUtil.isStairSlab(
                    player.getStandingOn())
                val rotation = RotationUtil.Rotation(AngleUtil.getAngles(current!!.toVec3Top()).yaw, 0f)
                directionYaw = rotation.yaw
                gameSettings.keyBindSprint.setPressed(true)
                gameSettings.keyBindForward.setPressed(true)
                gameSettings.keyBindJump.setPressed(jumpp)
                state = State.CALCULATING
            }

            State.STOPPING -> {
                done = true
                running = false
                aotving = false
                gameSettings.keyBindSprint.setPressed(false)
                gameSettings.keyBindForward.setPressed(false)
                gameSettings.keyBindJump.setPressed(false)
                state = State.NONE
                return
            }

            else -> {}
        }
    }
//
//        if(path.any{it.x == player.getStandingOn().x && it.z == player.getStandingOn().z && (player.getStandingOn().y-it.y) in 0..10}){
//            if (player.getStandingOn().x == path[path.size-1].x && player.getStandingOn().z == path[path.size-1].z){
//                done = true
//                running = false
//                aotving = false
//                gameSettings.keyBindSprint.setPressed(false)
//                gameSettings.keyBindForward.setPressed(false)
//                gameSettings.keyBindJump.setPressed(false)
//                return
//            }
//            pathFailCounter = 0
//            current = path[path.indexOf(path.find { it.x == player.getStandingOn().x && it.z == player.getStandingOn().z }) + 1]
//            RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(current!!).yaw, 20f), 500)
//        }
//        else if(player.onGround) pathFailCounter++
//
//        if(pathFailCounter>=100 && player.onGround){
//            pathFailCounter = 0
//            running = false
//            PathingUtil.goto(path[path.size-1])
//            return;
//        }
//        RenderUtil.markers.clear();
//        current?.let { RenderUtil.markers.add(it) };
//        movePlayer(current!!)
//    }

//    private fun movePlayer(current: BlockPos) {
//        val jump = (player.onGround && current.y > player.posY - 1 &&!BlockUtil.isStairSlab(current)  && !BlockUtil.isStairSlab(
//            player.getStandingOn())
//                && sqrt((player.getStandingOn().x - current.x).toDouble().pow(2.0) + (player.getStandingOn().z - current.z).toDouble().pow(2.0)) < 1.2)
//        val rotation = RotationUtil.Rotation(AngleUtil.getAngles(current.toVec3Top()).yaw, 0f)
//        directionYaw = rotation.yaw
//        gameSettings.keyBindSprint.setPressed(true)
//        gameSettings.keyBindForward.setPressed(true)
//    }
}
