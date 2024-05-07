package dev.macrohq.swiftslayer.macro.mobKillers

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.macro.AbstractMobKiller
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.InventoryUtil.getHotbarSlotForItem
import me.kbrewster.eventbus.Subscribe
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.BlockPos
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
    var rotTimer: Timer = Timer(0)
    override var tickCounter: Int = 0
    override var ticksSinceLastClick: Int = 0
    override var ticksSinceLastMovement: Int = 0
    var lastTargetPos: BlockPos? = null

    private var state: State = State.CHOOSE_TARGET
    @Subscribe
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        if(SwiftSlayer.autoBatphone.enabled) return

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

        if(BlockUtil.getXZDistance(player.lastTickPositionCeil(), player.getStandingOnCeil()) < 0.2) {
            ticksSinceLastMovement++
        } else {
            ticksSinceLastMovement = 0
        }

        if(ticksSinceLastMovement > 60 && (state == State.GOTO_TARGET || state == State.VERIFY_PATHFINDING)){
            ticksSinceLastMovement = 0
            blacklist.add(currentTarget!!)
            state = State.CHOOSE_TARGET
            Logger.info("stuck for 3 seconds!")
            PathingUtil.stop()
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
        }


        if(state != State.KILL_TARGET) {
            KeyBindUtil.stopClicking()
        }

        Logger.info(state.name)
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
                //AutoRotation.disable()
                if(!currentTarget!!.onGround || !player.onGround) return
                thread = Thread {
                    PathingUtil.stop()
                    //AutoRotation.disable()
                    if (player.canEntityBeSeen(currentTarget!!)) {
                        PathingUtil.goto(currentTarget!!.getStandingOnCeil(), currentTarget!!)
                        looking = true
                    } else {
                        PathingUtil.goto(currentTarget!!.getStandingOnCeil())
                        looking = false
                    }

                    state = State.VERIFY_PATHFINDING
                }
                thread!!.start()

                state = State.SUSPENDED
                return
            }

            State.VERIFY_PATHFINDING -> {
                if (player.getDistanceToEntity(currentTarget) < attackDistance() && player.canEntityBeSeen(currentTarget)) {
                    stopWalking()
                    state = State.LOOK_AT_TARGET
                    return
                }

                if(PathingUtil.hasFailed) {
                    Logger.info("Failed pathfinding :( finding next target")
                    blacklist.add(currentTarget!!)
                    state = State.CHOOSE_TARGET
                    return
                }

                if(PathingUtil.isDone && player.getDistanceToEntity(currentTarget!!) > attackDistance()) {
                    state = State.GOTO_TARGET
                    return
                }

            }

             State.LOOK_AT_TARGET -> {
                 if(!rotTimer.isDone) return
                 PathingUtil.stop()
                if(currentTarget != null) {
                    lookAtEntity(currentTarget!!)
                    rotTimer = Timer(100)
                } else {
                    Logger.info("failed to look at target!")
                    state = State.CHOOSE_TARGET
                    return
                }
                state = State.VERIFY_LOOKING
                return
            }

            State.VERIFY_LOOKING -> {

               if(player.getDistanceToEntity(currentTarget) > attackDistance() || !player.canEntityBeSeen(currentTarget)) {
                   state = State.GOTO_TARGET
                   return
               }
                when(config.mobKillerWeapon) {
                    0 -> {
                        state = State.KILL_TARGET
                    }

                    1 -> {
                        state = if(lookDone()) {
                            State.KILL_TARGET

                        } else {
                            State.LOOK_AT_TARGET
                        }
                    }

                    2 -> {
                        if(mc.objectMouseOver.entityHit != null) {
                            state = State.KILL_TARGET
                        } else {
                            state = State.LOOK_AT_TARGET
                            return
                        }
                    }
                }



            }

            State.KILL_TARGET -> {


                when(config.mobKillerWeapon) {
                    //hyperion
                    0 -> {
                        if(ticksSinceLastClick > 4)
                        KeyBindUtil.rightClick(1)
                    }

                    //ranged
                    1 -> {
                        if(ticksSinceLastClick > 4 && lookDone() && waitTimer.isDone) {
                            KeyBindUtil.rightClick(1)
                            waitTimer = Timer((config.rangedCooldown * 1000).toLong()) }
                        else if(!lookDone()) {
                            state = State.LOOK_AT_TARGET
                            return
                        }

                    }

                    //melee
                    2 -> {

                        if(mc.objectMouseOver.entityHit != null) {
                        KeyBindUtil.leftClick(5) }
                        else if(mc.objectMouseOver.entityHit == null){
                            state = State.LOOK_AT_TARGET
                            return
                        }

                    }
                }
                ticksSinceLastClick = 0
                if (currentTarget!!.isDead || currentTarget!!.health < 1) {
                    blacklist.add(currentTarget!!)
                    state = State.CHOOSE_TARGET
                    return
                }
                return
            }

            State.SUSPENDED -> {
                // my nuts are here
            }
        }
    }




    override fun enable() {
        var scoreBoard = ScoreboardUtil
        if(!scoreBoard.getScoreboardLines().contains("Coal Mine")) {
            Logger.info("You must enter the crypt ghoul cave to activate this macro.")
            macroManager.toggle()
            return
        }



        if(!enabled)
        SwiftEventBus.register(this)
        enabled = true
        if(currentTarget != null) {
            state = State.CHOOSE_TARGET
            return
        }
        //aaamc.thePlayer.inventory.currentItem = config.meleeWeaponSlot - 1
    }

    override fun disable() {
        if(enabled) SwiftEventBus.unregister(this)
        enabled = false


    }

    override fun pause() {
        paused = true
    }

    private enum class State {
        CHOOSE_TARGET, GOTO_TARGET, VERIFY_PATHFINDING, LOOK_AT_TARGET, VERIFY_LOOKING, KILL_TARGET,SUSPENDED
    }


    companion object{
        private var instance: RevMobKiller? = null
        fun getInstance(): RevMobKiller{
            if(instance == null){
                instance = RevMobKiller()
            }
            return instance!!
        }

        var thread: Thread? = null
    }


}