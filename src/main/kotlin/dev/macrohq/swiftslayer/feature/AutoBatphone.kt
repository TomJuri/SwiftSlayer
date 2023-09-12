package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.util.*
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoBatphone {
    private var state = State.SWITCH_TO_BATPHONE
    var enabled = false
        private set
    private var timeout: Timer? = null
    private var cancel = false

    enum class State {
        WAITING,
        SWITCH_TO_BATPHONE,
        USE_BATPHONE,
        BATPHONE_OPEN,
        CANCEL,
        ACCEPT_REWARDS,
        CLICK_SLAYER,
        CLICK_TIER,
        CONFIRM,
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        if (timeout != null && timeout!!.isDone) {
            Logger.error("State timed out!")
            tryAgain()
            return
        }
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
                timeout = Timer(10_000)
                state = State.WAITING
            }

            State.BATPHONE_OPEN -> {
                if (timeout == null) timeout = Timer(3_000)
                if (InventoryUtil.getSlotInGUI("Close") == -1) return
                state =
                    if (InventoryUtil.getSlotInGUI("Close") != -1 && InventoryUtil.getSlotInGUI("Ongoing Slayer Quest") != -1) State.CANCEL
                    else if (InventoryUtil.getSlotInGUI("Close") != -1 && InventoryUtil.getSlotInGUI("Slayer Quest Complete") != -1) State.ACCEPT_REWARDS
                    else State.CLICK_SLAYER
            }

            State.CANCEL -> {
                if (timeout == null) timeout = Timer(3_000)
                if (!InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Ongoing Slayer Quest"))) {
                    Logger.log("Failed to cancel slayer quest.")
                    return
                }
                state = State.CONFIRM
                cancel = true
            }

            State.ACCEPT_REWARDS -> {
                if (timeout == null) timeout = Timer(3_000)
                if (!InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Slayer Quest Complete"))) {
                    Logger.log("Failed to accept rewards.")
                    return
                }
                state = State.CLICK_SLAYER
            }

            State.CLICK_SLAYER -> {
                if (timeout == null) timeout = Timer(3_000)
                if (!InventoryUtil.clickSlot(SlayerUtil.getSlayerSlot())) {
                    Logger.log("Failed to click slayer!")
                    return
                }
                state = State.CLICK_TIER
            }

            State.CLICK_TIER -> {
                if (timeout == null) timeout = Timer(3_000)
                if (!InventoryUtil.clickSlot(SlayerUtil.getTierSlot())) {
                    Logger.log("Failed to click tier!")
                    return
                }
                state = State.CONFIRM
            }

            State.CONFIRM -> {
                if (timeout == null) timeout = Timer(3_000)
                if (!InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Confirm"))) {
                    Logger.log("Failed to click confirm!")
                    return
                }
                if (cancel) state = State.SWITCH_TO_BATPHONE else disable()
                cancel = false
            }

            else -> {}
        }
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.type.toInt() != 0) return
        if (event.message.unformattedText.contains("[OPEN MENU]") && state == State.WAITING) {
            player.sendChatMessage(event.message.siblings.find { it.unformattedText.contains("[OPEN MENU]") }?.chatStyle?.chatClickEvent?.value)
            state = State.BATPHONE_OPEN
        }
    }

    fun enable() {
        if (enabled) return
        state = State.SWITCH_TO_BATPHONE
        cancel = false
        timeout = null
        Logger.info("Enabling Auto Batphone")
        enabled = true
    }

    private fun tryAgain() {
        Logger.info("Something went wrong, trying again...")
        InventoryUtil.closeGUI()
        state = State.SWITCH_TO_BATPHONE
        timeout = null
        cancel = false
    }

    fun disable() {
        if (!enabled) return
        Logger.info("Disabling Auto Batphone")
        enabled = false
        InventoryUtil.closeGUI()
    }
}