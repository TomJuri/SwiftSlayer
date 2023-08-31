package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.player
import dev.macrohq.swiftslayer.util.world
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class EndermanMacro {

    private var enabled = false
    private lateinit var target: EntityArmorStand
    private var state = State.HIT
    var lasers: Pair<Vec3?, Vec3?>? = null

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

    fun determineState() : State {
        if(target.name.contains("Hits"))
            return State.HIT
        return State.DAMAGE
    }

    enum class State {
        HIT,
        DAMAGE,
        LASER,
    }
}