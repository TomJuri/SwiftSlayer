package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.feature.helper.Target
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityCaveSpider
import net.minecraft.init.Blocks
import kotlin.math.abs

object EntityUtil {

    fun getMobCost(entity: EntityLiving): Double {
        var yawCost: Double = 0.0
        var distanceCost: Double = 0.0
        var yChangeCost: Double = 0.0
        var healthCost: Double = 0.0
        var returnedCost: Double = 0.0

        distanceCost += RotationMath.getXZDistance(player.position, entity.position)

            if(abs(entity.position.y - player.position.y) > 5) {
                yChangeCost += (abs(entity.position.y - player.position.y) - 5) * 10
            }

            yawCost = abs(AngleUtil.yawTo360(mc.thePlayer.rotationYaw) - AngleUtil.yawTo360(Target(entity).getAngle().yaw)).toDouble()
            yawCost /= 10

            healthCost += entity.health.toDouble() / 100
        returnedCost = yawCost + distanceCost + yChangeCost + healthCost
        if(player.canEntityBeSeen(entity)) {
            returnedCost /= 2
        }
        return returnedCost
    }



    fun getMobs(entityClass: Class<out EntityLiving>): List<EntityLiving> {
        val entities = world.getLoadedEntityList().asSequence()
            .filterIsInstance(entityClass)
            .filter { it !is EntityCaveSpider }
            .filter { it.maxHealth > 250 }
           .filter { world.getBlockState(it.getStandingOnCeil()).block != Blocks.air }
           .filter { AngleUtil.getAngles(it).pitch < 60 && AngleUtil.getAngles(it).pitch > -60 }
            .filter { !SlayerUtil.isBoss(it) }
            .filter { !SlayerUtil.isMiniBoss(it) }
            .sortedBy{ getMobCost(it) }
            .toMutableList()
        if (!config.ignoreMiniBosses && SlayerUtil.getMiniBoss() != null)
            entities.add(0, SlayerUtil.getMiniBoss()!!)
        return entities
    }
}
