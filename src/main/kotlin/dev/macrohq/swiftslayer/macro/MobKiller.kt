package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class MobKiller {
    private var mobKiller = false
    private var state: State = State.NONE;
    private var targetEntity: Entity? = null
    private enum class State{
        NONE,
        STARTING,
        FINDING,
        PATHFINDING,
        PATHFINDING_VERIFY,
        KILLING,
        KILLING_VERIFY
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent){
        if(player == null || world == null) return
        if(!mobKiller) return

        when(state){
            State.STARTING -> {
                state = State.FINDING
                return
            }
            State.FINDING -> {
                RenderUtil.entites.clear()
                val targetEntityList = EntityUtil.getMobs(EntityZombie::class.java, 1999).toMutableList()
                if(targetEntity!=null) targetEntityList.remove(targetEntity)
                targetEntity = targetEntityList[0]
                RenderUtil.entites.add(targetEntity as EntityLiving)
                state = State.PATHFINDING
                info("Set state to pathfinding.")
                if(targetEntity==null) disable()
                return
            }
            State.PATHFINDING -> {
                info("In pathfinding!")
                PathingUtil.goto(targetEntity!!.position.down())
                state = State.PATHFINDING_VERIFY
                return
            }
            State.PATHFINDING_VERIFY -> {
                info("In pathfinding verify!")
                if(PathingUtil.isDone){
                    info("Arrived at Target Mob! Kill Mob.")
                    state = State.KILLING
                }
                return
            }
            State.KILLING -> {
                info("Killing")
                disable()
                return
            }

            else -> {}
        }
    }

    fun enable(){
        mobKiller = true
        state = State.STARTING
    }

    fun disable(){
        mobKiller = false
        state = State.NONE
        Logger.error("Disabling")
    }
}