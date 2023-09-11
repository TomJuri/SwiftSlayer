package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.util.*
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoBatphone {
    private var state = State.SWITCH_TO_BATPHONE
    var enabled = false
        private set

    enum class State {
        WAITING,
        SWITCH_TO_BATPHONE,
        USE_BATPHONE,
        OPEN_GUI,
        CLICK_SLAYER,
        CLICK_TIER,
        CONFIRM,
        CANCEL,
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        when (state) {
            State.SWITCH_TO_BATPHONE -> {
                if (!InventoryUtil.holdItem("Maddox Batphone")) {
                    Logger.error("Theres no Batphone on your hotbar!")
                    disable()
                    return
                }
                state = State.USE_BATPHONE
            }

            State.USE_BATPHONE -> {
                KeyBindUtil.rightClick()
                state = State.OPEN_GUI
            }

            State.CLICK_SLAYER -> {
                if (player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null && InventoryUtil.getGUIName()!!
                        .contains("Slayer")
                ) {
                    if (SlayerUtil.getSlayerSlot() != -1) {
                        InventoryUtil.clickSlot(SlayerUtil.getSlayerSlot())
                        state = State.CLICK_TIER
                    } else {
                        Logger.info("Slayer Not found!")
                        disable()
                    }
                }
            }

            State.CLICK_TIER -> {
                if (player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null && InventoryUtil.getGUIName()!!
                        .contains(SlayerUtil.getSlayerName()!!)
                ) {
                    if (SlayerUtil.getTierSlot() != -1) {
                        InventoryUtil.clickSlot(SlayerUtil.getTierSlot())
                        state = State.CONFIRM
                    } else {
                        Logger.info("Tier not found")
                        disable()
                    }
                }
            }

            State.CONFIRM -> {
                if (player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null
                    && InventoryUtil.getGUIName()!!.contains("Confirm") && InventoryUtil.getSlotInGUI("Confirm") != -1
                ) {
                    InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Confirm"))
                    disable()
                }
            }

            State.CANCEL -> {
                if (!InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Ongoing Slayer Quest"))) {
                    Logger.error("Failed to cancel slayer quest.")
                    disable()
                }
                if (player.openContainer is ContainerChest && InventoryUtil.getGUIName() != null && InventoryUtil.getGUIName()!!
                        .contains("Slayer")
                ) {
                    if (InventoryUtil.getSlotInGUI("Ongoing Slayer Quest") != -1) {
                        InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Ongoing Slayer Quest"))
                        state = State.CONFIRM
                    } else {
                        Logger.info("No Slayer Active.")
                        disable()
                    }
                }
            }

            else -> {}
        }
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.type.toInt() != 0) return
        if (event.message.unformattedText.contains("You already have an active Slayer Quest!")) disable()
        if (event.message.unformattedText.contains("[OPEN MENU]")) {
            player.sendChatMessage(event.message.siblings.find { it.unformattedText.contains("[OPEN MENU]") }?.chatStyle?.chatClickEvent?.value)
            state = if (canceling) State.CANCEL else State.CLICK_SLAYER
        }
    }

    fun enable() {
        if (enabled) return
        state = State.SWITCH_TO_BATPHONE
        enabled = true
        Logger.info("Enabling Auto Batphone")
    }

    private fun tryAgain() {
        InventoryUtil.closeGUI()
        state = State.SWITCH_TO_BATPHONE
    }

    fun disable() {
        if (!enabled) return
        Logger.info("Disabling Auto Batphone")
        state = State.SWITCH_TO_BATPHONE
        enabled = false
    }
}