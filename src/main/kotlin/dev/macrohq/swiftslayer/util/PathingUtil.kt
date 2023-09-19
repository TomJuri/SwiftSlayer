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
        Logger.log("Could not find path!!")
      } else {
        Logger.info("enabled patehxeutor")
        pathExecutor.enable(path)
      }
    }
  }

  fun stop() {
    hasFailed = false
    pathExecutor.disable()
  }

  fun getDifferentPosition(): BlockPos? {
    val pos = player.getStandingOnCeil()
    for (x in 2..10) {
      for (y in 0..3) {
        for (z in 2..10) {
          val newPos = pos.add(x, y, z)
          if (player.worldObj.getBlockState(newPos).block.isCollidable && !player.worldObj.getBlockState(newPos.up()).block.isCollidable && !player.worldObj.getBlockState(
              newPos.up().up()
            ).block.isCollidable
          ) return newPos
        }
      }
    }
    return null
  }
}