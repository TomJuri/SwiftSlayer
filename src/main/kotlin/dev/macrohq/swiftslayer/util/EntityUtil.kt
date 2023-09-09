package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import kotlin.math.abs
import kotlin.math.sqrt

object EntityUtil {
    fun getMobs(entityClass: Class<out EntityLiving>, health: Int): List<EntityLiving> {
        val entities = world.getLoadedEntityList().filterIsInstance(entityClass).filter {
            it.maxHealth == 1024f && it.health <= health && inRange(it)
        }
        return entities.sortedBy { getCost(it) }
    }

    fun getCost(entity: EntityLiving): Float {
        val yawChange =
            abs(MathHelper.wrapAngleTo180_float(AngleUtil.getAngles(entity).yaw - AngleUtil.yawTo360(player.rotationYaw))) / 180f
        val pitchChange = abs(-player.rotationPitch + AngleUtil.getAngles(entity).pitch)
        val angleChange = yawChange + pitchChange
        val distance = (player.getDistanceToEntity(entity))
//        val cost = getRevCost(entity)
        val cost = if (player.canEntityBeSeen(entity)) 0 else 3
        return (distance * 0.5f + angleChange * 0.2f + cost)
//        return distance
    }

    fun getRevCost(entity: EntityLiving): Int {
        return 1
    }

    private fun inRange(entity: EntityLiving): Boolean {
//        Logger.info("entity: ${entity is EntityWolf}")
//        Logger.info("entityDistance: ${sqrt(entity.position.distanceSq(BlockPos(-382, 51, -7)))}")
        if (entity is EntityWolf) {
            return sqrt(entity.position.distanceSq(BlockPos(-382, 51, -7))) > 20
        } else if (entity is EntitySpider) {
            return (sqrt(entity.position.distanceSq(BlockPos(-294, 43, -243))) < 60 &&
                    sqrt(entity.position.distanceSq(BlockPos(-284, 48, 151))) > 20)
        }
        return true
    }
}
