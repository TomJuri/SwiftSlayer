package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.init.Blocks

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>): List<EntityLiving> {
        val entities = world.getLoadedEntityList().filterIsInstance(entityClass).filter { it.maxHealth > 100 }
            .filter { world.getBlockState(it.getStandingOnCeil()).block != Blocks.air }.filter { !SlayerUtil.isBoss(it) }
        val newEntities = mutableListOf<EntityLiving>()
        if (SlayerUtil.getMiniBoss() != null) newEntities.add(SlayerUtil.getMiniBoss()!!)
            newEntities.addAll(entities.sortedBy { it.getDistanceToEntity(player) })
        return newEntities
    }
}
