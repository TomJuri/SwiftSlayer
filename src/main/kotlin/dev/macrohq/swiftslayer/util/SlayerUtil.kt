package dev.macrohq.swiftslayer.util

import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.StringUtils

object SlayerUtil {

    fun isBoss(entity: EntityArmorStand): Boolean {
        val name = StringUtils.stripControlCodes(entity.name)
        return name.contains("Atoned Horror") || name.contains("Revenant Horror") || name.contains("Tarantula Broodfather") || name.contains(
            "Sven Packmaster"
        ) || name.contains("Voidgloom Seraph")
    }

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