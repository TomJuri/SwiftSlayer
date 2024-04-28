package dev.macrohq.swiftslayer.macro.bossKiller

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs

class RevBossKiller:AbstractBossKiller() {

    override var enabled: Boolean = false
    override var paused: Boolean = false
    override var tickCounter: Int = 0
    override var ticksSinceLastMovement: Int = 0
    override var currentTarget: EntityLiving? = null
    private var findNewBlock: Boolean = true
    private var rotTimer: Timer = Timer(0)

    private var rotState: RotationState = RotationState.LOOK_AT_TARGET
    private var movState: MovementState = MovementState.FIND_BLOCK

    private var currentAngle: Angle = Angle(AngleUtil.yawTo360(player.rotationYaw), player.rotationPitch)

    @SubscribeEvent
    fun movement(event: ClientTickEvent) {
        if(!enabled || paused || currentTarget == null) return

     //   Logger.info(rotState.name)
       //Logger.info(movState.name)

     //movement (crazy)
        when(movState) {
            MovementState.FIND_BLOCK -> {
                when(SwiftSlayer.instance.config.movementType) {
                    0 -> { // find corner
                        if(chosenBlock == null || findNewBlock) {
                            findCorner@
                            for (block: BlockPos in BlockUtil.getBlocks(player.position, 15, 5, 15)) {
                                if (BlockUtil.isSingleCorner(block) && BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), player.getStandingOnCeil(), block)) {
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
                            Logger.info("getting called")
                            findBlock@
                            for (block: BlockPos in BlockUtil.getBlocks(BlockPos(mc.thePlayer.posX + mc.thePlayer.getLookVec().xCoord * -25, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.getLookVec().zCoord * -25), 15, 5, 15)) {
                                if (BlockUtil.getXZDistance(player.getStandingOnCeil(), block) > 6 && BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), player.getStandingOnCeil(), block) && !BlockUtil.isSingleCorner(block)) {
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
        //        AutoRotation.getInstance().disable()
                if(chosenBlock == null) {
                    Logger.info("Somehow chosenBlock is null? returning")
                    movState = MovementState.FIND_BLOCK
                    return
                }

                PathingUtil.goto(chosenBlock!!, null, false)
                movState = MovementState.VERIFY_PATHFINDIG
                return
            }

            MovementState.VERIFY_PATHFINDIG -> {
                //movement type = walking back
                if(BlockUtil.getXZDistance(player.getStandingOnCeil(), chosenBlock!!) < 1 && SwiftSlayer.instance.config.movementType == 1 ) {
                    findNewBlock = true
                    Logger.info("in need of new block!")
                    movState = MovementState.FIND_BLOCK
                    chosenBlock = null
                    return
                }

                //movement type = find corner
                if(BlockUtil.getXZDistance(player.getStandingOnCeil(), chosenBlock!!) < 1 && SwiftSlayer.instance.config.movementType == 0) {
                    if(BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), player.getStandingOnCeil(), chosenBlock!!)) {
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

    @SubscribeEvent
    fun rotation(event: ClientTickEvent) {
        if (!enabled || paused || currentTarget == null) return

        if (!mc.gameSettings.keyBindSneak.isPressed) mc.gameSettings.keyBindSneak.setPressed(true)
        //rotation (and clicking)
        Logger.info(rotState.name)
        when (rotState) {
            RotationState.LOOK_AT_TARGET -> {
                if(!rotTimer.isDone && AutoRotation.getInstance().enabled) return
                if (currentTarget != null) {
                    lookAtEntity(currentTarget!!)
                } else {
                    Logger.info("failed to look at target!")
                }
                rotState = RotationState.VERIFY_LOOKING
                return

            }

            RotationState.VERIFY_LOOKING -> {
                if (mc.objectMouseOver.entityHit != null && player.getDistanceToEntity(currentTarget) <= attackDistance() && player.canEntityBeSeen(currentTarget)) {
                    holdWeapon()
                    rotTimer = Timer(40)
                   // return
                } else if (mc.objectMouseOver.entityHit == null && player.getDistanceToEntity(currentTarget) > attackDistance()) {
                    Logger.info("entity far away")
                 //   return
                } else if (mc.objectMouseOver.entityHit == null && player.getDistanceToEntity(currentTarget) <= attackDistance() && !AutoRotation.getInstance().enabled && player.onGround) {
                    rotState = RotationState.LOOK_AT_TARGET
                }

                if(mc.objectMouseOver.entityHit == currentTarget && abs(player.rotationPitch - angleForWeapon(currentTarget!!).pitch) > 11 && !AutoRotation.getInstance().enabled && player.onGround) {
                    lookAtAnle(Target(Angle(AngleUtil.yawTo360(player.rotationYaw), angleForWeapon(currentTarget!!).pitch)))

                    Logger.info("huzzah")
                }
            }
        }

        if(player.getDistanceToEntity(currentTarget) < attackDistance() && player.canEntityBeSeen(currentTarget)) {
            KeyBindUtil.leftClick(8)
        } else {
            KeyBindUtil.stopClicking()
        }


    }

    override fun enable() {
        currentTarget = SlayerUtil.getFakeBoss()
        if(currentTarget == null) {
            Logger.info("null entity!?")
            return
        }
        if(!enabled) MinecraftForge.EVENT_BUS.register(this)
        enabled = true
        movState = MovementState.FIND_BLOCK
        rotState = RotationState.LOOK_AT_TARGET
        chosenBlock = null
    }

    override fun disable() {
        if(enabled) MinecraftForge.EVENT_BUS.unregister(this)
        enabled = false
        chosenBlock = null
        currentTarget = null
        ticksSinceLastMovement = 0
        tickCounter = 0
        AutoRotation.getInstance().disable()
        PathingUtil.stop()
        Logger.info("yoohoo")
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