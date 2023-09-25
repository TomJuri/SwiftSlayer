package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityCaveSpider
import net.minecraft.init.Blocks

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>): List<EntityLiving> {
        val entities = world.getLoadedEntityList().asSequence()
            .filterIsInstance(entityClass)
            .filter { it !is EntityCaveSpider }
            .filter { it.maxHealth > 250 }
            .filter { world.getBlockState(it.getStandingOnCeil()).block != Blocks.air }
            .filter { AngleUtil.getAngles(it).pitch < 60 && AngleUtil.getAngles(it).pitch > -60 }
            .filter { !SlayerUtil.isBoss(it) }
            .filter { !SlayerUtil.isMiniBoss(it) }
            .sortedBy { it.getDistanceToEntity(player) }
            .toMutableList()
        if (!config.ignoreMiniBosses && SlayerUtil.getMiniBoss() != null)
            entities.add(0, SlayerUtil.getMiniBoss()!!)
        return entities
    }
}
