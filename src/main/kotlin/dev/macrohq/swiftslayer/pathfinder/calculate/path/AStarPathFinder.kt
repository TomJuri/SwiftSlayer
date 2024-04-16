package dev.macrohq.swiftslayer.pathfinder.calculate.path

import dev.macrohq.swiftslayer.pathfinder.calculate.Path
import dev.macrohq.swiftslayer.pathfinder.calculate.PathNode
import dev.macrohq.swiftslayer.pathfinder.calculate.openset.BinaryHeapOpenSet
import dev.macrohq.swiftslayer.pathfinder.goal.Goal
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.pathfinder.movement.MovementResult
import dev.macrohq.swiftslayer.pathfinder.movement.Moves
import dev.macrohq.swiftslayer.util.Logger.note
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import scala.tools.nsc.interpreter.JavapClass.PathOps
import java.util.Scanner

class AStarPathFinder(val startX: Int, val startY: Int, val startZ: Int, val goal: Goal, val ctx: CalculationContext) {
  val closedSet: Long2ObjectMap<PathNode> = Long2ObjectOpenHashMap()

  fun calculatePath(): Path? {
    val openSet = BinaryHeapOpenSet()
    val startNode = PathNode(startX, startY, startZ, goal)
    val res = MovementResult()
    val st = System.currentTimeMillis()
    val end = System.currentTimeMillis() + 20
    var nodesConsidered = 0
    val moves = Moves.values()
    startNode.costSoFar = 0.0
    startNode.totalCost = startNode.costToEnd
    openSet.add(startNode)

    while (!openSet.isEmpty()) {
      if (end - st <= 0) break
      val currentNode = openSet.poll()
      nodesConsidered++

      if (goal.isAtGoal(currentNode.x, currentNode.y, currentNode.z)) {
        return Path(startNode, currentNode, goal, ctx)
      }

      for (move in moves) {
        res.reset()
        move.calculate(ctx, currentNode.x, currentNode.y, currentNode.z, res)
        val cost = res.cost
        if (cost >= ctx.cost.INF_COST) continue
        val neighbourNode = getNode(res.x, res.y, res.z, PathNode.longHash(res.x, res.y, res.z))
        val neighbourCostSoFar = currentNode.costSoFar + cost

        if (neighbourNode.costSoFar > neighbourCostSoFar) {
          neighbourNode.parentNode = currentNode
          neighbourNode.costSoFar = neighbourCostSoFar
          neighbourNode.totalCost = neighbourCostSoFar + neighbourNode.costToEnd

          if (neighbourNode.heapPosition == -1) {
            openSet.add(neighbourNode)
          } else {
            openSet.relocate(neighbourNode)
          }
        }
      }
    }
    return null
  }

  fun getNode(x: Int, y: Int, z: Int, hash: Long): PathNode {
    var n: PathNode? = closedSet.get(hash)
    if (n == null) {
      n = PathNode(x, y, z, goal)
      closedSet.put(hash, n)
    }
    return n
  }
}