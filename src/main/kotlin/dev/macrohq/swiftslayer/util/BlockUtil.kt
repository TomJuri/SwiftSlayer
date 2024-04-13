package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.feature.implementation.BossKillerMovement
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import net.minecraft.block.Block
import net.minecraft.block.BlockStairs
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3


object BlockUtil {


    fun canWalkOnBlock(pos: BlockPos): Boolean {
        val block = world.getBlockState(pos.add(0, 0, 0)).block
        val blockAbove = world.getBlockState(pos.up()).block // Get the block above

        val material: Material = block.material
        val materialAbove: Material = blockAbove.material

        // Check if both the block and the space above it are walkable
        return material.isSolid && !material.isLiquid && materialAbove == Material.air
    }


    fun getBlocksBetweenCorners(topLeft: BlockPos, bottomRight: BlockPos): List<BlockPos> {
        val blocksBetween = mutableListOf<BlockPos>()

        for (x in topLeft.x..bottomRight.x) {
            for (y in topLeft.y..bottomRight.y) {
                for (z in topLeft.z..bottomRight.z) {
                    val blockPos = BlockPos(x, y, z)
                    blocksBetween.add(blockPos)
                }
            }
        }

        return blocksBetween
    }

    fun getBlocks(centerPos: BlockPos, width: Int, height: Int, depth: Int, entity: EntityLiving? = null): MutableList<BlockPos> {
        var pos2: BlockPos

            val blocks = getBlocksBetweenCorners(getCornerBlocks(centerPos,width, height, depth).first,getCornerBlocks(centerPos,width, height, depth).second).asSequence()
                .filter { canWalkOnBlock(it.add(0, -1, 0)) }
                .filter { blocksBetweenValid(mc.thePlayer.position, it) }
                .filter { BossKillerMovement.getInstance().getDistanceBetweenBlocks(it, mc.thePlayer.position) > 1}
                .toMutableList()
        if(entity != null) {
            blocks.filter { BossKillerMovement.getInstance().getDistanceBetweenBlocks(it, mc.thePlayer.position) >  BossKillerMovement.getInstance().getDistanceBetweenBlocks(it, entity.position)}
        } else {
            blocks.filter { BossKillerMovement.getInstance().getDistanceBetweenBlocks(it, mc.thePlayer.position) > 1}
        }
        return blocks
    }

    
    fun getCornerBlocks(centerBlock: BlockPos, radiusX: Int, radiusY: Int, radiusZ: Int): Pair<BlockPos, BlockPos> {
        val topLeft = BlockPos(centerBlock.x - radiusX, centerBlock.y - radiusY, centerBlock.z - radiusZ)
        val bottomRight = BlockPos(centerBlock.x + radiusX, centerBlock.y + radiusY, centerBlock.z + radiusZ)
        return topLeft to bottomRight
    }

    fun neighbourGenerator(mainBlock: BlockPos, size: Int): List<BlockPos> {
        return neighbourGenerator(mainBlock, size, size, size)
    }

    fun neighbourGenerator(mainBlock: BlockPos, xD: Int, yD: Int, zD: Int): List<BlockPos>{
        return neighbourGenerator(mainBlock, -xD, xD, -yD, yD, -zD, zD)
    }

    fun neighbourGenerator(mainBlock: BlockPos, xD1: Int, xD2: Int, yD1: Int, yD2: Int, zD1: Int, zD2: Int): List<BlockPos> {
        val neighbours: MutableList<BlockPos> = ArrayList()
        for (x in xD1..xD2) {
            for (y in yD1..yD2) {
                for (z in zD1..zD2) {
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

    fun blocksBetweenValid(startPos: BlockPos, endPos: BlockPos): Boolean{
        val blocks = bresenham(startPos.toVec3().addVector(0.0,0.4,0.0), endPos.toVec3().addVector(0.0,0.4,0.0)).toMutableList()
        var blockFail = 0
        var lastBlockY = blocks[0].y
        var lastFullBlock = world.isBlockFullCube(blocks[0])
        var isLastBlockSlab = isStairSlab(blocks[0])
        var isLastBlockAir = world.isAirBlock(blocks[0])
        blocks.remove(blocks[0])
        blocks.forEach{
            if(!AStarPathfinder.Node(it, null).isWalkable() && !world.isAirBlock(it)){
                return false
            }
//            if(!(isLastBlockSlab && world.isBlockFullCube(it))) return false
            if(isLastBlockAir && world.isBlockFullCube(it) && !isStairSlab(it)) return false
//            if(!(isLastBlockAir && isStairSlab(it))) return false
            if(lastFullBlock && world.isBlockFullCube(it) &&  it.y > lastBlockY) return false
            if(world.isAirBlock(it)) blockFail++
            else blockFail=0
            if(blockFail>3) return false

            lastBlockY = it.y
            lastFullBlock = world.isBlockFullCube(it)
            isLastBlockSlab = isStairSlab(it)
            isLastBlockAir = world.isAirBlock(it)
        }
        return true
//        blocks.forEach{
//            if(!AStarPathfinder.Node(it, null).isWalkable() && !world.isAirBlock(it)){
//                return false
//            }
//            if(world.isAirBlock(it)){
//                blockFail++
//            }
//            if(world.isBlockFullCube(it) && blockFail>0) return false
//            if(AStarPathfinder.Node(it, null).isWalkable()){
//                blockFail = 0
//            }
//            if(blockFail>5){
//                return false
//            }
//        }
//        return true
    }

    fun bresenham(start: Vec3, end: Vec3): List<BlockPos> {
        var start0 = start
        val blocks = mutableListOf(start0.toBlockPos())
        val x1 = MathHelper.floor_double(end.xCoord)
        val y1 = MathHelper.floor_double(end.yCoord)
        val z1 = MathHelper.floor_double(end.zCoord)
        var x0 = MathHelper.floor_double(start0.xCoord)
        var y0 = MathHelper.floor_double(start0.yCoord)
        var z0 = MathHelper.floor_double(start0.zCoord)

        var iterations = 200
        while (iterations-- >= 0) {
            if (x0 == x1 && y0 == y1 && z0 == z1) {
                blocks.add(end.toBlockPos())
                return blocks
            }
            var hasNewX = true
            var hasNewY = true
            var hasNewZ = true
            var newX = 999.0
            var newY = 999.0
            var newZ = 999.0
            if (x1 > x0) {
                newX = x0.toDouble() + 1.0
            } else if (x1 < x0) {
                newX = x0.toDouble() + 0.0
            } else {
                hasNewX = false
            }
            if (y1 > y0) {
                newY = y0.toDouble() + 1.0
            } else if (y1 < y0) {
                newY = y0.toDouble() + 0.0
            } else {
                hasNewY = false
            }
            if (z1 > z0) {
                newZ = z0.toDouble() + 1.0
            } else if (z1 < z0) {
                newZ = z0.toDouble() + 0.0
            } else {
                hasNewZ = false
            }
            var stepX = 999.0
            var stepY = 999.0
            var stepZ = 999.0
            val dx = end.xCoord - start0.xCoord
            val dy = end.yCoord - start0.yCoord
            val dz = end.zCoord - start0.zCoord
            if (hasNewX) stepX = (newX - start0.xCoord) / dx
            if (hasNewY) stepY = (newY - start0.yCoord) / dy
            if (hasNewZ) stepZ = (newZ - start0.zCoord) / dz
            if (stepX == -0.0) stepX = -1.0E-4
            if (stepY == -0.0) stepY = -1.0E-4
            if (stepZ == -0.0) stepZ = -1.0E-4
            var enumfacing: EnumFacing
            if (stepX < stepY && stepX < stepZ) {
                enumfacing = if (x1 > x0) EnumFacing.WEST else EnumFacing.EAST
                start0 = Vec3(newX, start0.yCoord + dy * stepX, start0.zCoord + dz * stepX)
            } else if (stepY < stepZ) {
                enumfacing = if (y1 > y0) EnumFacing.DOWN else EnumFacing.UP
                start0 = Vec3(start0.xCoord + dx * stepY, newY, start0.zCoord + dz * stepY)
            } else {
                enumfacing = if (z1 > z0) EnumFacing.NORTH else EnumFacing.SOUTH
                start0 = Vec3(start0.xCoord + dx * stepZ, start0.yCoord + dy * stepZ, newZ)
            }
            x0 = MathHelper.floor_double(start0.xCoord) - if (enumfacing == EnumFacing.EAST) 1 else 0
            y0 = MathHelper.floor_double(start0.yCoord) - if (enumfacing == EnumFacing.UP) 1 else 0
            z0 = MathHelper.floor_double(start0.zCoord) - if (enumfacing == EnumFacing.SOUTH) 1 else 0
            blocks.add(BlockPos(x0, y0, z0))
        }
        return blocks
    }

}
