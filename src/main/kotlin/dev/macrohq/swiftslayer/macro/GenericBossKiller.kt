package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.AngleUtil
import dev.macrohq.swiftslayer.util.InventoryUtil
import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.PathingUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import dev.macrohq.swiftslayer.util.SlayerUtil
import dev.macrohq.swiftslayer.util.config
import dev.macrohq.swiftslayer.util.getStandingOnCeil
import dev.macrohq.swiftslayer.util.macroManager
import dev.macrohq.swiftslayer.util.player
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class GenericBossKiller {

  var enabled = false
    private set
  private var target: EntityLiving? = null
  private var hasRotated = false

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!enabled) return
    if (target == null) {
      val t = SlayerUtil.getBoss()
      if (t != null) target = t.first
      else return
    }
    if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD) {
      Logger.info("Boss killed.")
      disable()
      return
    }

    if (AngleUtil.getAngles(target!!).pitch < 70) {
      RotationUtil.lock(target!!, 350, true, true)
    } else {
      KeyBindUtil.stopClicking()
      val diffPos = PathingUtil.getDifferentPosition()
      if (diffPos == null || !PathingUtil.isDone) return
      RotationUtil.stop()
      PathingUtil.goto(diffPos)
      return
    }

    if (player.getDistanceToEntity(target) > 1.5 && PathingUtil.isDone) {
      PathingUtil.goto(target!!.getStandingOnCeil())
      return
    }

    // Melee
    if (config.bossKillerWeapon == 1) {
      if (player.inventory.currentItem != config.meleeWeaponSlot - 1)
        player.inventory.currentItem = config.meleeWeaponSlot - 1
      KeyBindUtil.leftClick(8)

      // Hyperion
    } else if (config.bossKillerWeapon == 0) {
      if (!InventoryUtil.holdItem("Hyperion")) {
        Logger.error("Hyperion not found in hotbar.")
        macroManager.disable()
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
    target = null
    enabled = true
    hasRotated = false
  }

  fun disable() {
    if (!enabled) return
    Logger.info("Disabling GenericBossKiller")
    enabled = false
    RotationUtil.stop()
    KeyBindUtil.stopClicking()
  }

  private enum class State {
    GOTO_BOSS,

  }
}