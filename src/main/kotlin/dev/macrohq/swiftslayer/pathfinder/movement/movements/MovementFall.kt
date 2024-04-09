package dev.macrohq.swiftslayer.pathfinder.movement.movements

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.pathfinder.movement.Movement
import dev.macrohq.swiftslayer.pathfinder.movement.MovementResult
import net.minecraft.util.BlockPos
import kotlin.concurrent.fixedRateTimer

class MovementFall(ss: SwiftSlayer, source: BlockPos, dest: BlockPos) : Movement(ss, source, dest) {
  override fun calculateCost(ctx: CalculationContext, res: MovementResult) {
    calculateCost(ctx, source.x, source.y, source.z, dest.x, dest.z, res)
    costs = res.cost
  }

  companion object{
    fun calculateCost(ctx: CalculationContext, x: Int, y: Int, z: Int, destX: Int, destZ: Int, res: MovementResult) {
      res.set(destX, y - 1, destZ)
      MovementDescend.calculateCost(ctx, x, y, z, destX, destZ, res)
    }
  }
}
