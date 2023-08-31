package dev.macrohq.swiftslayer.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import kotlin.math.abs
import kotlin.math.sqrt

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>, health: Int): List<EntityLiving>{
        val entities = world.getLoadedEntityList().filterIsInstance(entityClass).filter { it.health >= health }
        return entities.sortedBy { getCost(it) }
    }

    fun getCost(entity: EntityLiving): Float {
        // Maybe AngleDiff isn't important.
        val distance = sqrt(player.getDistanceToEntity(entity))
        val cost = getRevCost(entity)
        return (distance*0.5 + cost*0.3 + 1).toFloat()
    }

    fun getRevCost(entity: EntityLiving): Int{
        return 1;
    }
}