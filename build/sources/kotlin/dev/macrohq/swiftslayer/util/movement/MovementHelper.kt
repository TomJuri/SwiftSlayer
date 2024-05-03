package dev.macrohq.swiftslayer.util.movement

import dev.macrohq.swiftslayer.util.movement.helper.BlockStateAccessor
import net.minecraft.block.*
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing

object MovementHelper {

  fun canWalkThrough(bsa: BlockStateAccessor, x: Int, y: Int, z: Int): Boolean {
    return canWalkThroughBlockState(bsa, x, y, z, bsa.get(x, y, z))
  }

  fun canWalkThroughBlockState(bsa: BlockStateAccessor, x: Int, y: Int, z: Int, state: IBlockState): Boolean {
    val block = state.block
    return when {
      block is BlockAir -> true
      block is BlockBush || block is BlockTorch -> true
      block == Blocks.fire || block == Blocks.web || block == Blocks.tripwire || block == Blocks.end_portal || block == Blocks.cocoa || block is BlockSkull || block is BlockSlab || block is BlockTrapDoor -> false
      block is BlockCarpet || block == Blocks.waterlily -> true
      block is BlockSnow -> state.getValue(BlockSnow.LAYERS) < 3
      block == Blocks.water -> bsa.get(x, y + 1, z).block != Blocks.waterlily
      block == Blocks.flowing_water -> false
      else -> block.isPassable(null, null)
    }
  }

  fun canStandOnBlock(bsa: BlockStateAccessor, x: Int, y: Int, z: Int): Boolean {
    return canStandOnBlockState(bsa, x, y, z, bsa.get(x, y, z))
  }

  fun canStandOnBlockState(bsa: BlockStateAccessor, x: Int, y: Int, z: Int, state: IBlockState): Boolean {
    val block = state.block
    return when {
      block is BlockAir -> false
      block is BlockSnow -> true
      block.isBlockNormalCube -> true
      block == Blocks.ladder -> true
      block == Blocks.farmland -> true
      block == Blocks.ender_chest || block == Blocks.chest || block == Blocks.trapped_chest -> true
      block == Blocks.glass || block is BlockStainedGlass -> true
      block is BlockStairs -> true
      block is BlockLiquid -> {
        if(block == Blocks.lava || block == Blocks.flowing_lava || block == Blocks.flowing_water) return false
        return bsa.get(x,y + 1, z).block == Blocks.waterlily
      }
      else -> block is BlockSlab
    }
  }

  fun isWotah(state: IBlockState): Boolean {
    val block = state.block
    return block == Blocks.water || block == Blocks.flowing_water
  }

  fun isLava(state: IBlockState): Boolean {
    val block = state.block
    return block == Blocks.lava || block == Blocks.flowing_lava
  }

  fun isBottomSlab(state: IBlockState): Boolean {
    return state.block is BlockSlab && !(state.block as BlockSlab).isDouble && state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM
  }

  fun isValidStair(state: IBlockState, dx: Int, dz: Int): Boolean {
    if (dx == 0 && dz == 0) return false
    if (state.block !is BlockStairs) return false
    if (state.getValue(BlockStairs.HALF) != BlockStairs.EnumHalf.BOTTOM) return false

    val stairFacing = state.getValue(BlockStairs.FACING)

    return when {
      dz == -1 -> stairFacing == EnumFacing.NORTH
      dz == 1 -> stairFacing == EnumFacing.SOUTH
      dx == -1 -> stairFacing == EnumFacing.WEST
      dx == 1 -> stairFacing == EnumFacing.EAST
      else -> false
    }
  }

  fun hasTop(state: IBlockState, dX: Int, dZ: Int): Boolean {
    return !(isBottomSlab(state) || isValidStair(state, dX, dZ))
  }

  fun avoidWalkingInto(state: IBlockState): Boolean {
    val block = state.block
    return block is BlockLiquid || block is BlockFire || block == Blocks.cactus || block == Blocks.end_portal || block == Blocks.web
  }

  fun getFacing(dx: Int, dz: Int): EnumFacing {
    return if (dx == 0 && dz == 0) EnumFacing.UP else EnumFacing.HORIZONTALS[Math.abs(dx) * (2 + dx) + Math.abs(dz) * (1 - dz)]
  }

  fun isLadder(state: IBlockState): Boolean {
    return state.block == Blocks.ladder
  }

  fun canWalkIntoLadder(ladderState: IBlockState, dx: Int, dz: Int): Boolean {
    return isLadder(ladderState) && ladderState.getValue(BlockLadder.FACING) != getFacing(dx, dz)
  }
}
