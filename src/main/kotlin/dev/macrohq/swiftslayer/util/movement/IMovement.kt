package dev.macrohq.swiftslayer.util.movement

import dev.macrohq.swiftslayer.SwiftSlayer
import net.minecraft.util.BlockPos

interface IMovement {
  val ss: SwiftSlayer
  val source: BlockPos
  val dest: BlockPos
  val costs: Double // plural cuz kotlin gae

  fun getCost(): Double
  fun calculateCost(ctx: CalculationContext, res: MovementResult)
}