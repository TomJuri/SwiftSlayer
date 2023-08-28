package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving

object TargetingUtil {
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
}