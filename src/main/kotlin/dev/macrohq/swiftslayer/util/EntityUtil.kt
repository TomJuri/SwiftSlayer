package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.util.MathHelper
import kotlin.math.abs
import kotlin.math.sqrt

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>, health: Int): List<EntityLiving>{
        val entities = world.getLoadedEntityList().filterIsInstance(entityClass).filter { it.maxHealth == 1024f && it.health<=health}
        return entities.sortedBy { getCost(it) }
    }
    fun getCost(entity: EntityLiving): Float {
        val yawChange = abs(MathHelper.wrapAngleTo180_float(AngleUtil.getAngles(entity).yaw - AngleUtil.yawTo360(player.rotationYaw)))/180
        val pitchChange = abs(-player.rotationPitch + AngleUtil.getAngles(entity).pitch)/20
        val angleChange = yawChange + pitchChange
        val distance = sqrt(player.getDistanceToEntity(entity))
        val cost = getRevCost(entity)
        return (distance + angleChange + cost)
    }

    fun getRevCost(entity: EntityLiving): Int{
        return 1
    }
}
