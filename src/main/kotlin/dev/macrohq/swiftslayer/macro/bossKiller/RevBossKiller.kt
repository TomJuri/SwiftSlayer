package dev.macrohq.swiftslayer.macro.bossKiller

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class RevBossKiller:AbstractBossKiller() {

    override var enabled: Boolean = false
    override var paused: Boolean = false
    override var tickCounter: Int = 0
    override var ticksSinceLastMovement: Int = 0
    override var currentTarget: EntityLiving? = null

    private var rotState: RotationState = RotationState.LOOK_AT_TARGET
    private var movState: MovementState = MovementState.FIND_BLOCK


    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if(!enabled || paused || currentTarget == null) return


        if(!mc.gameSettings.keyBindSneak.isPressed) mc.gameSettings.keyBindSneak.setPressed(true)
        //rotation (and clicking)
        when(rotState) {
            RotationState.LOOK_AT_TARGET -> {
                lookAtEntity(currentTarget!!)
                rotState = RotationState.VERIFY_LOOKING
            }

            RotationState.VERIFY_LOOKING -> {
                if(mc.objectMouseOver.entityHit != null && player.getDistanceToEntity(currentTarget) <= attackDistance() && player.canEntityBeSeen(currentTarget)) {
                    holdWeapon()
                    KeyBindUtil.leftClick(8)
                } else if (mc.objectMouseOver.entityHit == null && player.getDistanceToEntity(currentTarget) <= attackDistance()) {
                    KeyBindUtil.stopClicking()
                    Logger.info("we going")
                    rotState = RotationState.LOOK_AT_TARGET
                }
            }

        }

        //movement (crazy)
        when(movState) {
            MovementState.FIND_BLOCK -> {
                when(SwiftSlayer.instance.config.movementType) {
                    0 -> { // find corner
                        if(chosenBlock == null) {
                            findCorner@
                            for (block: BlockPos in BlockUtil.getBlocks(player.position, 15, 5, 15)) {
                                if (BlockUtil.isSingleCorner(block) && BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), player.getStandingOnCeil(), block)) {
                                    chosenBlock = block
                                    break@findCorner
                                }
                            }
                        }
                        movState = MovementState.GOTO_BLOCK
                    }

                    1 -> { // move back
                        if(chosenBlock == null) {
                            findBlock@
                            for (block: BlockPos in BlockUtil.getBlocks(BlockPos(mc.thePlayer.posX + mc.thePlayer.getLookVec().xCoord * -5, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.getLookVec().zCoord * -5), 15, 5, 15)) {
                                if (BlockUtil.getXZDistance(player.getStandingOnCeil(), block) > 6 && BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), player.getStandingOnCeil(), block)) {
                                    chosenBlock = block
                                    break@findBlock
                                }
                            }
                        }
                        movState = MovementState.GOTO_BLOCK
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

                PathingUtil.goto(chosenBlock!!, currentTarget)
                movState = MovementState.VERIFY_PATHFINDIG
            }

            MovementState.VERIFY_PATHFINDIG -> {
                if(BlockUtil.getXZDistance(player.getStandingOnCeil(), chosenBlock!!) < 2 || PathingUtil.hasFailed) {
                    Logger.info("pather failed, trying agian")
                    movState = MovementState.GOTO_BLOCK
                }
            }
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