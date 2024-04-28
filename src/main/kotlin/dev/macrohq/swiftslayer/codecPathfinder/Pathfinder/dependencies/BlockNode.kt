package dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies

import dev.macrohq.swiftslayer.util.BlockUtil
import net.minecraft.util.BlockPos

class BlockNode(var position: BlockPos) {
    var parent: BlockNode? = null
    var gCost: Double = 0.0
    var hCost: Double = 0.0

    override fun equals(other: Any?): Boolean {
        return this.position == (other as BlockNode?)!!.position
    }

    fun totalCost(): Double {
        return hCost + gCost
    }

    fun updateValues(goal: BlockNode): BlockNode {
        var parent = this.parent
        this.gCost = if (parent != null) parent.gCost + 1 else 0.0
        this.hCost = BlockUtil.calculateDistance(position, goal.position)

        return this
    }

    fun updateParent(parent: BlockNode?): BlockNode {
        this.parent = parent
        return this
    }
}