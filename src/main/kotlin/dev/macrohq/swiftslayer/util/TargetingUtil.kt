package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving

object TargetingUtil {
    fun <T : EntityLiving> getBestMob(entity: Class<T>): T? {
        // i love generics
        // although this could probably be optimized a little bit more
        // cba rn tho so TODO
        val entities = player.worldObj.loadedEntityList.filterIsInstance(entity)
        val entitiesMap = mutableMapOf<T, Pair<Boolean, Float>>()
        var miniboss: T? = null
        entities.forEach {
            if (it.health > 40000) {
                miniboss = it
                return@forEach
            }
            entitiesMap[it] = Pair(player.canEntityBeSeen(it), it.getDistanceToEntity(player))
        }
        var closestEntity: T? = null
        var closestDistance = Float.MAX_VALUE
        var closestVisibleEntity: T? = null
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