package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.util.RotationUtil.Rotation
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object AngleUtil {

  // Start of AngleUtil with Angle Data Class
  val PLAYER_ANGLE get() = Angle(normalizeAngle(player.rotationYaw), player.rotationPitch)

  fun yawTo360(yaw: Float): Float {
    return (((yaw % 360) + 360) % 360)
  }

  private fun normalizeAngle(yaw: Float): Float {
    var convertedYaw = yaw % 360.0f
    if (convertedYaw > 180.0f) {
      convertedYaw -= 360.0f
    } else if (convertedYaw < -180.0f) {
      convertedYaw += 360.0f
    }
    return convertedYaw
  }

  fun getAngle(endVec: Vec3, startVec: Vec3 = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)): Angle {
    val dX = endVec.xCoord - startVec.xCoord
    val dY = endVec.yCoord - startVec.yCoord
    val dZ = endVec.zCoord - startVec.zCoord

    val yaw = -Math.toDegrees(atan2(dX, dZ)).toFloat()
    val pitch = -Math.toDegrees(atan2(dY, sqrt(dX * dX + dZ * dZ))).toFloat()
    return Angle(this.reduceTrailingPointsTo(yaw, 2), this.reduceTrailingPointsTo(pitch, 2))
  }

  fun getAngle(blockPos: BlockPos): Angle {
//    return getAngle(BlockUtil.getClosestSidePos(blockPos))
    return getAngle(blockPos.toVec3());
  }

  fun getAngle(entity: Entity, height: Float = 1.5f): Angle {
    return getAngle(entity.positionVector.addVector(0.0, height.toDouble(), 0.0))
  }

  fun calculateNeededAngleChange(entity: Entity, height: Float = 1.5f): Angle {
    val end = entity.positionVector.addVector(0.0, height.toDouble(), 0.0)
    return calculateNeededAngleChange(end)
  }

//  fun calculateNeededAngleChange(blockPos: BlockPos): Angle {
//    return calculateNeededAngleChange(BlockUtil.getClosestSidePos(blockPos))
//  }

  fun calculateNeededAngleChange(vecPos: Vec3): Angle {
    val end = getAngle(vecPos)
    return calculateNeededAngleChange(PLAYER_ANGLE, end)
  }

  fun calculateNeededAngleChange(startRot: Angle, endRot: Angle): Angle {
    var yawChange = MathHelper.wrapAngleTo180_float(endRot.yaw) - MathHelper.wrapAngleTo180_float(startRot.yaw)
    if (yawChange <= -180.0f) yawChange += 360.0f else if (yawChange > 180.0f) yawChange += -360.0f
    return Angle(yawChange, endRot.pitch - startRot.pitch)
  }

  fun reduceTrailingPointsTo(value: Float, number: Int = 2): Float {
    val multiplier = 10f.pow(number)
    return (value * multiplier).toInt() / multiplier
  }

  // End of AngleUtil with Angle Data Class

  // ANGLE -> Replacement for Rotation Class

  fun getAngles(vec: Vec3): Rotation {
    val diffX = vec.xCoord - player.posX
    val diffY = vec.yCoord - (player.posY + player.getEyeHeight())
    val diffZ = vec.zCoord - player.posZ
    val dist = sqrt(diffX * diffX + diffZ * diffZ)
    val yaw = (atan2(diffZ, diffX) * 180.0 / Math.PI).toFloat() - 90f
    val pitch = (-(atan2(diffY, dist) * 180.0 / Math.PI)).toFloat()
    return Rotation(
      player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw),
      player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch)
    )
  }

  fun getDiffBetweenBlockPos(first: BlockPos, second: BlockPos) =
    getAngles(first.toVec3()).yaw - getAngles(second.toVec3()).yaw

  fun getAngles(block: BlockPos) = getAngles(block.toVec3())
  fun getAngles(entity: Entity) = getAngles(entity.positionVector.add(Vec3(0.0, entity.eyeHeight.toDouble(), 0.0)))

  fun getNeededChange(startRot: Rotation, endRot: Rotation): Rotation {
    var yawChange = MathHelper.wrapAngleTo180_float(endRot.yaw) - MathHelper.wrapAngleTo180_float(startRot.yaw)
    if (yawChange <= -180.0f) yawChange += 360.0f else if (yawChange > 180.0f) yawChange += -360.0f
    return Rotation(yawChange, endRot.pitch - startRot.pitch)
  }

  fun getYawChange(entity: Entity): Float {
    val startRot = Rotation(player.rotationYaw, 0f)
    val endRot = Rotation(getAngles(entity.positionVector).yaw, 0f)
    return getNeededChange(startRot, endRot).yaw
  }

  fun getPitchChange(entity: Entity): Float {
    val startRot = Rotation(0f, player.rotationPitch)
    val endRot = Rotation(0f, getAngles(entity.positionVector).pitch)
    return getNeededChange(startRot, endRot).pitch
  }
}
