package dev.macrohq.swiftslayer.util.movement.config

class PathConfig(
  var sprintFactor: Double = 0.13,
  var walkFactor: Double = 0.1,
  var sneakFactor: Double = 0.03,
  var allowJump: Boolean = true,
  var holdSneak: Boolean = true,
  var maxFallHeight: Int = 20,
  var allowDiagonalAscend: Boolean = false,
  var allowDiagonalDescend: Boolean = false
){}