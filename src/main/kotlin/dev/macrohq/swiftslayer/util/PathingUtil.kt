package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.Multithreading.runAsync
import dev.macrohq.swiftslayer.SwiftSlayer.Companion.instance
import dev.macrohq.swiftslayer.pathfinder.calculate.Path
import dev.macrohq.swiftslayer.pathfinder.calculate.path.AStarPathFinder
import dev.macrohq.swiftslayer.pathfinder.goal.Goal
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos

object PathingUtil {
  val isDone get() = !pathExecutor.enabled
  var hasFailed = false
    private set

  fun goto(pos: BlockPos, target: EntityLiving? = null, rotate: Boolean = true) {
    hasFailed = false

    val ctx = CalculationContext(instance)
    val goal = Goal(pos.x, pos.y, pos.z, ctx)
    val start = instance.playerContext.playerPosition
    val pathfinder = AStarPathFinder(start.x, start.y, start.z, goal, ctx)
    var path: Path? = null
    runAsync {
      RenderUtil.lines.clear()
      path = pathfinder.calculatePath()
      if (path == null) {
        hasFailed = true
        Logger.log("Could not find path!!")
      } else {
        if(target != null) {
          pathExecutor.enable(path!!.getSmoothedPath(), target, rotate)
        } else {
          pathExecutor.enable(path!!.getSmoothedPath(), null, rotate)
        }
      }

    }


  }

  fun stop() {
    hasFailed = false
    pathExecutor.disable()
  }
}