package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityWolf
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.abs

class MobKiller {
    private var blacklist = mutableListOf<EntityLiving>()
    private var mobKiller = false
    private var state: State = State.NONE
    private var targetEntity: Entity? = null
    private var ticks: Int = 0
    private var stuckCounter: Int = 0
    private enum class State{
        NONE,
        STARTING,
        FINDING,
        PATHFINDING,
        PATHFINDING_VERIFY,
        LOOKING,
        LOOKING_VERIFY,
        KILLING,
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent){
        if(player == null || world == null) return
        if(!mobKiller) return
        ticks++
        if(player.lastTickPosition().add(0,-1,0) == player.getStandingOnFloor()){stuckCounter++}
        else stuckCounter=0

        when(state){
            State.STARTING -> {
                state = State.FINDING
                return
            }
            State.FINDING -> {
                RenderUtil.entites.clear()
                if(ticks>=60){
                    blacklist.clear()
                    ticks = 0
                }
                val targetEntityList = EntityUtil.getMobs(EntitySpider::class.java, 32000).toMutableList()
                if(targetEntity!=null) targetEntityList.remove(targetEntity)
                targetEntityList.removeAll(blacklist)
                if(targetEntityList.isEmpty()) return
                targetEntity = targetEntityList[0]
                RenderUtil.entites.add(targetEntity as EntityLiving)
                state = State.PATHFINDING
                if(targetEntity==null) disable()
            }
            State.PATHFINDING -> {
                PathingUtil.goto(targetEntity!!.position.down())
                state = State.PATHFINDING_VERIFY
            }
            State.PATHFINDING_VERIFY -> {
                if(PathingUtil.hasFailed() || (targetEntity as EntityLiving).health <= 0 || stuckCounter>=40){
                    PathingUtil.stop()
                    stuckCounter = 0
                    blacklist.add(targetEntity as EntityLiving)
                    state = State.FINDING
                    return
                }
                if(PathingUtil.isDone || player.getDistanceToEntity(targetEntity) < 6){
                    PathingUtil.stop()
                    state = State.LOOKING
                }
                return
            }
            State.LOOKING ->{
                RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(targetEntity!!).yaw, 45f), 400)
                state = State.LOOKING_VERIFY
                return
            }
            State.LOOKING_VERIFY -> {
                val yp = RotationUtil.Rotation(AngleUtil.getAngles(targetEntity!!).yaw, 45f)
                val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw)-AngleUtil.yawTo360(yp.yaw))
                val pitchDiff = abs(mc.thePlayer.rotationPitch - yp.pitch)
                if(pitchDiff < 2){
                    RotationUtil.stop()
                    InventoryUtil.holdItem("Spirit")
                    state = State.KILLING
                }
            }
            State.KILLING -> {
                KeyBindUtil.rightClick()
                blacklist.add(targetEntity as EntityLiving)
                state = State.FINDING
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