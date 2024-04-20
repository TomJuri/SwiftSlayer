package dev.macrohq.swiftslayer.util

import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

class RotationMath {

    var mc: Minecraft = Minecraft.getMinecraft()



    companion object {
        fun easeInOut(t: Float): Float {
            return if (t < 0.5) 2 * t * t else -1 + (4 - 2 * t) * t
        }

        fun interpolate(goal: Float, current: Float, time: Float): Float {
            val t = easeInOut(time)
            val difference = goal - current
            val shortestPathDifference = (difference + 180) % 360 - 180
            val target = current + shortestPathDifference * t
            return target
        }
        fun toClock(seconds: Int): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60

            return String.format("%dh, %dm, %ds", hours, minutes, remainingSeconds)
        }

        fun toFancyNumber(number: Int): String {
            val k = number / 1000
            val m = number / 1000000
            val remaining = number % 1000000

            return if (m > 0) {
                String.format("%dM", m)
            } else if (k > 0) {
                String.format("%dk", k)
            } else {
                String.format("%d", remaining)
            }
        }

        fun getYaw(blockPos: BlockPos): Float {
            val deltaX: Double = blockPos.x + 0.5 - mc.thePlayer.posX
            val deltaZ: Double = blockPos.z + 0.5 - mc.thePlayer.posZ
            val yawToBlock = atan2(-deltaX, deltaZ)
            val yaw = Math.toDegrees(yawToBlock)

            return yaw.toFloat()
        }

        fun getPitch(blockPos: BlockPos): Float {
            val deltaX: Double = blockPos.x + 0.5 - mc.thePlayer.posX
            val deltaY: Double = blockPos.y + 0.5 - mc.thePlayer.posY - mc.thePlayer.getEyeHeight()
            val deltaZ: Double = blockPos.z + 0.5 - mc.thePlayer.posZ
            val distanceXZ = sqrt(deltaX * deltaX + deltaZ * deltaZ)
            val pitchToBlock = -atan2(deltaY, distanceXZ)
            var pitch = Math.toDegrees(pitchToBlock)

            return pitch.toFloat()
        }

        fun fromBlockPos(pos: BlockPos): Vec3 {
            return Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        }



        fun getXZDistance(pos1: BlockPos, pos2: BlockPos): Double {
            val xDiff = (pos1.x - pos2.x).toDouble()
            val zDiff = (pos1.z - pos2.z).toDouble()
            return sqrt(xDiff * xDiff + zDiff * zDiff)
        }
    }
}
