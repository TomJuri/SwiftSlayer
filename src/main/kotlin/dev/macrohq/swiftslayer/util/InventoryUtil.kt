package dev.macrohq.swiftslayer.util

import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.StringUtils

object InventoryUtil {

    fun getOpenContainerSlots(): List<Slot?> {
        val inventory = mutableListOf<Slot?>()
        player.openContainer.inventorySlots.forEach { inventory.add(it) }
        return inventory
    }

    fun getGUIInventorySlot(): List<Slot?> {
        val inventory = getOpenContainerSlots()
        return inventory.slice(0..inventory.size - 37)
    }

    fun getGUIInventory(): List<ItemStack?> {
        val inventory = getOpenContainerItemStack()
        return inventory.slice(0..inventory.size - 37)
    }

    fun getOpenContainerItemStack(): List<ItemStack?> {
        val inventory = mutableListOf<ItemStack?>()
        getOpenContainerSlots().forEach { inventory.add(it!!.stack) }
        return inventory
    }

    fun holdItem(name: String): Boolean {
        if (getHotbarSlotForItem(name) < 9) {
            player.inventory.currentItem = getHotbarSlotForItem(name)
            return true
        }
        return false
    }

    fun getSlotInGUI(name: String): Int {
        return getGUIInventorySlot().find { it?.stack?.displayName?.contains(name) == true }?.slotIndex ?: -1
    }

    fun getHotbarSlotForItem(name: String): Int {
        val inventory = player.inventory
        for (i in 0..8) {
            val currItem = inventory.getStackInSlot(i)
            if (currItem != null && currItem.displayName.contains(name, true)) return i
        }
        return 100
    }

    fun clickSlot(slot: Int, button: Int = 0, clickType: Int = 0) {
        mc.playerController.windowClick(player.openContainer.windowId, slot, button, clickType, player)
    }

    fun getGUIName(): String? {
        return StringUtils.stripControlCodes(player.openContainer.inventorySlots[0].inventory.name)
    }
}