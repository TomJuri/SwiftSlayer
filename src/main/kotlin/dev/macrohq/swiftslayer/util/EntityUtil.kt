package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.init.Blocks

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>): List<EntityLiving> {
        val entities = world.getLoadedEntityList().asSequence()
            .filterIsInstance(entityClass)
            .filter { it.maxHealth > 100 }
            .filter { world.getBlockState(it.getStandingOnCeil()).block != Blocks.air }
            .filter { AngleUtil.getAngles(it).pitch < 60 && AngleUtil.getAngles(it).pitch > -60 }
            .filter { !SlayerUtil.isBoss(it) }
            .filter { !SlayerUtil.isMiniBoss(it) }
            .toList().sortedBy { it.getDistanceToEntity(player) }
        if (config.ignoreMiniBosses) return entities
        val newEntities = mutableListOf<EntityLiving>()
        if (SlayerUtil.getMiniBoss() != null) newEntities.add(SlayerUtil.getMiniBoss()!!)
            newEntities.addAll(entities.sortedBy { it.getDistanceToEntity(player) })
        return newEntities
    }
}
