package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.*
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PathExecutor {
    private var path = listOf<BlockPos>()
    private var next: BlockPos? = null
    private var pathFailCounter = 0
    private var done = false
    private var aotving = false
    var enabled = false
    var directionYaw = 0f

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        if (hasReachedEnd()) {
            Logger.info("Reached end of path.")
            disable()
            return
        }

        if (aotving && !hasAOTVed()) {
            Logger.log("Waiting for AOTV to arrive.")
            return
        } else if (aotving && hasAOTVed()) {
            aotving = false
        }

        if (!isOnPath() && player.onGround) {
            if (pathFailCounter >= 40) {
                disable()
                PathingUtil.goto(path[path.size - 1])
                return
            }
            pathFailCounter++
            return
        }
        pathFailCounter = 0

        next = path[path.indexOf(getStandingOn()!!) + 1]
        RenderUtil.markers.clear()
        RenderUtil.markers.add(next!!)

        if (canAOTV()) {
            if (InventoryUtil.getHotbarSlotForItem("Aspect of the Void") != 100) {
                Logger.log("No AOTV in hotbar.")
                KeyBindUtil.rightClick()
                aotving = true
            }

            return
        }
        RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(next!!).yaw, 20f), 500)
        val rotation = RotationUtil.Rotation(AngleUtil.getAngles(next!!.toVec3Top()).yaw, 0f)
        directionYaw = rotation.yaw
        gameSettings.keyBindSprint.setPressed(true)
        gameSettings.keyBindForward.setPressed(true)
        gameSettings.keyBindJump.setPressed(shouldJump())
    }

    fun enable(path: List<BlockPos>) {
        disable()
        done = false
        if (enabled || path.isEmpty()) return
        this.path = path
        next = this.path[0]
        enabled = true
        directionYaw = player.rotationYaw
    }

    fun disable() {
        pathFailCounter = 0
        RotationUtil.stop()
        path = listOf()
        next = null
        directionYaw = 0f
        done = true
        enabled = false
        aotving = false
        gameSettings.keyBindSprint.setPressed(false)
        gameSettings.keyBindForward.setPressed(false)
        gameSettings.keyBindJump.setPressed(false)
    }

    private fun canAOTV(): Boolean {
        val d = sqrt(player.getDistanceSqToCenter(next)) > 9 && !aotving && path.indexOf(next) != path.size - 1
        val yp = AngleUtil.getAngles(next!!.up().up())
        val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw) - AngleUtil.yawTo360(yp.yaw))
        val pitchDiff = abs(mc.thePlayer.rotationPitch - yp.pitch)
        return d && yawDiff < 5 && pitchDiff < 2 && config.useAOTV
    }

    private fun shouldJump() = player.onGround && (next!!.y + 0.5 - player.posY) >= 0.5 && (sqrt(
        (player.posX - next!!.x).pow(2.0) + (player.posZ - next!!.z).pow(2.0)
    ) < 2)

    private fun isOnPath() = getStandingOn() != null
    private fun getStandingOn() =
        path.find { it.x == player.getStandingOnCeil().x && it.z == player.getStandingOnCeil().z }

    private fun hasReachedEnd() =
        player.getStandingOnCeil().x == path[path.size - 1].x && player.getStandingOnCeil().z == path[path.size - 1].z

    private fun hasAOTVed() = sqrt(player.lastTickPosition().distanceSq(player.getStandingOnCeil())) > 4

}