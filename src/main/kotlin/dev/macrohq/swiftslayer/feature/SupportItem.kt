package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.macro.MacroManager
import dev.macrohq.swiftslayer.util.InventoryUtil
import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.SlayerUtil
import dev.macrohq.swiftslayer.util.Timer
import dev.macrohq.swiftslayer.util.config
import dev.macrohq.swiftslayer.util.macroManager
import dev.macrohq.swiftslayer.util.player
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.util.regex.Pattern

class SupportItem {
  private var maxHealth = -1
  private var health = -1
  private var healingTimer = Timer(0)
  private var tubaTimer = Timer(0)
  private var orbTimer = Timer(0)

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!macroManager.enabled) return
    if (config.useTuba && tubaTimer.isDone && macroManager.state == MacroManager.State.KILL_BOSS) {
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
    if (config.useHealing) {
      if (health == -1 || maxHealth == -1) return
      val healthPercent = health.toFloat() / maxHealth.toFloat()
      if (healthPercent > config.useHealingAt / 100f || !healingTimer.isDone) return
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
    if (config.deployOrb && SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_ALIVE && orbTimer.isDone) {
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

  @SubscribeEvent
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
}