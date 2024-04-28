package dev.macrohq.swiftslayer.codecPathfinder.Pathfinder.dependencies

import dev.macrohq.swiftslayer.util.BlockUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs

class CodecPathexecutor {
    var wayPoints: List<BlockPos>? = null
    var currentPoint: Int = 0

    fun start( wp: List<BlockPos>?) {
        Logger.info("yeetsg")
        wayPoints = wp
        currentPoint = 0
        DataAccessor.shouldWalk = true
        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindForward.keyCode, true)
        MinecraftForge.EVENT_BUS.register(this)

    }

    fun stop() {
        DataAccessor.shouldWalk = false
        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindForward.keyCode, false)
        MinecraftForge.EVENT_BUS.unregister(this)
    }

    @SubscribeEvent
    fun clientTick(event: ClientTickEvent?) {
        if (currentPoint >= wayPoints!!.size) {
            stop()
            Logger.info("stp")
            return
        }

        DataAccessor.yawToWalkTo = RotationUtil.normalize(RotationUtil.getYaw(BlockPos(wayPoints!![currentPoint])))
        val deltaYaw: Float =
            RotationUtil.normalize(Minecraft.getMinecraft().thePlayer.rotationYaw - DataAccessor.yawToWalkTo)

        KeyBinding.setKeyBindState(
            Minecraft.getMinecraft().gameSettings.keyBindSprint.keyCode,
            deltaYaw >= -45 && deltaYaw <= 45
        )
        if (BlockUtil.getXZDistance(
                BlockPos(wayPoints!![currentPoint]),
                Minecraft.getMinecraft().thePlayer.position
            ) < 0.5 + abs(
                Minecraft.getMinecraft().thePlayer.posY - BlockPos(
                    wayPoints!![currentPoint]
                ).y
            )
        ) {
            currentPoint++
        }
    }
}