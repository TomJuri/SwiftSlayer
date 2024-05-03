package dev.macrohq.swiftslayer.util.movement.movements

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.util.movement.CalculationContext
import dev.macrohq.swiftslayer.util.movement.Movement
import dev.macrohq.swiftslayer.util.movement.MovementHelper
import dev.macrohq.swiftslayer.util.movement.MovementResult
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos

class MovementDescend(ss: SwiftSlayer, from: BlockPos, to: BlockPos) : Movement(ss, from, to) {
  override fun calculateCost(ctx: CalculationContext, res: MovementResult) {
    calculateCost(ctx, source.x, source.y, source.z, dest.x, dest.z, res)
    costs = res.cost
  }


  companion object {
    fun calculateCost(ctx: CalculationContext, x: Int, y: Int, z: Int, destX: Int, destZ: Int, res: MovementResult) {
      res.set(destX, y - 1, destZ)
      cost(ctx, x, y, z, destX, destZ, res)
    }

    private fun cost(ctx: CalculationContext, x: Int, y: Int, z: Int, destX: Int, destZ: Int, res: MovementResult) {
      val destUpState = ctx.get(destX, y, destZ);
      if (!MovementHelper.canWalkThrough(ctx.bsa, destX, y + 2, destZ)
        || !MovementHelper.canWalkThrough(ctx.bsa, destX, y + 1, destZ)
        || !MovementHelper.canWalkThroughBlockState(ctx.bsa, destX, y, destZ, destUpState)
      ) {
        return;
      }
      val sourceState = ctx.get(x, y, z);
      if (MovementHelper.isLadder(sourceState) || MovementHelper.isLadder(destUpState)) { // Cannot descend from ladder it'll be MovementDownward
        return;
      }
      val destState = ctx.get(destX, y - 1, destZ);
      if (!MovementHelper.canStandOnBlockState(ctx.bsa, destX, y - 1, destZ, destState) || MovementHelper.isLadder(
          destState
        )
      ) {
        freeFallCost(ctx, x, y, z, destX, destZ, destState, res);
        return;
      }
      res.cost = ctx.cost.WALK_OFF_ONE_BLOCK_COST * ctx.cost.SPRINT_MULTIPLIER + ctx.cost.N_BLOCK_FALL_COST[1];
    }

    private fun freeFallCost(
      ctx: CalculationContext,
      x: Int,
      y: Int,
      z: Int,
      destX: Int,
      destZ: Int,
      destState: IBlockState,
      res: MovementResult
    ) {
      // im starting from 2 because I work with the blocks itself. x, y, z aren't for sourceBlock.up() like its in baritone its sourceBlock
      if (!MovementHelper.canWalkThroughBlockState(ctx.bsa, destX, y - 1, destZ, destState)) {
        return
      }

      var effStartHeight = y // for ladder
      var cost = 0.0
      for (fellSoFar in 2..Int.MAX_VALUE) {
        // cant visualize reachedMinimum - yet
        val newY = y - fellSoFar
        if (newY < 0) return

        val blockOnto = ctx.get(destX, newY, destZ)
        val unprotectedFallHeight = fellSoFar - (y - effStartHeight) // basic math
        val costUpUntilThisBlock =
          ctx.cost.WALK_OFF_ONE_BLOCK_COST + ctx.cost.N_BLOCK_FALL_COST[unprotectedFallHeight] + cost

        // This is probably a massive monkeypatch. Can't wait to suffer
        if (!MovementHelper.canStandOnBlockState(ctx.bsa, destX, newY, destZ, blockOnto)) {
          if (MovementHelper.isWotah(blockOnto)) {
            if (MovementHelper.canStandOnBlock(ctx.bsa, destX, newY - 1, destZ)) {
              res.y = newY - 1
              res.cost = costUpUntilThisBlock
              return
            }
            return
          }

          if (!MovementHelper.canWalkThroughBlockState(ctx.bsa, destX, newY, destZ, blockOnto)) {
            return
          }
          continue
        }
        if (unprotectedFallHeight <= 11 && MovementHelper.isLadder(blockOnto)) {
          // very cool logic baritone is built by smart ppl ong
          cost += ctx.cost.N_BLOCK_FALL_COST[unprotectedFallHeight - 1] + ctx.cost.ONE_DOWN_LADDER_COST
          effStartHeight = newY
          continue
        }
        if (fellSoFar <= ctx.pathConfig.maxFallHeight) {
          res.y = newY
          res.cost = costUpUntilThisBlock
          return
        }
        return
      }
    }
  }
}