package dev.macrohq.swiftslayer.codecPathfinder.Pathfinder


import dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies.BlockNode
import dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies.BlockNodeCompare
import dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies.Utils
import dev.macrohq.swiftslayer.util.BlockUtil
import io.netty.util.internal.ConcurrentSet
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import me.kbrewster.eventbus.Subscribe
import java.util.concurrent.ConcurrentSkipListSet

class Pathfinder {
    var openSet = ConcurrentSkipListSet<BlockNode>(BlockNodeCompare())
    var closedSet: MutableSet<BlockNode> = ConcurrentSet<BlockNode>()

    fun calculatePath(start: Entity, goal: Entity): List<BlockPos>? {
        if(calculatePath(start.position, goal.position) != null) {
            return calculatePath(start.position, goal.position)
        } else {
           return null
        }

    }

    fun calculatePath(start: BlockPos, goal: BlockPos): List<BlockPos>?  {
        return if(calculatePath(BlockNode(start), BlockNode(goal)) != null) {
            calculatePath(BlockNode(start), BlockNode(goal))!!
        } else {
            null
        }
    }

    fun calculatePath(start: BlockNode, goal: BlockNode): List<BlockPos>? {
        //Logger.info(BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer), start.position, goal.position)
       // if(BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer), start.position, goal.position)) return null
        openSet.clear()
        closedSet.clear()

        openSet.add(start.updateValues(goal))

        val startTime = System.currentTimeMillis()
        var currentTime = System.currentTimeMillis()
        while (!openSet.isEmpty() && currentTime - startTime <= 1500) {
            val parentNode: BlockNode = checkNotNull(openSet.pollFirst())
            if (BlockUtil.isNotWalkable(parentNode.position)) {
                currentTime = System.currentTimeMillis()
                continue
            }


            if (parentNode == goal) return Utils.constructPath(parentNode)


            for (childPos in Utils.getChildren(parentNode)) {
                val childNode: BlockNode = BlockNode(childPos).updateParent(parentNode).updateValues(goal)

                if (openSet.contains(childNode)) closedSet.add(childNode)
                else if (closedSet.contains(childNode)) {
                    if (childNode.gCost <= childNode.totalCost()) continue

                    closedSet.remove(childNode)
                    openSet.add(childNode)
                } else openSet.add(childNode)
            }
            currentTime = System.currentTimeMillis()
        }

        return null
    }

    @Subscribe
    fun renderWorld(event: RenderWorldLastEvent?) {
        /*for (BlockNode node : openSet) {
            RenderUtil.drawOutlinedFilledBoundingBox(node.position, Color.CYAN, event.partialTicks);
        }*/
    }
}