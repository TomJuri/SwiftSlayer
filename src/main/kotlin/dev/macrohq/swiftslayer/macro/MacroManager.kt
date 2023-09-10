package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class MacroManager {

    private var enabled = false
    private var state = State.ACTIVATE_QUEST

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!enabled || autoBatphone.enabled || mobKiller.enabled || genericBossKiller.enabled || endermanBossKiller.enabled) return
        when (state) {
            State.ACTIVATE_QUEST -> autoBatphone.enable(true)
            State.KILL_MOBS -> mobKiller.enable()
            State.KILL_BOSS -> {
                val boss =
                    player.worldObj.loadedEntityList.filterIsInstance<EntityLiving>().maxByOrNull { it.maxHealth }!!
                if (config.slayer == 3) endermanBossKiller.enable(boss)
                else genericBossKiller.enable(boss)
            }
        }
    }

    fun toggle() {
        if (!enabled) enable() else disable()
    }

    private fun enable() {
        if (enabled) return
        if (config.slayer != 0 && config.slayerTier == 4) {
            Logger.error("There's no tier 5 boss for this slayer.")
            return
        }
        Logger.info("Enabling macro.")
        enabled = true
    }

    private fun disable() {
        if (!enabled) return
        Logger.info("Disabling macro.")
        enabled = false
        autoBatphone.disable()
        mobKiller.disable()
        genericBossKiller.disable()
        endermanBossKiller.disable()
    }

    enum class State {
        ACTIVATE_QUEST,
        KILL_MOBS,
        KILL_BOSS
    }
}