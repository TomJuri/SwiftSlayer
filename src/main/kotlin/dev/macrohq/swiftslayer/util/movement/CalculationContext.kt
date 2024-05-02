package dev.macrohq.swiftslayer.util.movement

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.util.movement.config.PathConfig
import net.minecraft.block.state.IBlockState

class CalculationContext(val ss: SwiftSlayer, val pathConfig: PathConfig = PathConfig()) {
  val world = ss.playerContext.world
  val player = ss.playerContext.player
  val bsa = ss.bsa!! // If this is a null pls fix ur brain dev
  val cost = ActionCosts(pathConfig.sprintFactor, pathConfig.walkFactor, pathConfig.sneakFactor)

  fun get(x: Int, y: Int, z: Int): IBlockState{
    return bsa.get(x, y, z)
  }
}