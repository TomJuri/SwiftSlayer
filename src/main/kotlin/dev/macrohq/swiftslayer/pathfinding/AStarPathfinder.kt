package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.BlockUtil
import dev.macrohq.swiftslayer.util.world
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class AStarPathfinder(startPos: BlockPos, endPos: BlockPos) {
    private var startNode: Node
    private var endNode: Node
    private val openNodes = mutableListOf<Node>()
    private val closedNodes= mutableListOf<Node>()

    init {
        startNode = Node(startPos, null)
        endNode = Node(endPos, null)
    }

    fun findPath(iterations: Int): List<BlockPos> {
        startNode.calculateCost(startNode, endNode)
        openNodes.add(startNode)
        for (i in 0 until iterations) {
            val currentNode = openNodes.stream().min(Comparator.comparingDouble { it.getFCost().toDouble() }).orElse(null) ?: return listOf()
            if (currentNode.position == endNode.position) return reconstructPath(currentNode)
            openNodes.remove(currentNode)
            closedNodes.add(currentNode)
            for (node in currentNode.getNeighbours()) {
                node.calculateCost(startNode, endNode)
                if (!node.isIn(openNodes) && !node.isIn(closedNodes)) openNodes.add(node)
            }
        }
        return listOf()
    }

    private fun reconstructPath(end: Node): List<BlockPos> {
        val path = mutableListOf<BlockPos>()
        var currentNode: Node? = end
        while (currentNode != null) {
            path.add(0, currentNode.position)
            currentNode = currentNode.parent
        }
        return path
    }

    public class Node(val position: BlockPos, val parent: Node?) {
        private var gCost = Float.MAX_VALUE
        private var hCost = Float.MAX_VALUE

        fun calculateYaw(): Float {
            var yaw = 0f
            if (parent != null) {
                val dX = parent.position.x - position.x
                val dZ = parent.position.z - position.z
                yaw = MathHelper.wrapAngleTo180_float(-Math.toDegrees(atan2(dX.toDouble(), dZ.toDouble())).toFloat())
            }
            return yaw
        }

        fun calculateCost(start: Node, end: Node) {
            var f = 0f
            if(parent != null) f = (abs((calculateYaw() - parent.calculateYaw()).toDouble()) / 360).toFloat()
            val cost = 2 * f
            gCost = sqrt(start.position.distanceSq(position)).toFloat() + cost
            hCost = sqrt(position.distanceSq(end.position)).toFloat()
        }

        fun getFCost() = gCost + hCost

        fun getNeighbours() : List<Node> {
            val neighbours = mutableListOf<Node>()
            BlockUtil.neighbourGenerator(position, 1).forEach {
                val newNode = Node(it, this)
                if (newNode.isWalkable()) neighbours.add(newNode)
            }
            return neighbours
        }

        private fun isWalkable(): Boolean {
            var collision = false
            if (parent != null && parent.position.x != position.x && parent.position.z != position.z) {
                collision = !(world.isAirBlock(position.add(1, 1, 0))
                        && world.isAirBlock(position.add(-1, 1, 0))
                        && world.isAirBlock(position.add(0, 1, 1))
                        && world.isAirBlock(position.add(0, 1, -1)))
            }
            return allowedBlocks.contains(world.getBlockState(position.up()).block)
                    && allowedBlocks.contains(world.getBlockState(position.up().up()).block)
                    && world.getBlockState(position).block.material.isSolid
                    && !collision
        }

        fun isIn(nodes: List<Node>): Boolean {
            return nodes.stream().anyMatch { node: Node -> position == node.position }
        }

        private val allowedBlocks = listOf(
            Blocks.air,
            Blocks.tallgrass,
            Blocks.double_plant,
            Blocks.yellow_flower,
            Blocks.red_flower,
            Blocks.vine,
            Blocks.redstone_wire,
            Blocks.snow_layer,
            Blocks.cocoa,
            Blocks.end_portal,
            Blocks.tripwire,
            Blocks.web,
            Blocks.leaves,
            Blocks.flower_pot,
            Blocks.wooden_pressure_plate,
            Blocks.stone_pressure_plate,
            Blocks.redstone_torch,
            Blocks.lever,
            Blocks.stone_button,
            Blocks.wooden_button,
            Blocks.carpet,
            Blocks.standing_sign,
            Blocks.wall_sign,
            Blocks.rail,
            Blocks.detector_rail,
            Blocks.activator_rail,
            Blocks.golden_rail
        )
    }
}