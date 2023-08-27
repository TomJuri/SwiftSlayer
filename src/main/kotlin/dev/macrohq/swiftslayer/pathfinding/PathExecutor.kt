package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.block.BlockSlab
import net.minecraft.block.BlockStairs
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.sqrt

class PathExecutor {
    private var path = listOf<BlockPos>()
    private var current: BlockPos? = null
    var running = false
    var directionYaw = 0f

    fun executePath(inputPath: List<BlockPos>) {
        path = inputPath
        if (running || path.isEmpty()) return
        current = path[0]
        running = true
        directionYaw = player.rotationYaw
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!running) return
        if(path.any{it.x == player.getStandingOn().x && it.z == player.getStandingOn().z && abs(it.y-player.getStandingOn().y) < 10}){
            info("player is on a block in the list")
            if (player.getStandingOn().x == path[path.size-1].x && player.getStandingOn().z == path[path.size-1].z){
                info("player is on the end block")
                running = false
                gameSettings.keyBindSprint.setPressed(false)
                gameSettings.keyBindForward.setPressed(false)
                gameSettings.keyBindJump.setPressed(false)
                return
            }
            current = path[path.indexOf(path.find { it.x == player.getStandingOn().x && it.z == player.getStandingOn().z }) + 1]
            path.dropWhile {it!=current}
        }
        RenderUtil.markers.clear();
        current?.let { RenderUtil.markers.add(it) };
        movePlayer(current!!)
    }

    fun disable() {
        running = false
    }

    private fun movePlayer(current: BlockPos) {
        val jump = (player.onGround && current.y > player.posY - 1 && world.getBlockState(current).block !is BlockSlab && world.getBlockState(current).block !is BlockStairs && sqrt(player.getDistanceSqToCenter(current)) < 1)
        val rotation = RotationUtil.Rotation(AngleUtil.getAngles(current.toVec3()).yaw, 0f)
        directionYaw = rotation.yaw
        gameSettings.keyBindSprint.setPressed(true)
        gameSettings.keyBindForward.setPressed(true)
        if(jump) player.jump()
    }
}
