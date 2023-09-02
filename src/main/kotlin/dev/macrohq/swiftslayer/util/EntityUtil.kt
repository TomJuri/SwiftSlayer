package dev.macrohq.swiftslayer.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.MathHelper
import kotlin.math.abs
import kotlin.math.sqrt

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>, health: Int): List<EntityLiving>{
        val entities = world.getLoadedEntityList().filterIsInstance(entityClass).filter { it.health >= health && it.health <= 13000}
        return entities.sortedBy { getCost(it) }
    }
    fun getCost(entity: EntityLiving): Float {
        // Maybe AngleDiff isn't important.
        var yawChange = MathHelper.wrapAngleTo180_float(AngleUtil.getAngles(entity).yaw) - MathHelper.wrapAngleTo180_float(AngleUtil.yawTo360(
            player.rotationYaw))
        if (yawChange <= -180.0f) yawChange += 360.0f else if (yawChange > 180.0f) yawChange += -360.0f
        val pitchChange = abs(player.rotationPitch - AngleUtil.getAngles(entity).pitch)
        val angleChange = yawChange * 0.5 + pitchChange * 0.5
        val distance = sqrt(player.getDistanceToEntity(entity))
        val cost = getRevCost(entity)
        return (distance).toFloat()
    }

    fun getRevCost(entity: EntityLiving): Int{
        return 1;
    }
}