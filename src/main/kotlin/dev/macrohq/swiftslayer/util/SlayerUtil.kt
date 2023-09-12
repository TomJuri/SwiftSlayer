package dev.macrohq.swiftslayer.util

object SlayerUtil {
    fun getSlayerName(): String? {
        return when(config.slayer){
            0 -> "Revenant Horror"
            1 -> "Tarantula Broodfather"
            2 -> "Sven Packmaster"
            3 -> "Voidgloom Seraph"
            else -> null
        }
    }

    fun getSlayerSlot(): Int{
        return InventoryUtil.getSlotInGUI(getSlayerName()!!)
    }

    fun getTierSlot(): Int{
        return when(config.slayerTier){
            0 -> InventoryUtil.getSlotInGUI("${getSlayerName()} I")
            1 -> InventoryUtil.getSlotInGUI("${getSlayerName()} II")
            2 -> InventoryUtil.getSlotInGUI("${getSlayerName()} III")
            3 -> InventoryUtil.getSlotInGUI("${getSlayerName()} IV")
            4 -> InventoryUtil.getSlotInGUI("${getSlayerName()} V")
            else -> -1
        }
    }
}