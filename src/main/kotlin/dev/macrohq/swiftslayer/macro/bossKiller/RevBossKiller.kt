package dev.macrohq.swiftslayer.macro.bossKiller

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.macro.mobKillers.RevMobKiller
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.movement.CalculationContext
import me.kbrewster.eventbus.Subscribe
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos
import net.minecraftforge.common.MinecraftForge
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


    private var rotState: RotationState = RotationState.LOOK_AT_TARGET
    private var movState: MovementState = MovementState.FIND_BLOCK

    private var currentAngle: Angle = Angle(AngleUtil.yawTo360(player.rotationYaw), player.rotationPitch)

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
                            for (block: BlockPos in BlockUtil.getBlocks(player.position, 15, 5, 15)) {
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
                        if(chosenBlock == null || findNewBlock) {
                            findBlock@
                            for (block: BlockPos in BlockUtil.getBlocks(BlockPos(mc.thePlayer.posX + mc.thePlayer.getLookVec().xCoord * -25, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.getLookVec().zCoord * -25), 15, 5, 15)) {
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
        //        AutoRotation.disable()
                if(chosenBlock == null) {
                    movState = MovementState.FIND_BLOCK
                    Logger.info("null block")
                    return
                }

                PathingUtil.goto(chosenBlock!!, null, false)
                blockTimer = Timer(6000)
                movState = MovementState.VERIFY_PATHFINDIG
                return
            }

            MovementState.VERIFY_PATHFINDIG -> {


                if(blockTimer.isDone) {
                    movState = MovementState.FIND_BLOCK
                    Logger.info("ahahrh")
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
                    if(BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer), player.getStandingOnCeil(), chosenBlock!!)) {
                        movState = MovementState.GOTO_BLOCK
                    } else {
                        findNewBlock = true
                        movState = MovementState.FIND_BLOCK
                        chosenBlock = null
                    }
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
        Logger.log(rotState.name)
        when (rotState) {
            RotationState.LOOK_AT_TARGET -> {
                if(!currentTarget!!.onGround) return

                lookAtEntity(currentTarget!!)

            }

            RotationState.VERIFY_LOOKING -> {
                if(mc.objectMouseOver.entityHit == currentTarget) return
                if (mc.objectMouseOver.entityHit != null && player.getDistanceToEntity(currentTarget) <= attackDistance() && player.canEntityBeSeen(currentTarget)) {
                    holdWeapon()
                    rotTimer = Timer(15)
                   // return
                }

            }
        }



    }

    override fun enable() {
        if(SlayerUtil.getFakeBoss() != null) {
            currentTarget = SlayerUtil.getFakeBoss()
        }
        if(SlayerUtil.getFakeBoss() == null) return

        if(currentTarget == null) {
            Logger.info("null entity!?")
            return
        }
        if(!enabled) SwiftEventBus.register(this)
        enabled = true
        movState = MovementState.FIND_BLOCK
        rotState = RotationState.LOOK_AT_TARGET
        chosenBlock = null
        player.inventory.currentItem = SwiftSlayer.config.meleeWeaponSlot - 1
    }

    override fun disable() {
        if(enabled) MinecraftForge.EVENT_BUS.unregister(this)
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