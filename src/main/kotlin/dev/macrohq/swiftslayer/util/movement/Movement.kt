package dev.macrohq.swiftslayer.util.movement

import dev.macrohq.swiftslayer.SwiftSlayer
import net.minecraft.util.BlockPos

abstract class Movement(override val ss: SwiftSlayer, override val source: BlockPos, override val dest: BlockPos) :
    IMovement {

  override var costs: Double = 1e6
  override fun getCost() = costs
}