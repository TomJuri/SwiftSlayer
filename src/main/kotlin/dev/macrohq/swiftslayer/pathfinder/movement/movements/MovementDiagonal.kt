package dev.macrohq.swiftslayer.pathfinder.movement.movements

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.pathfinder.movement.Movement
import dev.macrohq.swiftslayer.pathfinder.movement.MovementHelper
import dev.macrohq.swiftslayer.pathfinder.movement.MovementResult
import net.minecraft.util.BlockPos
import kotlin.math.sqrt

class MovementDiagonal(ss: SwiftSlayer, from: BlockPos, to: BlockPos) : Movement(ss, from, to) {

  override fun calculateCost(ctx: CalculationContext, res: MovementResult) {
    calculateCost(ctx, source.x, source.y, source.z, dest.x, dest.z, res)
    costs = res.cost
  }

  companion object {
    private val SQRT_2 = sqrt(2.0)
    fun calculateCost(ctx: CalculationContext, x: Int, y: Int, z: Int, destX: Int, destZ: Int, res: MovementResult) {
      res.set(destX, y, destZ)
      cost(ctx, x, y, z, destX, destZ, res)
    }

    // when walking from bottom to bottom slab if theres lava on the sides it'll still go. maybe add that? maybe not idk
    private fun cost(ctx: CalculationContext, x: Int, y: Int, z: Int, destX: Int, destZ: Int, res: MovementResult) {
      if (!MovementHelper.canWalkThrough(ctx.bsa, destX, y + 2, destZ)) return

      val destUpState = ctx.get(destX, y + 1, destZ)
      val isSourceBottomSlab = MovementHelper.isBottomSlab(ctx.get(x, y, z))
      var isDestBottomSlab = false
      var canAscend = false
      var canDescend = false
      if (!MovementHelper.canWalkThroughBlockState(ctx.bsa, destX, y + 1, destZ, destUpState)) {
        canAscend = true
        isDestBottomSlab = MovementHelper.isBottomSlab(destUpState)
        if ((isSourceBottomSlab && !isDestBottomSlab) || MovementHelper.isLadder(
            destUpState
          ) || !MovementHelper.canWalkThrough(ctx.bsa, x, y + 3, z) || !MovementHelper.canStandOnBlockState(
            ctx.bsa,
            destX,
            y + 1,
            destZ,
            destUpState
          ) || !MovementHelper.canWalkThrough(ctx.bsa, destX, y + 3, destZ)
        ) {
          return
        }
      } else {
        if (!MovementHelper.canStandOnBlock(ctx.bsa, destX, y, destZ)) {
          canDescend = true
          val newDestState = ctx.get(destX, y - 1, destZ)
          if (!MovementHelper.canStandOnBlockState(
              ctx.bsa,
              destX,
              y - 1,
              destZ,
              newDestState
            ) || !MovementHelper.canWalkThrough(ctx.bsa, destX, y, destZ)
          ) {
            return
          }
          isDestBottomSlab = MovementHelper.isBottomSlab(newDestState)
        }
      }
      var cost = ctx.cost.ONE_BLOCK_WALK_COST

      val sourceState = ctx.get(x, y, z)
      if (MovementHelper.isLadder(sourceState)) {
        return
      }

      var water = false
      if (MovementHelper.isWotah(ctx.get(x, y + 1, z))) {
        if (canAscend) {
          return
        }
        cost = ctx.cost.ONE_BLOCK_WALK_IN_WATER_COST * SQRT_2
        water = true
      }
      val ALOWState = ctx.get(x, y + 1, destZ)
      val BLOWState = ctx.get(destX, y + 1, z)

      val ATOP = MovementHelper.canWalkThrough(ctx.bsa, x, y + 3, destZ)
      val AMID = MovementHelper.canWalkThrough(ctx.bsa, x, y + 2, destZ)
      val ALOW = MovementHelper.canWalkThroughBlockState(ctx.bsa, x, y + 1, destZ, ALOWState)
      val BTOP = MovementHelper.canWalkThrough(ctx.bsa, destX, y + 3, z)
      val BMID = MovementHelper.canWalkThrough(ctx.bsa, destX, y + 2, z)
      val BLOW = MovementHelper.canWalkThroughBlockState(ctx.bsa, destX, y + 1, z, BLOWState)

      if (!(ATOP && AMID && ALOW && BTOP && BMID && BLOW)) {
        return
      }
      if (canAscend) {
        res.y = y + 1
        res.cost = cost * SQRT_2
        if (isSourceBottomSlab || !isDestBottomSlab) {
          res.cost += ctx.cost.JUMP_ONE_BLOCK_COST
        } else {
          res.cost *= ctx.cost.SPRINT_MULTIPLIER
        }
        return
      }

      if (!water) {
        cost *= SQRT_2 * ctx.cost.SPRINT_MULTIPLIER
      }
      if (canDescend) {
        if (isSourceBottomSlab == isDestBottomSlab) {
          cost += ctx.cost.N_BLOCK_FALL_COST[1]
        }
        res.y = y - 1
      }
      res.cost = cost
    }
  }
}