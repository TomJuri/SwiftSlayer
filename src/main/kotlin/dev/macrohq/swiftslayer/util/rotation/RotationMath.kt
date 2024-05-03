package dev.macrohq.swiftslayer.util.rotation

import net.minecraft.util.BlockPos
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


class RotationMath {

    companion object {
        private var instance: RotationMath? = null
        fun getInstance(): RotationMath {
            if (instance == null) {
                instance = RotationMath()
            }
            return instance!!
        }
    }
    fun calculateNeededRotation(source: BlockPos, target: BlockPos): Rotation {
        val deltaX = (target.x - source.x).toFloat()
        val deltaY = (target.y - source.y).toFloat()
        val deltaZ = (target.z - source.z).toFloat()

        return Rotation(
            toDegrees(atan2(-deltaX, deltaZ).toDouble()).toFloat(),
            -toDegrees(atan2(deltaY, sqrt(deltaX * deltaX + deltaZ * deltaZ)).toDouble()).toFloat()
        )
    }

    fun binomialCoefficient(n: Int, k: Int): Int {
        if (k > n) return 0
        if (k == 0 || k == n) return 1

        return (binomialCoefficient(n - 1, k - 1)
                + binomialCoefficient(n - 1, k))
    }

    fun calculateBezierPath(yawControlPoints: List<Float>, pitchControlPoints: List<Float>, t: Float): Rotation {
        return Rotation(
            calculateBezierT(yawControlPoints, t),
            calculateBezierT(pitchControlPoints, t)
        )
    }

    private fun calculateBezierT(controlPoints: List<Float>, t: Float): Float {
        val n = controlPoints.size
        var y = 0f
        for (i in 0 until n) { y += (binomialCoefficient(n, (i + 1)) * (1 - t).pow((n - (i + 1)).toFloat()) * t.pow((i + 1).toFloat()) * controlPoints[i])
        }

        return y
    }
}