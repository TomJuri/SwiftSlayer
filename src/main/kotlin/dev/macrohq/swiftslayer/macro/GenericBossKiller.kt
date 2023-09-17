package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.InventoryUtil
import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.PathingUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import dev.macrohq.swiftslayer.util.SlayerUtil
import dev.macrohq.swiftslayer.util.config
import dev.macrohq.swiftslayer.util.gameSettings
import dev.macrohq.swiftslayer.util.getStandingOnCeil
import dev.macrohq.swiftslayer.util.player
import dev.macrohq.swiftslayer.util.setPressed
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class GenericBossKiller {

  var enabled = false
    private set
  private var target: EntityLiving? = null

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!enabled) return
    target = SlayerUtil.getBoss()?.first
    if (target == null) return
    if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD) {
      Logger.info("Boss killed.")
      gameSettings.keyBindSneak.setPressed(false)
      disable()
      return
    }
    gameSettings.keyBindSneak.setPressed(true)

    // stay close to boss
    RotationUtil.lock(target!!, 850, true)
    if (player.getDistanceToEntity(target) > 2) {
      if (!PathingUtil.isDone) return
      PathingUtil.goto(target!!.getStandingOnCeil())
      return
    } else {
      PathingUtil.stop()
    }

    // Melee
    if (config.bossKillerWeapon == 0) {
      player.inventory.currentItem = config.meleeWeaponSlot - 1
      KeyBindUtil.leftClick(8)

      // Hyperion
    } else if (config.bossKillerWeapon == 1) {
      if (!InventoryUtil.holdItem("Heroic Spirit Sceptre")) {
        Logger.error("Hyperion not found in hotbar.")
        // macroManager.disable()
        return
      }
      RotationUtil.stop()
      RotationUtil.ease(RotationUtil.Rotation(player.rotationYaw, 90f), 350, true)
      KeyBindUtil.rightClick(8)
    }
  }

  fun enable() {
    if (enabled) return
    Logger.info("Enabling GenericBossKiller")
    RotationUtil.stop()
    PathingUtil.stop()
    target = null
    enabled = true
  }

  fun disable() {
    if (!enabled) return
    Logger.info("Disabling GenericBossKiller")
    enabled = false
    RotationUtil.stop()
    PathingUtil.stop()
    KeyBindUtil.stopClicking()
  }
}