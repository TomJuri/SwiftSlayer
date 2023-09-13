package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.PathingUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import dev.macrohq.swiftslayer.util.SlayerUtil
import dev.macrohq.swiftslayer.util.config
import dev.macrohq.swiftslayer.util.getStandingOnCeil
import dev.macrohq.swiftslayer.util.player
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class GenericBossKiller {

  var enabled = false
    private set
  private lateinit var target: EntityLiving
  private var hasRotated = false

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!enabled) return
    if (!::target.isInitialized) {
      val t = SlayerUtil.getBoss()
      if (t != null) target = t.first
      else return
    }
    if (target.isDead) {
      Logger.info("Boss killed.")
      disable()
      return
    }
    if (player.getDistanceToEntity(target) <= 1.5) PathingUtil.stop()
    if (player.getDistanceToEntity(target) > 1.5 && PathingUtil.isDone) PathingUtil.goto(target.getStandingOnCeil())
    if (config.bossKillerWeapon == 1) {
      player.inventory.currentItem = 0
      if (player.getDistanceToEntity(target) < 2) {
        RotationUtil.lock(target, 500, true, true)
        KeyBindUtil.leftClick(10)
      } else {
        RotationUtil.stop()
        KeyBindUtil.stopClicking()
      }
    }
  }

  fun enable() {
    if (enabled) return
    Logger.info("Enabling GenericBossKiller")
    RotationUtil.stop()
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