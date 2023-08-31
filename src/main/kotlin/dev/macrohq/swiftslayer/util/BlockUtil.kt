package dev.macrohq.swiftslayer.util

import net.minecraft.block.BlockStairs
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

object BlockUtil {
    fun neighbourGenerator(mainBlock: BlockPos, size: Int): List<BlockPos> {
        return neighbourGenerator(mainBlock, size, size, size)
    }

    private fun neighbourGenerator(mainBlock: BlockPos, xD: Int, yD: Int, zD: Int): List<BlockPos> {
        val neighbours: MutableList<BlockPos> = ArrayList()
        for (x in -xD..xD) {
            for (y in -yD..yD) {
                for (z in -zD..zD) {
                    neighbours.add(BlockPos(mainBlock.x + x, mainBlock.y + y, mainBlock.z + z))
                }
            }
        }
        return neighbours
    }

    fun isStairSlab(block: BlockPos): Boolean{
        return world.getBlockState(block).block is BlockStairs ||
                world.getBlockState(block).block is BlockStairs
    }
}
