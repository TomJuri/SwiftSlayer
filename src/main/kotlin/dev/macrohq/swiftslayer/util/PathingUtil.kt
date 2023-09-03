package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import net.minecraft.entity.EntityLiving
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
            val path = AStarPathfinder(player.getStandingOn(), pos).findPath(10000)
            if (path.isEmpty()) {
                hasFailed = true
                Logger.info("Could not find path!!")
            } else {
                RenderUtil.markers.addAll(path)
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