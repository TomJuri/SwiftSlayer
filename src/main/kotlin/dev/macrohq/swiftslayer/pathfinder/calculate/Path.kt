package dev.macrohq.swiftslayer.pathfinder.calculate

import dev.macrohq.swiftslayer.pathfinder.goal.Goal
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.Logger.note
import net.minecraft.util.BlockPos
import java.util.*


class Path(start: PathNode, end: PathNode, val goal: Goal, val ctx: CalculationContext) {
  var start: BlockPos = BlockPos(start.x, start.y, start.z)
  var end: BlockPos = BlockPos(end.x, end.y, end.z)
  var path: List<BlockPos>
  var node: List<PathNode>
  init{
    var temp: PathNode? = end;
    val listOfBlocks = LinkedList<BlockPos>()
    val listOfNodes = LinkedList<PathNode>()
    while (temp != null){
      listOfNodes.addFirst(temp)
      listOfBlocks.addFirst(BlockPos(temp.x, temp.y, temp.z))
      temp = temp.parentNode
    }
    path = listOfBlocks.toList()
    node = listOfNodes.toList()
  }
}