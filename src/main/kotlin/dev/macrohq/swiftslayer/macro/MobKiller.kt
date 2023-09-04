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
import kotlin.math.abs

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
        LOOKING,
        LOOKING_VERIFY,
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
                targetEntity = targetEntityList[0]
                RenderUtil.entites.add(targetEntity as EntityLiving)
                state = State.PATHFINDING
//                info("Set state to pathfinding.")
                if(targetEntity==null) disable()
                return
            }
            State.PATHFINDING -> {
//                info("In pathfinding!")
                PathingUtil.goto(targetEntity!!.position.down())
                state = State.PATHFINDING_VERIFY
                return
            }
            State.PATHFINDING_VERIFY -> {
//                info("In pathfinding verify!")
                info("hasfailed: ${PathingUtil.hasFailed()}")
                if(PathingUtil.hasFailed()){
                    PathingUtil.stop()
                    blacklist.add(targetEntity as EntityLiving)
                    state = State.FINDING
                }
                if(PathingUtil.isDone || player.getDistanceToEntity(targetEntity) < 6){
                    PathingUtil.stop()
//                    info("Arrived at Target Mob! Kill Mob.")
                    state = State.LOOKING
                }
                return
            }
            State.LOOKING ->{
                RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(targetEntity!!).yaw, 45f), 400)
                state = State.LOOKING_VERIFY
                return;
            }
            State.LOOKING_VERIFY -> {
                val yp = RotationUtil.Rotation(AngleUtil.getAngles(targetEntity!!).yaw, 45f)
                val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw)-AngleUtil.yawTo360(yp.yaw));
                val pitchDiff = abs(mc.thePlayer.rotationPitch - yp.pitch);
                if(pitchDiff < 2){
                    RotationUtil.stop()
//                    info("holding sceptre")
                    InventoryUtil.holdItem("Spirit")
                    state = State.KILLING
                }
            }
            State.KILLING -> {
//                gameSettings.keyBindSneak.setPressed(false)
                KeyBindUtil.rightClick()
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