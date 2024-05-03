package dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies

import dev.macrohq.swiftslayer.util.BlockUtil
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos

@Suppress("unused")
object Utils {
    fun isEqual(one: BlockNode, two: BlockNode): Boolean {
        return one.position.distanceSq(two.position) < 1
    }

    fun constructPath(node: BlockNode?): List<BlockPos> {
        var currentNode = node
        var path: MutableList<BlockPos> = ArrayList<BlockPos>().toMutableList()
        while (currentNode != null) {
            path = (listOf(currentNode.position.add(0,-1, 0)) + path) as MutableList<BlockPos>
            currentNode = currentNode.parent
        }

        return path
    }

    fun getChildren(node: BlockNode): List<BlockPos> {
        val locations: MutableList<BlockPos> = ArrayList()

        for (xOffset in -1..1) {
            for (yOffset in -1..1) {
                for (zOffset in -1..1) {
                    if (xOffset != 0 || yOffset != 0 || zOffset != 0) {
                        val newPos = node.position.add(xOffset, yOffset, zOffset)
                        if (Minecraft.getMinecraft().theWorld.rayTraceBlocks(
                                BlockUtil.toVec3(newPos.add(0, 1, 0)),
                                BlockUtil.toVec3(node.position.add(0, 1, 0))
                            ) == null
                        ) {
                            locations.add(newPos)
                        }
                    }
                }
            }
        }

        return locations
    }
}