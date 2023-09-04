package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.*
import net.minecraft.block.BlockStairs
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
        startNode.calculateCost(endNode)
        openNodes.add(startNode)
        for (i in 0 until iterations) {
            val currentNode = openNodes.stream().min(Comparator.comparingDouble { it.getFCost().toDouble() }).orElse(null) ?: return listOf()
            if (currentNode.position == endNode.position) return reconstructPath(currentNode)
            openNodes.remove(currentNode)
            closedNodes.add(currentNode)
            for (node in currentNode.getNeighbours()) {
                node.calculateCost(endNode)
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

//        return path

        val smooth = mutableListOf<BlockPos>()
        if(path.isNotEmpty()){
            smooth.add(path[0])
            var currPoint = 0
            var maxiters = 2000

            while(currPoint+1 < path.size && maxiters-->0){
                var nextPos = currPoint + 1

                for(i in (path.size-1) downTo nextPos){
                    if(BlockUtil.blocksBetweenValid(path[currPoint], path[i])){
                        nextPos = i
                        break
                    }
                }
                smooth.add(path[nextPos])
                currPoint = nextPos
            }
        }
        return smooth
    }

    class Node(val position: BlockPos, val parent: Node?) {
        private var gCost = Float.MAX_VALUE
        private var hCost = Float.MAX_VALUE
        private var yaw = 0f

        private fun calculateYaw(): Float {
            var yaw = 0f
            if (parent != null) {
                val dX = parent.position.x - position.x
                val dZ = parent.position.z - position.z
                yaw = MathHelper.wrapAngleTo180_float(-Math.toDegrees(atan2(dX.toDouble(), dZ.toDouble())).toFloat())
            }
            return yaw
        }
        private fun angleCost(bp1: BlockPos, bp2: BlockPos): Float {
            val dx = bp2.x - bp1.x
            val dz = bp2.z - bp1.z
            val yaw = -Math.toDegrees(atan2(dx.toDouble(), dz.toDouble())).toFloat()
            return AngleUtil.yawTo360(yaw);
        }
        fun calculateCost(endNode: Node) {
            var cost = 0f
            if (this.parent != null) {
                this.yaw = angleCost(this.parent.position, this.position)
                cost += if (this.parent.parent == null) 0f
                else AngleUtil.yawTo360(abs(this.yaw - this.parent.yaw)) / 360

                if (this.parent.position.y < this.position.y
                    && !BlockUtil.isStairSlab(this.position)
                ) {
                    cost += 1.5f;
                }
            }
            BlockUtil.neighbourGenerator(this.position.up().up().up(), 1).forEach{
                if(world.isBlockFullCube(it)) cost += 1f
            }
            this.gCost = if (this.parent != null) sqrt(this.parent.position.distanceSq(this.position)).toFloat()
            else 0f
            this.gCost += cost
            this.hCost = sqrt(endNode.position.distanceSq(this.position)).toFloat()
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

        fun isWalkable(): Boolean {
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
                    && !collision;
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
            Blocks.golden_rail,
        )
    }
}