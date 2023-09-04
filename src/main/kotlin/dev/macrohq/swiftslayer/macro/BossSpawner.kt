package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


class BossSpawner {

    private var enabled = false
    private var state = State.GOTO_MOB
    private var target: EntityLiving? = null
    private var condition = { true }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if(!enabled || !condition()) return
        if(target == null || target!!.isDead) target = EntityUtil.getMobs(EntityZombie::class.java, 100).firstOrNull()
        if(target == null) {
            Logger.error("Couldn't find any targets :(")
            disable()
            return
        }
        Logger.info(target!!.getDistanceSqToEntity(player))
        when(state) {
            State.GOTO_MOB -> {
                RenderUtil.entites.add(target!!)
                PathingUtil.goto(target!!.getStandingOnCeil())
                condition = { PathingUtil.isDone }
            }

            State.ROTATE_TO_MOB -> {

            }

            State.KILL_MOB -> {

            }
        }
        state = State.entries[(state.ordinal + 1) % State.entries.size]
    }

    fun enable(entity: Class<out EntityLiving>) {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    private enum class State {
        GOTO_MOB,
        ROTATE_TO_MOB,
        KILL_MOB,
    }

}
