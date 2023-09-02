package dev.macrohq.swiftslayer.macro

import cc.polyfrost.oneconfig.utils.dsl.tick
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import kotlinx.coroutines.currentCoroutineContext
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import javax.swing.text.JTextComponent.KeyBinding

class MobKiller {
    private var blacklist = mutableListOf<EntityLiving>()
    private var mobKiller = false
    private var state: State = State.NONE;
    private var targetEntity: Entity? = null
    private var ticks: Int = 0
    private enum class State{
        NONE,
        STARTING,
        FINDING,
        PATHFINDING,
        PATHFINDING_VERIFY,
        KILLING,
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent){
        if(player == null || world == null) return
        if(!mobKiller) return
        ticks++

        when(state){
            State.STARTING -> {
                state = State.FINDING
                return
            }
            State.FINDING -> {
                RenderUtil.entites.clear()
                if(ticks>=60){
                    blacklist.clear()
                    ticks = 0;
                }
                val targetEntityList = EntityUtil.getMobs(EntityZombie::class.java, 1999).toMutableList()
                if(targetEntity!=null) targetEntityList.remove(targetEntity)
                targetEntityList.removeAll(blacklist)
//                if(targetEntityList.size==0){
//                    info("size 0"); disable(); return;}
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
                if(PathingUtil.isDone || player.getDistanceToEntity(targetEntity) < 12){
                    PathingUtil.stop()
                    gameSettings.keyBindSneak.setPressed(true)
                    RotationUtil.easeToEntity(targetEntity as EntityLiving, 100, true)
                    info("Arrived at Target Mob! Kill Mob.")
                    state = State.KILLING
                }
                return
            }
            State.KILLING -> {
                info("Killing")
                gameSettings.keyBindSneak.setPressed(false)
                KeyBindUtil.rightClick()
                RotationUtil.stop()
                blacklist.add(targetEntity as EntityLiving)
                state = State.FINDING
                ticks = 0
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
        PathingUtil.stop()
        RotationUtil.stop()
        state = State.NONE
        Logger.error("Disabling")
    }
}