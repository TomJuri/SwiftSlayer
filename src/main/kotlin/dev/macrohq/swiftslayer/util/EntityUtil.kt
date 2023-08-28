package dev.macrohq.swiftslayer.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import kotlin.math.abs
import kotlin.math.sqrt

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>, health: Int): List<EntityLiving>{
        val entities = world.getLoadedEntityList().filterIsInstance(entityClass).filter { it.health > health }
        return entities.sortedBy { getCost(it) }
    }
    fun getBestMob(entity: Class<out EntityLiving>): EntityLiving? {
        val entities = player.worldObj.loadedEntityList.filterIsInstance(entity)
        val entitiesMap = mutableMapOf<EntityLiving, Pair<Boolean, Float>>()
        var miniboss: EntityLiving? = null
        entities.forEach {
            if (it.health > 40000) {
                miniboss = it
                return@forEach
            }
            entitiesMap[it] = Pair(player.canEntityBeSeen(it), it.getDistanceToEntity(player))
        }
        var closestEntity: EntityLiving? = null
        var closestDistance = Float.MAX_VALUE
        var closestVisibleEntity: EntityLiving? = null
        var closestVisibleDistance = Float.MAX_VALUE
        entitiesMap.keys.forEach {
            val distance = entitiesMap[it]!!.second
            if (distance <= 40) {
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestEntity = it
                }
                if (entitiesMap[it]!!.first && distance < closestVisibleDistance) {
                    closestVisibleDistance = distance
                    closestVisibleEntity = it
                }
            }
        }
        return if (miniboss != null) miniboss else if (closestVisibleEntity != null) closestVisibleEntity else closestEntity
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