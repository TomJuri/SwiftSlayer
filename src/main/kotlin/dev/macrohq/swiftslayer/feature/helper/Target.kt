package dev.macrohq.swiftslayer.feature.helper

import dev.macrohq.swiftslayer.util.AngleUtil
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

// Rotation Target
class Target(private var targetAngle: Angle) {
  private var entity: Entity? = null
  private var block: BlockPos? = null
  private var vecPos: Vec3? = null
  constructor(entity: Entity) : this(AngleUtil.getAngle(entity)) {this.entity = entity}
  constructor(block: BlockPos) : this(AngleUtil.getAngle(block)) { this.block = block }
  constructor(vecPos: Vec3) : this(AngleUtil.getAngle(vecPos)) {this.vecPos = vecPos}

  fun getAngle(): Angle {
    if (block != null) {
      return AngleUtil.getAngle(block!!)
    }
    if (entity != null) {
      return AngleUtil.getAngle(entity!!)
    }
    if(vecPos != null) {
      return AngleUtil.getAngle(vecPos!!)
    }
    return targetAngle
  }
}