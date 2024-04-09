package dev.macrohq.swiftslayer.pathfinder.movement.config

import dev.macrohq.swiftslayer.SwiftSlayer

class PathConfig(
  var sprintFactor: Double = 0.13,
  var walkFactor: Double = 0.1,
  var sneakFactor: Double = 0.03,
  var allowJump: Boolean = SwiftSlayer.instance.config.allowJump,
  var holdSneak: Boolean = SwiftSlayer.instance.config.holdSneak,
  var maxFallHeight: Int = SwiftSlayer.instance.config.maxFallHeight,
  var allowDiagonalAscend: Boolean = SwiftSlayer.instance.config.allowDiagonalAscend,
  var allowDiagonalDescend: Boolean = SwiftSlayer.instance.config.allowDiagonalDescend
){}