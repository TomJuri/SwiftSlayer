package dev.macrohq.swiftslayer.util

object InventoryUtil {

    fun holdItem(name: String): Boolean {
        if (getSlotForItem(name) < 9) {
            player.inventory.currentItem = getSlotForItem(name)
            return true
        }
        return false
    }

    fun getSlotForItem(name: String): Int {
        val inventory = player.inventory
        for (i in 0..8) {
            val currItem = inventory.getStackInSlot(i)
            if (currItem != null && currItem.displayName.contains(name, true)) return i
        }
        return 100
    }
}