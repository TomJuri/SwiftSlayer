package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.macro.bossKiller.RevBossKiller
import dev.macrohq.swiftslayer.util.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class MacroManager {

  var enabled = false
    private set
  var state = State.ACTIVATE_QUEST
    private set

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    mc.gameSettings.fovSetting
    if (!enabled || mobKiller.getInstance().enabled || RevBossKiller.getInstance().enabled) return
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
      State.KILL_MOBS -> mobKiller.getInstance().enable()
      State.KILL_BOSS -> {
        //if (config.slayer == 3) endermanBossKiller.enable()
        /* else*/ RevBossKiller.getInstance().enable()
      }
    }
    state = State.entries[(state.ordinal + 1) % State.entries.size] // tf is this for?
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
    mobKiller.getInstance().disable()
    RevBossKiller.getInstance().disable()
    endermanBossKiller.disable()
    AutoRotation.getInstance().disable()
    KeyBindUtil.stopClicking()
    PathingUtil.stop()
    UnGrabUtil.grabMouse()
    RenderUtil.clearAll()
  }

  enum class State {
    ACTIVATE_QUEST,
    KILL_MOBS,
    KILL_BOSS
  }
}