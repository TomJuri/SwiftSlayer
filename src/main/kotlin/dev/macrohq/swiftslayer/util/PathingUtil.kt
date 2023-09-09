package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import net.minecraft.util.BlockPos

object PathingUtil {

    var isDone = true
        private set
    private var hasFailed = false

    fun goto(pos: BlockPos) {
        if(!isDone) return
        hasFailed = false
        isDone = false
        runAsync {
            RenderUtil.lines.clear()
            val path = AStarPathfinder(player.getStandingOnCeil(), pos).findPath(1000)
            if (path.isEmpty()) {
                hasFailed = true
                Logger.info("Could not find path!!")
            } else {
                pathExecutor.executePath(path)
            }
            while(pathExecutor.running) Thread.sleep(1)
            isDone = true
        }
    }

    fun stop() {
        isDone = true
        pathExecutor.disable()
    }

    fun hasFailed(): Boolean{
        return hasFailed
    }

}