package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.error
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoBatphone {
    private var state = State.NONE
    private var batphone = false
    private var canceling = false
    enum class State{
        STARTING,
        CLICKING,
        OPENING,
        CLICKING_SLAYER,
        CLICKING_TIER,
        CLICKING_CONFIRM,
        CLICKING_CANCEL,
        NONE
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent){
        if(player == null || world == null) return
        when(state){
            State.STARTING -> {
                if(InventoryUtil.holdItem("Maddox Batphone")) state = State.CLICKING
                else disable()
            }
            State.CLICKING -> {
                mc.playerController.sendUseItem(player, world, player.inventory.getStackInSlot(InventoryUtil.getHotbarSlotForItem("Maddox Batphone")))
                state = State.OPENING
            }
            State.CLICKING_SLAYER -> {
                if(player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null && InventoryUtil.getGUIName()!!.contains("Slayer")){
                    if(SlayerUtil.getSlayerSlot()!=-1){
                        InventoryUtil.clickSlot(SlayerUtil.getSlayerSlot())
                        state = State.CLICKING_TIER
                    }else{
                        info("Slayer Not found!")
                        disable()
                    }
                }
            }
            State.CLICKING_TIER -> {
                if(player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null && InventoryUtil.getGUIName()!!.contains(SlayerUtil.getSlayerName()!!)){
                    if(SlayerUtil.getTierSlot()!=-1){
                        InventoryUtil.clickSlot(SlayerUtil.getTierSlot())
                        state = State.CLICKING_CONFIRM
                    }
                    else{
                        info("Tier not found")
                        disable()
                    }
                }
            }
            State.CLICKING_CONFIRM ->{
                if(player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null
                    && InventoryUtil.getGUIName()!!.contains("Confirm") && InventoryUtil.getSlotInGUI("Confirm")!=-1){
                    InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Confirm"))
                    disable()
                }
            }
            State.CLICKING_CANCEL -> {
                if(player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null && InventoryUtil.getGUIName()!!.contains("Slayer")){
                    if(InventoryUtil.getSlotInGUI("Ongoing Slayer Quest") != -1) {
                        InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Ongoing Slayer Quest"))
                        state = State.CLICKING_CONFIRM
                    }
                    else{
                        info("No Slayer Active.")
                    }
                }
            }
            else -> {}
        }
    }
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent){
        if(event.type.toInt() != 0) return
        if(event.message.unformattedText.contains("[OPEN MENU]") && state == State.OPENING) {
            player.sendChatMessage(event.message.siblings.find { it.unformattedText.contains("[OPEN MENU]") }?.chatStyle?.chatClickEvent?.value)
            state = if(canceling) State.CLICKING_CANCEL else State.CLICKING_SLAYER
        }
    }

    fun enable(cancelSlayer: Boolean = false){
        canceling = cancelSlayer
        state = State.STARTING
        batphone = true
    }

    fun disable(){
        error("Disabling Auto Batphone")
        state = State.NONE
        batphone = false
    }
}