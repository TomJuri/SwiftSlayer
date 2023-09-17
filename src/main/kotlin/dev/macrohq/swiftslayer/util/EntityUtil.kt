package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.util.BlockPos
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>): List<EntityLiving> {
        val entities = world.getLoadedEntityList().filterIsInstance(entityClass).filter { it.maxHealth > 100 }.filter { inRange(it) }.filter { !SlayerUtil.isBoss(it) }
        val newEntities = mutableListOf<EntityLiving>()
        if (SlayerUtil.getMiniBoss() != null) newEntities.add(SlayerUtil.getMiniBoss()!!)
        if (entities.none { player.canEntityBeSeen(it) && isInFov(it) })
            newEntities.addAll(entities.sortedBy { it.getDistanceToEntity(player) })
        else
            newEntities.addAll(entities.filter { player.canEntityBeSeen(it) && isInFov(it) }.sortedBy { it.getDistanceToEntity(player) })
        return newEntities
    }

    private fun inRange(entity: EntityLiving): Boolean {
        if (entity is EntityWolf) {
            return sqrt(entity.position.distanceSq(BlockPos(-382, 51, -7))) > 20
        } else if (entity is EntitySpider) {
            return (sqrt(entity.position.distanceSq(BlockPos(-294, 43, -243))) < 60 &&
                    sqrt(entity.position.distanceSq(BlockPos(-284, 48, 151))) > 20)
        }
        return true
    }

    private fun isInFov(entity: EntityLiving): Boolean {
        val deltaX = entity.posX - player.posX
        val deltaZ = entity.posZ - player.posZ
        val angle = atan2(deltaZ, deltaX) - atan2(player.lookVec.zCoord, player.lookVec.xCoord)
        val fov = Math.toRadians(110.0)
        if (abs(angle) < fov / 2.0) return true
        return false
    }
}
