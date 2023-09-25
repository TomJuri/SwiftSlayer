package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.PathingUtil
import dev.macrohq.swiftslayer.util.SlayerUtil
import dev.macrohq.swiftslayer.util.UnGrabUtil
import dev.macrohq.swiftslayer.util.autoBatphone
import dev.macrohq.swiftslayer.util.config
import dev.macrohq.swiftslayer.util.endermanBossKiller
import dev.macrohq.swiftslayer.util.genericBossKiller
import dev.macrohq.swiftslayer.util.mobKiller
import dev.macrohq.swiftslayer.util.tracker
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class MacroManager {

  var enabled = false
    private set
  var state = State.ACTIVATE_QUEST
    private set

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!enabled || autoBatphone.enabled || mobKiller.enabled || genericBossKiller.enabled || endermanBossKiller.enabled) return
    when (state) {
      State.ACTIVATE_QUEST -> {
        if (SlayerUtil.getActive() == null ||
          SlayerUtil.getActive()!!.first.name != SlayerUtil.getSlayerName()!!.uppercase().replace(" ", "_") ||
          SlayerUtil.getActive()!!.second.name != SlayerUtil.getTier() ||
          SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD
        ) {
          if (!config.autoSlayer)
            autoBatphone.enable()
        }
      }
      State.KILL_MOBS -> mobKiller.enable()
      State.KILL_BOSS -> {
        //if (config.slayer == 3) endermanBossKiller.enable()
        /* else*/ genericBossKiller.enable()
      }
    }
    state = State.entries[(state.ordinal + 1) % State.entries.size]
  }

  fun toggle() = if (!enabled) enable() else disable()

  private fun enable() {
    if (enabled) return
    if (config.slayer != 0 && config.slayerTier == 4) {
      Logger.error("There's no tier 5 boss for this slayer.")
      return
    }
    Logger.info("Enabling macro.")
    tracker.reset()
    UnGrabUtil.unGrabMouse()
    state = State.ACTIVATE_QUEST
    enabled = true
  }

  fun disable() {
    if (!enabled) return
    Logger.info("Disabling macro.")
    enabled = false
    autoBatphone.disable()
    mobKiller.disable()
    genericBossKiller.disable()
    endermanBossKiller.disable()
    KeyBindUtil.stopClicking()
    PathingUtil.stop()
    UnGrabUtil.grabMouse()
  }

  enum class State {
    ACTIVATE_QUEST,
    KILL_MOBS,
    KILL_BOSS
  }
}