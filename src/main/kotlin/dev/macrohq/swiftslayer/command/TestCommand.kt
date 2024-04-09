package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import dev.macrohq.swiftslayer.SwiftSlayer.Companion.instance
import dev.macrohq.swiftslayer.pathfinder.calculate.Path
import dev.macrohq.swiftslayer.pathfinder.calculate.PathNode
import dev.macrohq.swiftslayer.pathfinder.calculate.path.AStarPathFinder
import dev.macrohq.swiftslayer.pathfinder.goal.Goal
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.pathfinder.movement.MovementResult
import dev.macrohq.swiftslayer.pathfinder.movement.Moves
import dev.macrohq.swiftslayer.pathfinder.movement.movements.MovementAscend
import dev.macrohq.swiftslayer.pathfinder.movement.movements.MovementDescend
import dev.macrohq.swiftslayer.pathfinder.movement.movements.MovementDiagonal
import dev.macrohq.swiftslayer.pathfinder.movement.movements.MovementTraverse
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.note

@Command("test", aliases = ["set"])
class TestCommand {
  private var rotationTargetBlock: BlockPos? = null
  private var ns = mutableListOf<BlockPos>()

  @SubCommand
  private fun pf(a: String) {
    if (a == "end") {
      rotationTargetBlock = instance.playerContext.playerPosition
      return
    }

    if (rotationTargetBlock == null) return
    val ctx = CalculationContext(instance)
    val goal = Goal(rotationTargetBlock!!.x, rotationTargetBlock!!.y, rotationTargetBlock!!.z, ctx)
    val start = instance.playerContext.playerPosition
    val pathfinder = AStarPathFinder(start.x, start.y, start.z, goal, ctx)
    var path: Path? = null
    runAsync {
      path = pathfinder.calculatePath()
      if (path == null) {
        note("No path found")
      } else {
        note("Size: ${path!!.path.size}")
        ns.addAll(path!!.path)
      }
    }
//    note("NeighbourSize: ${niggas.size}")
  }

  fun getNode(x: Int, y: Int, z: Int, goal: Goal, hash: Long): PathNode {
    val n = PathNode(x, y, z, goal)
    return n
  }

  @SubCommand
  private fun neighbour() {
    val ctx = CalculationContext(instance)
    val start = instance.playerContext.playerPosition
    val goal = Goal(rotationTargetBlock!!.x, rotationTargetBlock!!.y, rotationTargetBlock!!.z, ctx)
    val currentNode = PathNode(start.x, start.y, start.z, goal)
    currentNode.costSoFar = 0.0
    currentNode.totalCost = currentNode.costToEnd
    val res = MovementResult()

    val list = mutableListOf<PathNode>()

    for (move in Moves.entries) {
      res.reset()
      move.calculate(ctx, currentNode.x, currentNode.y, currentNode.z, res)
      val cost = res.cost

      if (cost >= ctx.cost.INF_COST) continue
      val neighbourNode = getNode(res.x, res.y, res.z, goal, PathNode.longHash(res.x, res.y, res.z))
      val neighbourCostSoFar = currentNode.costSoFar + cost

      if (neighbourNode.costSoFar > neighbourCostSoFar) {
        neighbourNode.parentNode = currentNode
        neighbourNode.costSoFar = neighbourCostSoFar
        neighbourNode.totalCost = neighbourCostSoFar + neighbourNode.costToEnd

        note("$neighbourNode, parentGCost: ${currentNode.costSoFar}, gCost: $neighbourCostSoFar")

        if (neighbourNode.heapPosition == -1) {
          list.add(neighbourNode)
        }
      }
    }

    list.forEach { ns.add(it.getBlock()); }
    note("Size: ${list.size}")
  }

  @Main
  private fun main() {
//    val move = MovementDiagonal(instance, instance.playerContext.playerPosition, instance.playerContext.playerPosition.add(1, 0, 1))
//    val ctx = CalculationContext(instance)
//    val res = MovementResult()
//    move.getCost(ctx, res)
//    note("Cost: ${res.cost}")
//    rotationTargetBlock = res.getDest()
////    rotationTargetBlock = instance.playerContext.playerPosition.add(0, -1, 1)
//    note("Dest: ${res.getDest()}")
    val moves = Moves.values()
    val move = moves[12]
    val ctx = CalculationContext(instance)
    val res = MovementResult()
    move.calculate(
      ctx,
      instance.playerContext.playerPosition.x,
      instance.playerContext.playerPosition.y,
      instance.playerContext.playerPosition.z,
      res
    )
    note("Cost: ${res.cost}")
    rotationTargetBlock = res.getDest()
//    rotationTargetBlock = instance.playerContext.playerPosition.add(0, -1, 1)
    note("Dest: ${res.getDest()}")
  }

  @SubCommand
  private fun rotate(lock: Boolean = false) {
    if (rotationTargetBlock == null) return
    if (!AutoRotation.getInstance().enabled) {
      AutoRotation.getInstance()
        .easeTo(Target(rotationTargetBlock!!), 500, if (lock) LockType.SMOOTH else LockType.NONE, 500)
    } else {
      AutoRotation.getInstance().disable();
    }
  }

  @SubCommand(aliases = ["srt"])
  private fun setRotationTarget() {
    rotationTargetBlock = player.getStandingOnCeil()
  }

  @SubCommand
  private fun clear() {
    rotationTargetBlock = null
    ns.clear()
  }

  @SubscribeEvent
  fun onRender(event: RenderWorldLastEvent) {
    if (rotationTargetBlock != null) {
      RenderUtil.drawBox(event, rotationTargetBlock!!, Color.GREEN, true)
    }

    if (ns.size > 0) {
      ns.forEach {
        RenderUtil.drawBox(event, it, Color.CYAN, true)
      }
    }
  }
}