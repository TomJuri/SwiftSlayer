package dev.macrohq.swiftslayer.macro

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.security.Key

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
                if(player.getDistanceSqToEntity(target) < 10.0) gameSettings.keyBindBack.setPressed(true) else gameSettings.keyBindBack.setPressed(false)
                if(player.worldObj.loadedEntityList.filterIsInstance<EntityZombie>().any { it.getDistanceSqToEntity(target) > 4.0 }) return
                InventoryUtil.holdItem("Reaper Scythe")
                KeyBindUtil.rightClick()
            }

            State.DAMAGE -> {
                RotationUtil.easeToEntity(target, 250, true)
                if(player.getDistanceSqToEntity(target) > 3.0) gameSettings.keyBindForward.setPressed(true) else gameSettings.keyBindForward.setPressed(false)
                runAsync {
                    while(state == State.DAMAGE) {
                        gameSettings.keyBindSneak.setPressed(true)
                        Thread.sleep(100)
                        KeyBindUtil.leftClick()
                        gameSettings.keyBindSneak.setPressed(false)
                    }
                }
            }

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