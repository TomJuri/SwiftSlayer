package dev.macrohq.swiftslayer.util

import java.util.*

object InventoryUtil {

    fun holdItem(name: String): Boolean{
        if(getSlotFromName(name)<9){
            player.inventory.currentItem = getSlotFromName(name)
            return true
        }
        return false
    }
    private fun getSlotFromName(name: String): Int{
        val inventory = player.inventory
        for(i in 0..8){
            val currItem = inventory.getStackInSlot(i)
            if(currItem!=null && currItem.displayName.contains(name, true)) return i
        }
        return 100
    }
}