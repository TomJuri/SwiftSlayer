package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.*
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PathExecutor {
    var enabled = false
        private set
    var directionYaw = 0f
        private set
    private var path = listOf<BlockPos>()
    private var next: BlockPos? = null
    private var pathFailCounter = 0
    private var aotving = false

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
                Logger.error("Path execution failed, retrying.")
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
                KeyBindUtil.rightClick()
                aotving = true
                return
            }
            Logger.error("No AOTV in hotbar.")
        }

        RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(next!!).yaw, 20f), 500)
        val rotation = RotationUtil.Rotation(AngleUtil.getAngles(next!!.toVec3Top()).yaw, 0f)
        directionYaw = rotation.yaw
        gameSettings.keyBindSprint.setPressed(true)
        gameSettings.keyBindForward.setPressed(true)
        gameSettings.keyBindJump.setPressed(shouldJump())
    }

    fun enable(pathIn: List<BlockPos>) {
        if (enabled || pathIn.isEmpty()) return
        Logger.info("Enabling PathExecutor.")
        path = pathIn
        next = path[0]
        directionYaw = player.rotationYaw
        pathFailCounter = 0
        aotving = false
        enabled = true
    }

    fun disable() {
        RotationUtil.stop()
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