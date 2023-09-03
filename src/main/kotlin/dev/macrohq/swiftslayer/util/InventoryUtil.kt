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
    fun getSlotFromName(name: String): Int{
        val inventory = player.inventory
        for(i in 0..inventory.sizeInventory){
            if(inventory.getStackInSlot(i)!=null &&
                inventory.getStackInSlot(i).displayName.lowercase(Locale.getDefault()).contains(name.lowercase(Locale.getDefault()))) return i
        }
        return 100
    }

}