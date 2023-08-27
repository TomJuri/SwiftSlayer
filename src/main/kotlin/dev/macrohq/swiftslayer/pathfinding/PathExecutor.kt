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
        Logger.info(path.isEmpty())
        if (running || path.isEmpty()) return
        path = ArrayList(inputPath)
        current = path[0]
        running = true
        directionYaw = player.rotationYaw
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!running) return
        Logger.info("Tick")
        if (path.indexOf(getStandingOn()) == path.size - 1) {
            Logger.info("Done")
            running = false
            gameSettings.keyBindSprint.setPressed(false)
            gameSettings.keyBindForward.setPressed(false)
            gameSettings.keyBindJump.setPressed(false)
            return
        }
        current = path[path.indexOf(getStandingOn()) + 1]
        movePlayer(current!!)
    }

    fun disable() {
        running = false
    }

    private fun movePlayer(current: BlockPos) {
        Logger.info("Move")
        val jump =
            (current.y > player.posY - 1 && world.getBlockState(current).block !is BlockSlab && world.getBlockState(
                current
            ).block !is BlockStairs)
        val rotation = AngleUtil.getAngles(current.toVec3())
        RotationUtil.ease(rotation, 1500)
        directionYaw = rotation.yaw
        gameSettings.keyBindSprint.setPressed(true)
        gameSettings.keyBindForward.setPressed(true)
        gameSettings.keyBindJump.setPressed(jump)
    }
}
