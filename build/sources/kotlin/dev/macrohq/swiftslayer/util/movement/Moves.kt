package dev.macrohq.swiftslayer.util.movement

import dev.macrohq.swiftslayer.util.movement.movements.MovementAscend
import dev.macrohq.swiftslayer.util.movement.movements.MovementDescend
import dev.macrohq.swiftslayer.util.movement.movements.MovementDiagonal
import dev.macrohq.swiftslayer.util.movement.movements.MovementTraverse

enum class Moves(val offsetX: Int, val offsetZ: Int) {
  TRAVERSE_NORTH(0, -1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementTraverse.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  TRAVERSE_SOUTH(0, +1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementTraverse.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  TRAVERSE_EAST(+1, 0) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementTraverse.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  TRAVERSE_WEST(-1, 0) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementTraverse.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  ASCEND_NORTH(0, -1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementAscend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  ASCEND_SOUTH(0, +1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementAscend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  ASCEND_EAST(+1, 0) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementAscend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  ASCEND_WEST(-1, 0) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementAscend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DESCEND_NORTH(0, -1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDescend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DESCEND_SOUTH(0, +1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDescend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DESCEND_EAST(+1, 0) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDescend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DESCEND_WEST(-1, 0) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDescend.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DIAGONAL_NORTHEAST(+1, -1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDiagonal.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DIAGONAL_NORTHWEST(-1, -1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDiagonal.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DIAGONAL_SOUTHEAST(+1, +1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDiagonal.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  },

  DIAGONAL_SOUTHWEST(-1, +1) {
    override fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult) {
      MovementDiagonal.calculateCost(ctx, parentX, parentY, parentZ, parentX + offsetX, parentZ + offsetZ, res)
    }
  };

  abstract fun calculate(ctx: CalculationContext, parentX: Int, parentY: Int, parentZ: Int, res: MovementResult)
}