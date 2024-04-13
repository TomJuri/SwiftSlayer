package dev.macrohq.swiftslayer.util;

import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

public class RotationMath {

    var mc: Minecraft = Minecraft.getMinecraft()



    companion object {
        fun easeInOut(t: Float): Float {
            return if (t < 0.5) 2 * t * t else -1 + (4 - 2 * t) * t
        }

        fun interpolate(goal: Float, current: Float, time: Float): Float {
            var current = current
            while (goal - current > 180) {
                current += 360f
            }
            while (goal - current < -180) {
                current -= 360f
            }

            val t = easeInOut(time)
            return current + (goal - current) * t
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
            var yaw = Math.toDegrees(yawToBlock)

            yaw = (yaw + 360) % 360
            if (yaw > 180) {
                yaw -= 360.0
            }

            return yaw.toFloat()
        }

        fun fromBlockPos(pos: BlockPos): Vec3 {
            return Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        }

        fun getPitch(blockPos: BlockPos): Float {
            val deltaX: Double = blockPos.x + 0.5 - mc.thePlayer.posX
            val deltaY: Double = blockPos.y + 0.5 - mc.thePlayer.posY - mc.thePlayer.getEyeHeight()
            val deltaZ: Double = blockPos.z + 0.5 - mc.thePlayer.posZ
            val distanceXZ = sqrt(deltaX * deltaX + deltaZ * deltaZ)
            val pitchToBlock = -atan2(deltaY, distanceXZ)
            var pitch = Math.toDegrees(pitchToBlock)
            pitch = max(-90.0, min(90.0, pitch))
            return pitch.toFloat()
        }

        fun getXZDistance(pos1: BlockPos, pos2: BlockPos): Double {
            val xDiff = (pos1.x - pos2.x).toDouble()
            val zDiff = (pos1.z - pos2.z).toDouble()
            return sqrt(xDiff * xDiff + zDiff * zDiff)
        }
    }
}
