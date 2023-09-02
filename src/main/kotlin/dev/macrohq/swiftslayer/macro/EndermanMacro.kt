package dev.macrohq.swiftslayer.macro

import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class EndermanMacro {

    private var enabled = false
    private lateinit var target: EntityArmorStand
    private var state = State.HIT
    lateinit var lasers: Pair<Vec3, Vec3>
    var lastLaser = 0L

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if(!enabled) return
        when(state) {
            State.HIT -> {
                if(determineState() != State.HIT) state = State.DAMAGE
            }

            State.DAMAGE -> TODO()
            State.LASER -> TODO()
        }
    }

    private fun determineState() : State {
        if(target.name.contains("Hits"))
            return State.HIT
        if(::lasers.isInitialized && lastLaser + 100 > System.currentTimeMillis())
            return State.LASER
        return State.DAMAGE
    }

    enum class State {
        HIT,
        DAMAGE,
        LASER,
    }

    enum class HitState {
        SWITCH_TO_REAPER_SCYTHE,
        USE_REAPER_SCYTHE,
        STAY_AWAY
    }

    enum class DamageState {
        HIT
    }

    enum class LaserState {
        SWITCH_TO_SOUL_WHIP,
        USE_SOUL_WHIP,
        JUMP
    }
}