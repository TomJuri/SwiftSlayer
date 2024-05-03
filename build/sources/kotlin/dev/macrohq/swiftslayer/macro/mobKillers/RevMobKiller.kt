package dev.macrohq.swiftslayer.macro.mobKillers

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.macro.AbstractMobKiller
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.InventoryUtil.getHotbarSlotForItem
import me.kbrewster.eventbus.Subscribe
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class RevMobKiller: AbstractMobKiller() {

    override var enabled: Boolean = false
    override var paused: Boolean = false
    override val featureName:String = "Revenant Mob Killer"
    override var currentTarget: EntityLiving? = null
    var looking: Boolean = false
    override val targetEntityClass: Class<out EntityLiving> = EntityZombie::class.java
    override var blacklist = mutableListOf<EntityLiving>()
    var waitTimer: Timer = Timer(0)
    override var tickCounter: Int = 0
    override var ticksSinceLastClick: Int = 0
    override var ticksSinceLastMovement: Int = 0
    var lastTargetPos: BlockPos? = null

    private var state: State = State.CHOOSE_TARGET
    @Subscribe
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        if(SwiftSlayer.autoBatphone.enabled) return

        InventoryUtil.closeGUI()
        if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_ALIVE) {
            disable()
            return
        }
        if(SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD && getHotbarSlotForItem("Maddox Batphone") != -1) {
            SwiftSlayer.autoBatphone.enable()
            return
        }



        if(tickCounter == 20) {
            tickCounter = 1
        } else {
            tickCounter++
        }

        ticksSinceLastClick++

        if(BlockUtil.getXZDistance(player.lastTickPositionCeil(), player.getStandingOnCeil()) < 0.5) {
            ticksSinceLastMovement++
        } else {
            ticksSinceLastMovement = 0
        }

        if(ticksSinceLastMovement > 60 && (state == State.GOTO_TARGET || state == State.VERIFY_PATHFINDING)){
            ticksSinceLastMovement = 0
            blacklist.add(currentTarget!!)
            state = State.CHOOSE_TARGET
            Logger.info("stuck for 3 seconds!")
            return
        }

        if(blacklistResetTimer.isDone) {
            blacklist.clear()
            blacklistResetTimer = Timer(1000)
        }

        if(currentTarget != null) {
            if (currentTarget!!.isDead || currentTarget!!.health < 1) {
                blacklist.add(currentTarget!!)
                state = State.CHOOSE_TARGET

            }

            if(player.canEntityBeSeen(currentTarget!!) && !looking) {
                state = State.GOTO_TARGET
            }
        }

       // Logger.log(state.name)
        when (state) {
            State.CHOOSE_TARGET -> {
                RenderUtil.entites.clear()
                val targetEntityList = EntityUtil.getMobs(targetEntityClass).toMutableList()
                targetEntityList.removeAll(blacklist)
                if(targetEntityList.isEmpty()) return
                if(currentTarget != null) lastTargetPos = currentTarget!!.position
                currentTarget = targetEntityList.first()
                if(currentTarget!!.totalArmorValue == 11) {
                    Logger.log("golden zombie! ignored")
                    blacklist.add(currentTarget!!)
                    return
                }
                RenderUtil.entites.add(currentTarget!!)
                state = State.GOTO_TARGET
                return
            }

            State.GOTO_TARGET -> {
                AutoRotation.disable()
                if(player.canEntityBeSeen(currentTarget!!)) {
                    PathingUtil.goto(currentTarget!!.getStandingOnCeil(), currentTarget, true)
                    looking = true
                }
                else {
                    PathingUtil.goto(currentTarget!!.getStandingOnCeil(), null, true)
                    looking = false

                }

                state = State.VERIFY_PATHFINDING
                return
            }

            State.VERIFY_PATHFINDING -> {
                if (PathingUtil.hasFailed || currentTarget!!.isDead) {
                    PathingUtil.stop()
                    blacklist.add(currentTarget!!)
                    state = State.CHOOSE_TARGET
                    return

                }
                if (player.getDistanceToEntity(currentTarget) < attackDistance()) {
                    stopWalking()
                    state = State.LOOK_AT_TARGET
                    return
                }

                if(PathingUtil.isDone && player.getDistanceToEntity(currentTarget) > attackDistance()) {
                    Logger.info("Failed pathfinding :( finding next target")
                    blacklist.add(currentTarget!!)
                    state = State.CHOOSE_TARGET
                }
                return
            }

             State.LOOK_AT_TARGET -> {
                //AutoRotation.disable()
                if(currentTarget != null) {
                    lookAtEntity(currentTarget!!)
                    Logger.info("done")
                } else {
                    Logger.info("failed to look at target!")
                    state = State.CHOOSE_TARGET
                    return
                }
                state = State.VERIFY_LOOKING
                return
            }

            State.VERIFY_LOOKING -> {
              //  if(AutoRotation.enabled) return
                if(mc.objectMouseOver.entityHit != null && player.getDistanceToEntity(currentTarget) <= attackDistance() && player.canEntityBeSeen(currentTarget)) {
                    holdWeapon()
                    state = State.KILL_TARGET
                    return
                } else if(mc.objectMouseOver.entityHit == null && player.getDistanceToEntity(currentTarget) > attackDistance()) {
                    if(!currentTarget!!.onGround) return
                    
                    state = State.GOTO_TARGET
                    return
                }

                if(!AutoRotation.enabled && mc.objectMouseOver.entityHit != currentTarget) {
                    state = State.LOOK_AT_TARGET
                    return
                }
            }

            State.KILL_TARGET -> {
                if(ticksSinceLastClick > 3) {
                    useWeapon()
                    ticksSinceLastClick = 0
                }

                if (currentTarget!!.isDead || currentTarget!!.health < 1) {
                    blacklist.add(currentTarget!!)
                    state = State.CHOOSE_TARGET
                    return
                }
                state = State.VERIFY_LOOKING
                return
            }

        }
    }




    override fun enable() {
        if(!enabled)
        SwiftEventBus.register(this)
        enabled = true
    }

    override fun disable() {
        if(enabled) MinecraftForge.EVENT_BUS.unregister(this)
        enabled = false


    }

    override fun pause() {
        paused = true
    }

    private enum class State {
        CHOOSE_TARGET, GOTO_TARGET, VERIFY_PATHFINDING, LOOK_AT_TARGET, VERIFY_LOOKING, KILL_TARGET,
    }


    companion object{
        private var instance: RevMobKiller? = null
        fun getInstance(): RevMobKiller{
            if(instance == null){
                instance = RevMobKiller()
            }
            return instance!!
        }


    }


}