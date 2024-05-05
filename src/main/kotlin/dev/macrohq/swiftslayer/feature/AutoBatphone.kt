package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.util.*
import me.kbrewster.eventbus.Subscribe
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class AutoBatphone {

    private var state = State.SWITCH_TO_BATPHONE
    private var nextState = State.USE_BATPHONE
    var enabled = false
        private set
    private var timeout = Timer(500)
    private var cancel = false
    private var timerDelay: Long = SwiftSlayer.config.getRandomGUIMacroDelay()
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


    @Subscribe
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return

       if(!timeout.isDone) {
           return
       }

        when (state) {
            State.SWITCH_TO_BATPHONE -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
                if (!InventoryUtil.holdItem("Maddox Batphone")) {
                    Logger.error("Theres no Batphone on your hotbar!")
                    disable()
                    return
                }
                state = State.USE_BATPHONE

            }

            State.USE_BATPHONE -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
                KeyBindUtil.rightClick()

                state = State.BATPHONE_OPEN
            }

            State.BATPHONE_OPEN -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
                if (InventoryUtil.getSlotInGUI("Close") == -1) return
                state =
                    if (InventoryUtil.getSlotInGUI("Close") != -1 && InventoryUtil.getSlotInGUI("Ongoing Slayer Quest") != -1) State.CANCEL
                    else if (InventoryUtil.getSlotInGUI("Close") != -1 && InventoryUtil.getSlotInGUI("Slayer Quest Complete") != -1) State.ACCEPT_REWARDS
                    else State.CLICK_SLAYER

            }

            State.CANCEL -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
                if (!InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Ongoing Slayer Quest"))) {
                    Logger.log("Failed to cancel slayer quest.")
                    return
                }
                state = State.CONFIRM
                cancel = true

            }

            State.ACCEPT_REWARDS -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
                if (!InventoryUtil.clickSlot(InventoryUtil.getSlotInGUI("Slayer Quest Complete"))) {
                    Logger.log("Failed to accept rewards.")
                    return
                }
                state = State.CLICK_SLAYER
            }

            State.CLICK_SLAYER -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
                if (!InventoryUtil.clickSlot(SlayerUtil.getSlayerSlot())) {
                    Logger.log("Failed to click slayer!")
                    return
                }
                state = State.CLICK_TIER
            }

            State.CLICK_TIER -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
                if (!InventoryUtil.clickSlot(SlayerUtil.getTierSlot())) {
                    Logger.log("Failed to click tier!")
                    return
                }
                state = State.CONFIRM
            }

            State.CONFIRM -> {
                timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
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


    @Subscribe
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.type.toInt() != 0) return
        if (event.message.unformattedText.contains("[NPC]") && state == State.WAITING) {
            state = State.BATPHONE_OPEN
        }
    }

    fun enable() {
        if (enabled) return
        state = State.SWITCH_TO_BATPHONE
        cancel = false
        timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
        Logger.info("Enabling Auto Batphone")
        enabled = true
    }

    private fun tryAgain() {
        Logger.info("Something went wrong, trying again...")
        InventoryUtil.closeGUI()
        state = State.SWITCH_TO_BATPHONE
        timeout = Timer(SwiftSlayer.config.getRandomGUIMacroDelay())
        cancel = false
    }

    fun disable() {
        if (!enabled) return
        Logger.info("Disabling Auto Batphone")
        enabled = false
        InventoryUtil.closeGUI()
    }
}