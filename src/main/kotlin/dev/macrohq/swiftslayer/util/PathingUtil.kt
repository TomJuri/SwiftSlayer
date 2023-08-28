package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import net.minecraft.util.BlockPos

object PathingUtil {
    fun goto(pos: BlockPos) {
        runAsync {
            RenderUtil.lines.clear()
            val path = AStarPathfinder(player.getStandingOn(), pos).findPath(10000)
            if (path.isEmpty()) {
                Logger.info("Could not find path!!")
            } else {
                path.forEach { RenderUtil.lines.add(it) }
                pathExecutor.executePath(path)
            }
        }
    }
    fun hasArrived() = !pathExecutor.running
}