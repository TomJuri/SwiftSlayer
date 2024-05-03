package dev.macrohq.swiftslayer.util

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Some Predefined Ease Function
object EaseUtil {
  val easingFunctions: List<(Float) -> Float> = listOf(
    ::easeOutBack,
    ::easeOutSine,
    ::easeInOutSine,
    ::easeOutQuad,
    ::easeOutCubic,
    ::easeOutCirc,
    ::easeOutMinJerk
  )

  fun easeOutBack(x: Float): Float = 1 + 2 * (x - 1) * (x - 1) * (x - 1) + (x - 1) * (x - 1)
  fun easeOutSine(x: Float): Float = sin((x * Math.PI) / 2f).toFloat()
  fun easeInOutSine(x: Float): Float = (-(cos(x * Math.PI) - 1) / 2f).toFloat()
  fun easeOutQuad(x: Float): Float = 1 - (1 - x) * (1 - x)
  fun easeOutCubic(x: Float): Float = 1 - (1 - x) * (1 - x) * (1 - x)
  fun easeOutCirc(x: Float): Float = sqrt(1 - (x - 1) * (x - 1))
  fun easeOutMinJerk(x: Float): Float = 6 * x.pow(5) - 15 * x.pow(4) + 10 * x.pow(3)
}