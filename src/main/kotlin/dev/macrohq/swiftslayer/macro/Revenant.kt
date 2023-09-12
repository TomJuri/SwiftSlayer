package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityZombie
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class Revenant {
    val MINIBOSSES = arrayOf("Revenant Sycophant", "Revenant Champion", "Deformed Revenant", "Atoned Champion", "Atoned Revenant")
    private var mob: EntityZombie? = null
    private var armorStandBlacklist = mutableListOf<EntityArmorStand>()
    private var found = false
    private var enabled = false
    private var state: State = State.STARTING
    enum class State{
        STARTING,
        MOBKILLER,
        CHECKING,
        WALKING,
        WALKING_VERIFY,
        LOOKING,
        KILLING
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent){
        if(!enabled) return
        if(mob!=null){
            info("mobhealth: ${mob!!.health}, isAlive: ${mob!!.isEntityAlive}")
        }

        when(state){
            State.STARTING -> {
                state = State.MOBKILLER
            }
            State.MOBKILLER -> {
                mobKiller.enable()
                state = State.CHECKING
            }
            State.CHECKING -> {
//                info("in checking. found: ${found}")
                if(found){
                    info("mob found disabling")
                    mobKiller.disable()
                    state = State.WALKING
                }
            }
            State.WALKING -> {
                info("walking. distance to entity: ${player.getDistanceToEntity(mob)>5}")
                if(player.getDistanceToEntity(mob)>5){
                    info("going")
                    PathingUtil.goto(mob!!.position.down())
                    state = State.WALKING_VERIFY
                    return
                }
                state = State.LOOKING
            }
            State.WALKING_VERIFY -> {
//                info("walking verify. info: ${PathingUtil.isDone}")
                if(PathingUtil.isDone || player.getDistanceToEntity(mob)<5){
                    info("done going")
                    PathingUtil.stop()
                    state = State.LOOKING
                }
            }
            State.LOOKING -> {
                info("looking")
                if(config.bossKillerWeapon==0) RotationUtil.ease(RotationUtil.Rotation(player.rotationYaw, 90f), 200)
                else RotationUtil.lock(mob!!, 200, false,true)
                state = State.KILLING
            }
            State.KILLING -> {
//                info("killing")
//                info("distance to entity: ${player.getDistanceToEntity(mob!!)}")
//                info("mobhealth: ${mob!!.health}")
                if(player.getDistanceToEntity(mob!!)<5 && mob!!.health > 0) {
                    if (config.bossKillerWeapon == 0) KeyBindUtil.rightClick(10)
                    else KeyBindUtil.leftClick(10)
                }
                else{
                    KeyBindUtil.stopClicking()
                }
                if(mob!!.isEntityAlive) return

                KeyBindUtil.stopClicking()
                mob = null
                found = false
                state = State.STARTING
            }
        }
    }

    @SubscribeEvent
    fun onEntityRender(event: RenderLivingEvent.Pre<EntityLivingBase>){
        if(!enabled) return
        if(state != State.CHECKING) return
        if(event.entity in armorStandBlacklist) return
        if(event.entity is EntityArmorStand){
            if(!event.entity.hasCustomName()) return
            val name = (event.entity as EntityArmorStand).displayName.unformattedText
            if(MINIBOSSES.any { name.contains(it) } || name.contains("Revenant Horror")){
                mob = world.getLoadedEntityList().filterIsInstance<EntityZombie>().minByOrNull { it.getDistanceToEntity(event.entity) }
                if(mob==null) return

                armorStandBlacklist.clear()
                armorStandBlacklist.add(event.entity as EntityArmorStand)

                found = true
                RenderUtil.entites.remove(mob!!)
                RenderUtil.entites.add(mob!!)
            }
        }
    }

    fun enable(){
        info("enabling rev")
        enabled = true
        mob = null
        found = false
        state = State.STARTING
    }

    fun disable(){
        enabled = false
        RotationUtil.stop()
        PathingUtil.stop()
        KeyBindUtil.stopClicking()
    }
}