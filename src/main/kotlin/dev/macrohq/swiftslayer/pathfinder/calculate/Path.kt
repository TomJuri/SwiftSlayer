package dev.macrohq.swiftslayer.pathfinder.calculate

import dev.macrohq.swiftslayer.pathfinder.goal.Goal
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.BlockUtil
import dev.macrohq.swiftslayer.util.world
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import java.util.*


class Path(start: PathNode, end: PathNode, val goal: Goal, val ctx: CalculationContext) {
  var start: BlockPos = BlockPos(start.x, start.y, start.z)
  var end: BlockPos = BlockPos(end.x, end.y, end.z)
  var path: List<BlockPos>
  var node: List<PathNode>
  init{
    var temp: PathNode? = end
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

     fun reconstructPath(end: PathNode): List<BlockPos> {
        val path = mutableListOf<BlockPos>()
        var currentNode: PathNode? = end
        while (currentNode != null) {
            path.add(0, currentNode.getBlock())
            currentNode = currentNode.parentNode
        }

        val smooth = mutableListOf<BlockPos>()
        if (path.isNotEmpty()) {
            smooth.add(path[0])
            var currPoint = 0
            var maxiters = 2000

            while (currPoint + 1 < path.size && maxiters-- > 0) {
                var nextPos = currPoint + 1

                for (i in (path.size - 1) downTo nextPos) {
                    if (BlockUtil.blocksBetweenValid(path[currPoint], path[i])) {
                        nextPos = i
                        break
                    }
                }
                smooth.add(path[nextPos])
                currPoint = nextPos
            }
        }
        smooth.removeIf { world.getBlockState(it).block == Blocks.air }
        return smooth
    }

}