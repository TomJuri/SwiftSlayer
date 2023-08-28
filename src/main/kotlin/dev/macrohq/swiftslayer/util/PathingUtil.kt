package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import net.minecraft.util.BlockPos

object PathingUtil {
    fun goto(pos: BlockPos, callback: () -> Unit) {
        RenderUtil.lines.clear()
        AStarPathfinder(player.getStandingOn(), pos).findPath(10000) { path ->
            if (path.isEmpty()) {
                Logger.info("Could not find path!!")
            } else {
                path.forEach { RenderUtil.lines.add(it) }
                pathExecutor.executePath(path)
            }
        }
        while (pathExecutor.running) Thread.sleep(100)
        callback()
    }
}