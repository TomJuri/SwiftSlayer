package dev.macrohq.swiftslayer.util

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityBlaze
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.util.StringUtils

object SlayerUtil {

    private val bosses = listOf(
        "Revenant Horror",
        "Atoned Horror",
        "Tarantula Broodfather",
        "Sven Packmaster",
        "Voidgloom Seraph",
        "Inferno Demonlord"
    )
    private val miniBosses = listOf(
        "Revenant Sycophant",
        "Revenant Champion",
        "Deformed Revenant",
        "Atoned Champion",
        "Atoned Revenant",
        "Tarantula Vermin",
        "Tarantula Beast",
        "Mutant Tarantula",
        "Pack Enforcer",
        "Sven Follower",
        "Sven Alpha",
        "Voidling Devotee",
        "Voidling Radical",
        "Voidcrazed Maniac",
        "Flare Demon",
        "Kindleheart Demon",
        "Burningsoul Demon"
    )
    private val bossTypes =
        listOf(EntityZombie::class, EntitySpider::class, EntityWolf::class, EntityEnderman::class, EntityBlaze::class)

    fun getMiniBoss() = player.worldObj.loadedEntityList.filter { it::class in bossTypes }.minByOrNull { it0 ->
        it0.getDistanceToEntity(
            player.worldObj.loadedEntityList.filterIsInstance<EntityArmorStand>().filter { isMiniBoss(it) }
                .minByOrNull { it.getDistanceToEntity(player) })
    } as EntityLiving

    fun getBoss(): Pair<EntityLiving, EntityArmorStand>? {
        val spawnedBy = player.worldObj.loadedEntityList.firstOrNull {
            StringUtils.stripControlCodes(it.name).contains("Spawned by: ${player.name}")
        }
        if (spawnedBy != null) {
            val bossArmorStand =
                player.worldObj.loadedEntityList.filterIsInstance<EntityArmorStand>().filter { isBoss(it) }
                    .minByOrNull { it.getDistanceToEntity(spawnedBy) }
            val boss = player.worldObj.loadedEntityList.filter { it::class in bossTypes }
                .minByOrNull { it.getDistanceToEntity(spawnedBy) }
            if (bossArmorStand != null && boss != null) return Pair(boss as EntityLiving, bossArmorStand)
        }
        return null
    }

    fun isBoss(entity: EntityArmorStand) = bosses.contains(StringUtils.stripControlCodes(entity.name))
    fun isMiniBoss(entity: EntityArmorStand) = miniBosses.contains(StringUtils.stripControlCodes(entity.name))

    fun getSlayerName(): String? {
        return when (config.slayer) {
            0 -> "Revenant Horror"
            1 -> "Tarantula Broodfather"
            2 -> "Sven Packmaster"
            3 -> "Voidgloom Seraph"
            else -> null
        }
    }

    fun getSlayerSlot(): Int {
        return InventoryUtil.getSlotInGUI(getSlayerName()!!)
    }

    fun getTierSlot(): Int {
        return when (config.slayerTier) {
            0 -> InventoryUtil.getSlotInGUI("${getSlayerName()} I")
            1 -> InventoryUtil.getSlotInGUI("${getSlayerName()} II")
            2 -> InventoryUtil.getSlotInGUI("${getSlayerName()} III")
            3 -> InventoryUtil.getSlotInGUI("${getSlayerName()} IV")
            4 -> InventoryUtil.getSlotInGUI("${getSlayerName()} V")
            else -> -1
        }
    }
}