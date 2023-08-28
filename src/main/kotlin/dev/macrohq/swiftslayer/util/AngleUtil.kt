package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.util.RotationUtil.Rotation
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

object AngleUtil {
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
    fun getDiffBetweenBlockPos(first: BlockPos, second: BlockPos) = getAngles(first.toVec3()).yaw - getAngles(second.toVec3()).yaw
    fun getAngles(block: BlockPos) = getAngles(block.toVec3())
    fun getAngles(entity: Entity) = getAngles(entity.positionVector.add(Vec3(0.0, entity.eyeHeight.toDouble(), 0.0)))
}
