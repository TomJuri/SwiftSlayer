package dev.macrohq.swiftslayer.pathfinder.goal

import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

class Goal(val goalX: Int, val goalY: Int, val goalZ: Int, val ctx: CalculationContext) : IGoal {
  private val SQRT_2 = sqrt(2.0)

  override fun isAtGoal(x: Int, y: Int, z: Int): Boolean {
    return goalX == x && goalY == y && goalZ == z
  }

  override fun heuristic(x: Int, y: Int, z: Int): Double {
    val dx = abs(goalX - x)
    val dz = abs(goalZ - z)
    val straight = abs(dx - dz).toDouble()
    var vertical = abs(goalY - y).toDouble()
    val diagonal = min(dx, dz).toDouble()

    vertical *= if (goalY > y) { ctx.cost.JUMP_ONE_BLOCK_COST }
                else { ctx.cost.N_BLOCK_FALL_COST[2] / 2.0 }

    return (straight + diagonal * SQRT_2) * ctx.cost.ONE_BLOCK_SPRINT_COST + vertical
  }
}