package dev.macrohq.swiftslayer.macro.bossKiller

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.macro.mobKillers.RevMobKiller
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.movement.CalculationContext
import me.kbrewster.eventbus.Subscribe
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class RevBossKiller:AbstractBossKiller() {

    override var enabled: Boolean = false
    override var paused: Boolean = false
    override var tickCounter: Int = 0
    override var ticksSinceLastMovement: Int = 0
    override var currentTarget: EntityLiving? = null
    private var findNewBlock: Boolean = true
    private var rotTimer: Timer = Timer(0)
    private var blockTimer: Timer = Timer(0)
    private var pausedPathExec: Boolean = false
    private var iteration: Int = 0
    private var centerBlock: BlockPos? = null
    private var rotState: RotationState = RotationState.LOOK_AT_TARGET
    private var movState: MovementState = MovementState.FIND_BLOCK


    @Subscribe
    fun movement(event: ClientTickEvent) {
        if(!enabled || paused || currentTarget == null) return

        if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD) {
            Logger.info("Boss killed.")
            disable()
            return
        }
     //   Logger.info(rotState.name)
       //Logger.info(movState.name)
        if(currentTarget!!.isDead || currentTarget!!.health < 1) {
            Logger.info("boss dead. Enabling mob killer ")
            instance!!.disable()
            RevMobKiller.getInstance().enable()
            mc.gameSettings.keyBindSneak.setPressed(false)
            return
        }


     //movement (crazy)
        when(movState) {
            MovementState.FIND_BLOCK -> {
                when(SwiftSlayer.config.movementType) {
                    0 -> { // find corner
                        if(chosenBlock == null || findNewBlock) {
                            findCorner@
                            for (block: BlockPos in BlockUtil.getBlocks(centerBlock!!, 15, 5, 15)) {
                                if (BlockUtil.isSingleCorner(block) && BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer), player.getStandingOnCeil(), block)) {
                                    chosenBlock = block
                                    break@findCorner
                                }
                            }
                        }
                        movState = MovementState.GOTO_BLOCK
                        return
                    }

                    1 -> { // move back
                        if(iteration < 5) centerBlock = BlockPos(mc.thePlayer.posX + mc.thePlayer.getLookVec().xCoord * -10, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.getLookVec().zCoord * -10)

                        if(chosenBlock == null || findNewBlock) {
                            findBlock@
                            for (block: BlockPos in BlockUtil.getBlocks(centerBlock!!, 6, 4, 6)) {
                                if (BlockUtil.getXZDistance(player.getStandingOnCeil(), block) > 6 && BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer), player.getStandingOnCeil(), block) && !BlockUtil.isSingleCorner(block)) {
                                    chosenBlock = block
                                    break@findBlock
                                }
                            }
                        }
                        movState = MovementState.GOTO_BLOCK
                        findNewBlock = false
                        return
                    }
                }
            }

            MovementState.GOTO_BLOCK -> {
                if(iteration > 5 && config.movementType == 1) {
                    centerBlock = player.getStandingOnCeil()
                    iteration = 0
                    movState = MovementState.FIND_BLOCK
                    return
                }


                if(chosenBlock == null) {
                    movState = MovementState.FIND_BLOCK
                    Logger.info("null block")
                    iteration++
                    return
                }

                PathingUtil.goto(chosenBlock!!, null, false)
                blockTimer = Timer(6000)
                movState = MovementState.VERIFY_PATHFINDIG
                return
            }

            MovementState.VERIFY_PATHFINDIG -> {

                if(blockTimer.isDone && SwiftSlayer.config.movementType == 1) {
                    movState = MovementState.FIND_BLOCK
                    return
                }
                //movement type = walking back
                if(player.getDistanceToEntity(currentTarget!!) > attackDistance() && !pausedPathExec) {
                    PathingUtil.stop()
                    pausedPathExec = true
                    return
                }

                if(pausedPathExec && player.getDistanceToEntity(currentTarget!!) <= attackDistance() && chosenBlock != null) {
                    PathingUtil.goto(chosenBlock!!, null, false)
                    pausedPathExec = false
                    return
                }


                if(chosenBlock == null) {
                    Logger.info("null chosen block, crazzy")
                    movState = MovementState.FIND_BLOCK
                    return
                }
                if(BlockUtil.getXZDistance(player.getStandingOnCeil(), chosenBlock!!) < 1 && SwiftSlayer.config.movementType == 1 ) {
                    findNewBlock = true
                    Logger.info("in need of new block!")
                    movState = MovementState.FIND_BLOCK
                    chosenBlock = null
                    return
                }

                //movement type = find corner
                if(BlockUtil.getXZDistance(player.getStandingOnCeil(), chosenBlock!!) < 1 && SwiftSlayer.config.movementType == 0) {
                        movState = MovementState.GOTO_BLOCK

                    return
                }
            }
        }


    }

    @Subscribe
    fun rotation(event: ClientTickEvent) {
        if (!enabled || paused || currentTarget == null) return


        if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD) {
            Logger.info("Boss killed.")
            disable()

            return
        }
        if(!player.onGround) return

        if(tickCounter < 20) {
            tickCounter++
        } else {
            tickCounter = 0
        }
        if(player.getDistanceToEntity(currentTarget) < attackDistance() && player.canEntityBeSeen(currentTarget)) {
            KeyBindUtil.leftClick(8)
        } else {
            KeyBindUtil.stopClicking()
        }

        if (!mc.gameSettings.keyBindSneak.isPressed) mc.gameSettings.keyBindSneak.setPressed(true)
        //rotation (and clicking)
        Logger.info(rotState.name)
        when (rotState) {
            RotationState.LOOK_AT_TARGET -> {
                if(!currentTarget!!.onGround) return
                if(mc.objectMouseOver.entityHit == null && !rotTimer.isDone) return
                lookAtEntity(currentTarget!!)

                if(mc.objectMouseOver.entityHit != null) {
                    rotTimer = Timer(50)
                }
                rotState = RotationState.VERIFY_LOOKING
            }

            RotationState.VERIFY_LOOKING -> {
                if(mc.objectMouseOver.entityHit == currentTarget) return
                if (mc.objectMouseOver.entityHit != null && player.getDistanceToEntity(currentTarget) <= attackDistance() && player.canEntityBeSeen(currentTarget)) {
                    //holdWeapon()
                    rotTimer = Timer(15)
                    return
                }
                if(mc.objectMouseOver.entityHit == null)
                    rotState = RotationState.LOOK_AT_TARGET
                    return

            }
        }



    }

    override fun enable() {
        if(SlayerUtil.getBoss() != null) {
            currentTarget = SlayerUtil.getBoss()!!.first
        }
        if(SlayerUtil.getBoss() == null) return

        if(currentTarget == null) {
            Logger.info("null entity!?")
            return
        }
        if(!enabled) SwiftEventBus.register(this)
        enabled = true
        movState = MovementState.FIND_BLOCK
        rotState = RotationState.LOOK_AT_TARGET
        chosenBlock = null
        iteration = 0
        when(config.movementType) {
            0 ->{
                centerBlock = player.getStandingOnCeil()
            }

            1 -> {
                centerBlock = BlockPos(mc.thePlayer.posX + mc.thePlayer.getLookVec().xCoord * -10, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.getLookVec().zCoord * -10)
            }

        }
        player.inventory.currentItem = SwiftSlayer.config.meleeWeaponSlot - 1
    }

    override fun disable() {
        if(enabled) SwiftEventBus.unregister(this)
        enabled = false
        chosenBlock = null
        currentTarget = null
        ticksSinceLastMovement = 0
        tickCounter = 0
        PathingUtil.stop()
        KeyBindUtil.stopClicking()
        mc.gameSettings.keyBindSneak.setPressed(false)
    }

    override fun pause() {
        paused = true
    }

    private enum class RotationState {
        LOOK_AT_TARGET, VERIFY_LOOKING
    }

    private enum class MovementState {
        FIND_BLOCK, GOTO_BLOCK, VERIFY_PATHFINDIG
    }

    companion object{
        private var instance: RevBossKiller? = null
        fun getInstance(): RevBossKiller {
            if(instance == null){
                instance = RevBossKiller()
            }
            return instance!!
        }
        var chosenBlock: BlockPos? = null
    }
}