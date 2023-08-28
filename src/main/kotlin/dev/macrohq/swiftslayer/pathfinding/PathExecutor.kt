package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.block.BlockSlab
import net.minecraft.block.BlockStairs
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.sqrt

class PathExecutor {
    private var path = listOf<BlockPos>()
    private var current: BlockPos? = null
    var running = false
    var directionYaw = 0f

    fun executePath(inputPath: List<BlockPos>) {
        path = inputPath
        if (running || path.isEmpty()) return
        current = path[0]
        running = true
        directionYaw = player.rotationYaw
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!running) return
        if(path.any{it.x == player.getStandingOn().x && it.z == player.getStandingOn().z && (player.getStandingOn().y-it.y) in 0..10}){
            if (player.getStandingOn().x == path[path.size-1].x && player.getStandingOn().z == path[path.size-1].z){
                running = false
                gameSettings.keyBindSprint.setPressed(false)
                gameSettings.keyBindForward.setPressed(false)
                gameSettings.keyBindJump.setPressed(false)
                return
            }
            current = path[path.indexOf(path.find { it.x == player.getStandingOn().x && it.z == player.getStandingOn().z }) + 1]
            path.dropWhile {it!=current}
        }
        RenderUtil.markers.clear();
        current?.let { RenderUtil.markers.add(it) };
        movePlayer(current!!)
    }

    fun disable() {
        running = false
    }

    private fun movePlayer(current: BlockPos) {
        val jump = (player.onGround && current.y > player.posY - 1 && world.getBlockState(current).block !is BlockSlab
                && world.getBlockState(current).block !is BlockStairs && sqrt(player.getDistanceSqToCenter(current)) < 1.2)
        val rotation = RotationUtil.Rotation(AngleUtil.getAngles(current.toVec3Top()).yaw, 0f)
        directionYaw = rotation.yaw
        gameSettings.keyBindSprint.setPressed(true)
        gameSettings.keyBindForward.setPressed(true)
        gameSettings.keyBindJump.setPressed(jump)
    }

    fun execute(startPos: BlockPos, endPos: BlockPos) {
        running = false
        RenderUtil.lines.clear()
        Thread(Runnable() {
            var tempPath = listOf<BlockPos>()
            val algo = AStarPathfinder(startPos, endPos);
            tempPath = (algo.findPath(10000))
            if (tempPath.isEmpty()) {
                error("Could not find path!!")
            } else {
                tempPath.forEach { RenderUtil.lines.add(it) }
                executePath(tempPath)
            }
        }).start()
    }
}
