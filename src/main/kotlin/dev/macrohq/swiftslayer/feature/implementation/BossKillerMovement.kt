package dev.macrohq.swiftslayer.feature.implementation

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.AbstractFeature
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.BlockUtil
import dev.macrohq.swiftslayer.util.Logger
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class BossKillerMovement: AbstractFeature() {
    override val featureName: String = "Slayer Boss Movement"
    override val isPassiveFeature: Boolean = false
    private var isOverriden = false
    var mc: Minecraft = Minecraft.getMinecraft()

    var random: Random = Random()
    companion object{
        private var instance: BossKillerMovement? = null
        fun getInstance(): BossKillerMovement{
            if(instance == null){
                instance = BossKillerMovement()
            }
            return instance!!
        }
    }

    override fun disable() {
        TODO("Not yet implemented")
    }

    override fun canEnable(): Boolean {
        return this.enabled
    }

    fun getPlayerLookingDirection(): Direction {
        var yaw = (mc.thePlayer.rotationYaw + 360) % 360
        if (yaw > 180) {
            yaw -= 360f
        }

        return if (yaw >= -135 && yaw <= -45) {
            Direction.East
        } else if(yaw >= -45 && yaw <= 45) {
            Direction.South
        } else if(yaw in 45.0..135.0) {
            Direction.West
        } else {
            Direction.North

        }

   }

    fun getInverseDirection(direction: Direction): Direction {
        return when (direction) {
            Direction.West -> Direction.East
            Direction.East -> Direction.West
            Direction.North -> Direction.South
            Direction.South -> Direction.North
        }
    }

    private fun getRandomX(loc: BlockPos, size: Int): Int {
        val minX = loc.x as Int - size
        val maxX = loc.x as Int + size

        return random.nextInt( (maxX - minX)) + minX;
    }

    private fun getRandomZ(loc: BlockPos, size: Int): Int {
        val minZ = loc.z as Int - size
        val maxZ = loc.z as Int + size

        return random.nextInt((maxZ - minZ)) + minZ
    }

     fun getRandomBlock(loc: BlockPos, width: Int ): BlockPos {
        return BlockPos(getRandomX(loc, width), loc.y, getRandomZ(loc, width))
    }

    fun getDistanceBetweenBlocks(blockPos1: BlockPos, blockPos2: BlockPos): Double {
        val deltaX: Double = abs(blockPos1.x.toDouble() - blockPos2.x.toDouble())
        val deltaY: Double = abs(blockPos1.y.toDouble() - blockPos2.y.toDouble())
        val deltaZ: Double = abs(blockPos1.z.toDouble() - blockPos2.z.toDouble())

        return sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ))

    }

    fun getValidBlock(loc: BlockPos, width: Int, iterations: Int): BlockPos? {
            var block: BlockPos
        for (i in 0 until iterations) {
            block = BlockPos(getRandomX(loc, width), loc.y, getRandomZ(loc, width))
            if(getDistanceBetweenBlocks(mc.thePlayer.position, block) > getDistanceBetweenBlocks(loc,block)) {

                continue
            } else {
                if(BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), block, mc.thePlayer.position)) {
                    return block

                } else {
                    continue
                }

            }
        }
        Logger.info("couldnt find a block in the iterations")
        return null
    }


    enum class Direction {
        North, East, South, West,
    }



}