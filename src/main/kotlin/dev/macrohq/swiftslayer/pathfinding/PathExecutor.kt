package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.block.BlockSlab
import net.minecraft.block.BlockStairs
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class PathExecutor {
    private var path = listOf<BlockPos>()
    private var current: BlockPos? = null
    var running = false
    var directionYaw = 0f

    fun executePath(inputPath: List<BlockPos>) {
        path = ArrayList(inputPath)
        if (running || path.isEmpty()) return
        current = path[0]
        running = true
        directionYaw = player.rotationYaw
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!running) return
        if (path.indexOf(player.getStandingOn()) == path.size - 1) {
            running = false
            gameSettings.keyBindSprint.setPressed(false)
            gameSettings.keyBindForward.setPressed(false)
            gameSettings.keyBindJump.setPressed(false)
            return
        }
        current = path[path.indexOf(player.getStandingOn()) + 1]
        movePlayer(current!!)
    }

    fun disable() {
        running = false
    }

    private fun movePlayer(current: BlockPos) {
        val jump = (current.y > player.posY - 1 && world.getBlockState(current).block !is BlockSlab && world.getBlockState(current).block !is BlockStairs)
        val rotation = RotationUtil.Rotation(AngleUtil.getAngles(current.toVec3()).yaw, 0f)
        RotationUtil.ease(rotation, 1500)
        directionYaw = rotation.yaw
        gameSettings.keyBindSprint.setPressed(true)
        gameSettings.keyBindForward.setPressed(true)
        gameSettings.keyBindJump.setPressed(jump)
    }
}
