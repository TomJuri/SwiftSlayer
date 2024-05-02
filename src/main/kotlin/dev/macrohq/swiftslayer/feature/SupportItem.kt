package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.macro.MacroManager
import dev.macrohq.swiftslayer.macro.bossKiller.RevBossKiller
import dev.macrohq.swiftslayer.util.*
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import me.kbrewster.eventbus.Subscribe
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.util.regex.Pattern

class SupportItem {
  private var maxHealth = -1
  private var health = -1
  private var  state: State = State.SHOULD_ENABLE
  private var nextSlot: Int = -1
  private var previousSlot: Int = -1
  private var wait: Timer = Timer(150)



  @Subscribe
  fun onTick(event: ClientTickEvent) {
    if(!macroManager.enabled) return


    when(state) {
      State.SHOULD_ENABLE -> {
        if(!wait.isDone) return
        if(health.toFloat() / maxHealth.toFloat() < config.useHealingAt / 100f || enable) {
          if(mobKiller.getInstance().enabled) mobKiller.getInstance().pause()
          if(RevBossKiller.getInstance().enabled) RevBossKiller.getInstance().pause()
          state = State.SWITCH_ITEM
          Logger.info("enabling")
          previousSlot = player.inventory.currentItem
          nextSlot = InventoryUtil.getHotbarSlotForItem("Wand of ")
          wait = Timer(150)

        }
      }

      State.SWITCH_ITEM -> {
        if(!wait.isDone) return
      player.inventory.currentItem = nextSlot
      state = State.CLICK_ITEM
        wait = Timer(100)
      }
      State.CLICK_ITEM -> {
        if(!wait.isDone) return
        KeyBindUtil.rightClick()
        state = State.SWITCH_BACK
        wait = Timer(150)
      }

      State.SWITCH_BACK -> {
        if(!wait.isDone) return
        player.inventory.currentItem = previousSlot
        state = State.SHOULD_ENABLE
        wait = Timer(1500)
        if(mobKiller.getInstance().paused) mobKiller.getInstance().paused = false
        if(RevBossKiller.getInstance().paused) RevBossKiller.getInstance().paused = false
      }
    }

  }

   fun healing() {
    if (true) {
      // if (health == -1 || maxHealth == -1) return
      // val healthPercent = health.toFloat() / maxHealth.toFloat()
      //  if (healthPercent > config.useHealingAt / 100f || !healingTimer.isDone) return
      if (healingTimer.isDone) {
        val previousItem = player.inventory.currentItem
        if (InventoryUtil.holdItem("Wand of ")) {
          Logger.info("Using healing wand.")
          KeyBindUtil.rightClick()
          healingTimer = Timer(8000)
          player.inventory.currentItem = previousItem
        } else {
          Logger.error("No Wand of Healing found in hotbar!")
        }
      }
    }
  }

  private fun tuba() {
    if ( tubaTimer.isDone && macroManager.state == MacroManager.State.KILL_BOSS) {
      val previousItem = player.inventory.currentItem
      if (InventoryUtil.holdItem("Tuba")) {
        Logger.info("Using Weird Tuba.")
        KeyBindUtil.rightClick()
        tubaTimer = Timer(21000)
        player.inventory.currentItem = previousItem
      } else {
        Logger.error("No Weird Tuba found in hotbar!")
      }
    }
  }

  private fun orb() {
    if ( SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_ALIVE && orbTimer.isDone) {
      val previousItem = player.inventory.currentItem
      if (InventoryUtil.holdItem("Flux")) {
        Logger.info("Using Power Orb.")
        KeyBindUtil.rightClick()
        orbTimer = Timer(30_000)
        player.inventory.currentItem = previousItem
      } else {
        Logger.error("No Power Orb found in hotbar!")
      }
    }
  }

  @Subscribe
  fun onChatReceive(event: ClientChatReceivedEvent) {
    if (event.type.toInt() != 2) return
    val message = StringUtils.stripControlCodes(event.message.unformattedText)
    if (!message.contains("❤")) return
    val splitMessage = message.split("     ")
    splitMessage.forEach { sect ->
      if (sect.contains("❤")) {
        val m = Pattern.compile("(?<health>[0-9,.]+)/(?<maxHealth>[0-9,.]+)❤(?<wand>\\+(?<wandHeal>[0-9,.]+)[▆▅▄▃▂▁])?").matcher(sect)
        if (m.matches()) {
          health = m.group("health").replace(",", "").toInt()
          maxHealth = m.group("maxHealth").replace(",", "").toInt()
        }
      }
    }
  }

  private enum class State {
    SHOULD_ENABLE, SWITCH_ITEM, CLICK_ITEM, SWITCH_BACK
  }
  companion object {
    private var healingTimer = Timer(0)
    private var tubaTimer = Timer(0)
    private var orbTimer = Timer(0)
    var enable: Boolean = false
  }

}