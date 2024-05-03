package dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies

class BlockNodeCompare : Comparator<BlockNode?> {
    override fun compare(o1: BlockNode?, o2: BlockNode?): Int {
        val totalCostComparison = java.lang.Double.compare(o1!!.totalCost(), o2!!.totalCost())
        val hCostComparison = java.lang.Double.compare(o1.hCost, o2.hCost)

        if (totalCostComparison < 0 && hCostComparison < 0) {
            return -1
        }

        return java.lang.Double.compare(o1.totalCost() + o1.hCost, o2.totalCost() + o2.hCost)
    }
}