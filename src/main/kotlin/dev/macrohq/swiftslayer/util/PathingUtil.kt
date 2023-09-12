package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import net.minecraft.util.BlockPos

object PathingUtil {
    val isDone get() = !pathExecutor.enabled
    var hasFailed = false
        private set

    fun goto(pos: BlockPos) {
        hasFailed = false
        runAsync {
            RenderUtil.lines.clear()
            val path = AStarPathfinder(player.getStandingOnCeil(), pos).findPath(1000)
            if (path.isEmpty()) {
                hasFailed = true
                Logger.error("Could not find path!!")
            } else {
                pathExecutor.enable(path)
            }
        }
    }

    fun stop() {
        hasFailed = false
        pathExecutor.disable()
    }
}