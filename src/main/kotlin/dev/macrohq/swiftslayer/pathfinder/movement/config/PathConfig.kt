package dev.macrohq.swiftslayer.pathfinder.movement.config

import dev.macrohq.swiftslayer.SwiftSlayer

class PathConfig(
  var sprintFactor: Double = 1.3 * SwiftSlayer.instance.playerContext.player.capabilities.walkSpeed,
  var walkFactor: Double = SwiftSlayer.instance.playerContext.player.capabilities.walkSpeed.toDouble(),
  var sneakFactor: Double = SwiftSlayer.instance.playerContext.player.capabilities.walkSpeed*0.3,

  var allowJump: Boolean = true,
  var holdSneak: Boolean = true,
  var maxFallHeight: Int = 20,
  var allowDiagonalAscend: Boolean = false,
  var allowDiagonalDescend: Boolean = false

)