package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos

object PathingUtil {
  val isDone get() = !pathExecutor.enabled
  var hasFailed = false
    private set

  fun goto(pos: BlockPos, target: EntityLiving? = null, rotate: Boolean = true) {
    hasFailed = false
    runAsync {
      RenderUtil.lines.clear()
      val path = AStarPathfinder(player.getStandingOnCeil(), pos).findPath(1000)
      if (path.isEmpty()) {
        hasFailed = true
        Logger.log("Could not find path!!")
      } else {
        if(target != null) {
          pathExecutor.enable(path, target, rotate)
        } else {
          pathExecutor.enable(path, null, rotate)
        }
        if(rotate) {
          val next = path[1]
          val time = SwiftSlayer.config.calculateRotationTime(SwiftSlayer.config.calculateDegreeDistance(AngleUtil.yawTo360(mc.thePlayer.rotationYaw).toDouble(), mc.thePlayer.rotationPitch.toDouble(), AngleUtil.yawTo360(Target(next!!).getAngle().yaw).toDouble(), Target(next!!).getAngle().pitch.toDouble()))
          AutoRotation.getInstance().easeTo(Target(Angle(AngleUtil.getAngle(next.toVec3Top()).yaw, 20f)), time / 2, LockType.NONE, false)
        }
      }

    }
  }

  fun stop() {
    hasFailed = false
    pathExecutor.disable()
  }
}