package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies.BlockNode
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.pathfinder.movement.MovementHelper
import net.minecraft.block.BlockStairs
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLiving
import net.minecraft.util.*
import kotlin.math.abs
import kotlin.math.sqrt


object BlockUtil {


    fun canWalkOnBlock(pos: BlockPos): Boolean {
        val block = world.getBlockState(pos.add(0, 0, 0)).block
        val blockAbove = world.getBlockState(pos.up()).block

        val material: Material = block.material
        val materialAbove: Material = blockAbove.material

        return material.isSolid && !material.isLiquid && materialAbove == Material.air
    }

    fun calculateDistance(one: BlockPos, two: BlockPos?): Double {
        return sqrt(one.distanceSq(two))
    }

    fun toVec3(pos: BlockPos): Vec3 {
        return Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
    }

    fun isNotWalkable(pos: BlockPos): Boolean {
        val node1 = BlockNode(pos.add(0, -1, 0))
        val node2 = BlockNode(pos)
        val node3 = BlockNode(pos.add(0, 1, 0))

        val node1BS = Minecraft.getMinecraft().theWorld.getBlockState(node1.position)
        val node2BS = Minecraft.getMinecraft().theWorld.getBlockState(node2.position)
        val node3BS = Minecraft.getMinecraft().theWorld.getBlockState(node3.position)

        var node1BB: AxisAlignedBB? = null
        var node2BB: AxisAlignedBB? = null
        var node3BB: AxisAlignedBB? = null

        try {
            node1BB = node1BS.block.getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, node1.position, node1BS)
        } catch (ignored: Exception) {
        }
        try {
            node2BB = node2BS.block.getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, node1.position, node2BS)
        } catch (ignored: Exception) {
        }
        try {
            node3BB = node3BS.block.getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, node1.position, node3BS)
        } catch (ignored: Exception) {
        }

        var allBB = 0.0
        if (node1BB != null) {
            allBB += (node1BB.maxY - node1BB.minY)
        }

        if (node2BB != null) {
            allBB += (node2BB.maxY - node2BB.minY)
        }

        if (node3BB != null) {
            allBB += (node3BB.maxY - node3BB.minY)
        }

        return allBB < 0.2 || allBB > 1
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

            val blocks = getBlocksBetweenCorners(getCornerBlocks(centerPos, width, height, depth).first,getCornerBlocks(centerPos, width, height, depth).second).asSequence()
                .filter { canWalkOnBlock(it.add(0, 0, 0)) }
                .filter { world.isBlockFullCube(it) }
                .toMutableList()

        return blocks
    }

    fun getCornerBlocks(centerBlock: BlockPos, radiusX: Int, radiusY: Int, radiusZ: Int): Pair<BlockPos, BlockPos> {
        val topLeft = BlockPos(centerBlock.x - radiusX, centerBlock.y - radiusY, centerBlock.z - radiusZ)
        val bottomRight = BlockPos(centerBlock.x + radiusX, centerBlock.y + radiusY, centerBlock.z + radiusZ)
        return topLeft to bottomRight
    }

    fun isSingleCorner(pos: BlockPos): Boolean {
        val top = pos.add(1, 0, 0)
        val right = pos.add(0, 0, 1)
        val bottom = pos.add(-1, 0, 0)
        val left = pos.add(0, 0, -1)
        val topAbove = pos.add(1, 1, 0)
        val rightAbove = pos.add(0, 1, 1)
        val bottomAbove = pos.add(-1, 1, 0)
        val leftAbove = pos.add(0, 1, -1)

        if(!isValidBlock(topAbove)) return false
        if(!isValidBlock(bottomAbove)) return false
        if(!isValidBlock(leftAbove)) return false
        if(!isValidBlock(rightAbove)) return false


        val r1 = !canWalkOnBlock(top) && !canWalkOnBlock(right)
        val r2 = !canWalkOnBlock(bottom) && !canWalkOnBlock(right)
        val r3 = !canWalkOnBlock(top) && !canWalkOnBlock(left)
        val r4 = !canWalkOnBlock(bottom) && !canWalkOnBlock(left)

        return (r1.and(r2.not()) .and(r3.not()) .and(r4.not())
            .or(r2.and(r1.not()) .and(r3.not()) .and(r4.not()))
            .or(r3.and(r1.not()) .and(r2.not()) .and(r4.not()))
            .or(r4.and(r1.not()) .and(r2.not()) .and(r3.not())))
    }


    fun isValidBlock(block: BlockPos):Boolean {
        return !(!world.isAirBlock(block) && !world.getBlockState(block).block.isBlockNormalCube)
    }

    fun getXZDistance(pos1: BlockPos, pos2: BlockPos): Double {
        val xDiff = (pos1.x - pos2.x).toDouble()
        val zDiff = (pos1.z - pos2.z).toDouble()
        return sqrt(xDiff * xDiff + zDiff * zDiff)
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

    fun isStairSlab(block: BlockPos): Boolean {
        return world.getBlockState(block).block is BlockStairs ||
                world.getBlockState(block).block is BlockStairs
    }

    fun blocksBetweenValid(ctx: CalculationContext = CalculationContext(SwiftSlayer.instance), startPoss: BlockPos, endPoss: BlockPos): Boolean {
        val blocksBetween = bresenham(ctx, startPoss.toVec3(), endPoss.toVec3())
        if(blocksBetween.isEmpty()){
            return false
        }
        for (i in blocksBetween.indices) {
            val it = blocksBetween[i]
            if (!MovementHelper.canStandOnBlock(ctx.bsa, it.x, it.y, it.z)) {
                return false
            }
            if (i == 0) continue
            val prev = blocksBetween[i - 1]
            if (!canWalkOn(ctx, prev, it)){
                return false
            }
        }
        return true
    }

    fun getDirectionToWalkOnStairs(state: IBlockState): EnumFacing {
        return when (state.block.getMetaFromState(state)) {
            0 -> {
                EnumFacing.EAST
            }
            1 -> {
                EnumFacing.WEST
            }
            2 -> {
                EnumFacing.SOUTH
            }
            3 -> {
                EnumFacing.NORTH
            }
            4 -> {
                EnumFacing.DOWN
            }
            else -> EnumFacing.UP
        }
    }

    fun getPlayerDirectionToBeAbleToWalkOnBlock(startPos: BlockPos, endPoss: BlockPos): EnumFacing {
        val deltaX: Int = endPoss.x - startPos.x
        val deltaZ: Int = endPoss.z - startPos.z

        return if (abs(deltaX) > abs(deltaZ)) {
            if (deltaX > 0) EnumFacing.EAST else EnumFacing.WEST
        } else {
            if (deltaZ > 0) EnumFacing.SOUTH else EnumFacing.NORTH
        }
    }

    fun canWalkOn(ctx: CalculationContext, startPos: BlockPos, endPos: BlockPos): Boolean {
        val startState = world.getBlockState(startPos)
        val endState = world.getBlockState(endPos)
        if (!endState.block.material.isSolid) {
            return endPos.y - startPos.y <= 1
        }
        if (endState.block is BlockStairs && MovementHelper.isValidStair(endState, endPos.x - startPos.x, endPos.z - startPos.z)) {
            return true
        }
        val sourceMaxY = startState.block.getCollisionBoundingBox(ctx.world, startPos, startState)?.maxY ?: startPos.y.toDouble()
        val destMaxY = endState.block.getCollisionBoundingBox(ctx.world, endPos, endState)?.maxY ?: (startPos.y + 1.0)
        return destMaxY - sourceMaxY <= .5
    }

    fun bresenham(ctx: CalculationContext, start: Vec3, end: Vec3): List<BlockPos> {
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
            val pos = BlockPos(x0, y0, z0)
            if (ctx.world.isAirBlock(pos)) {
                val down = pos.down()
                val down2 = pos.add(0, -2, 0)
                if (MovementHelper.canStandOnBlock(ctx.bsa, down.x, down.y, down.z)) {
                    blocks.add(down)
                } else if (MovementHelper.canStandOnBlock(ctx.bsa, down2.x, down2.y, down2.z)) {
                    blocks.add(down2)
                } else return emptyList()
            } else{
                val airUp = world.isAirBlock(pos.up())
                val airUp2 = world.isAirBlock(pos.add(0, 2, 0))
                val airUp3 = world.isAirBlock(pos.add(0, 3, 0))
                if (airUp) {
                    blocks.add(pos)
                } else if (airUp2) {
                    blocks.add(pos.up())
                } else if (airUp3) {
                    blocks.add(pos.add(0, 2, 0))
                } else {
                    return emptyList()
                }
            }
        }
        return blocks
    }
}


