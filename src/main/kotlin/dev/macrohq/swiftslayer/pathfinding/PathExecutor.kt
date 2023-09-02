package dev.macrohq.swiftslayer.pathfinding

import cc.polyfrost.oneconfig.utils.Multithreading.runAsync
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.block.BlockSlab
import net.minecraft.block.BlockStairs
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PathExecutor {
    private var path = listOf<BlockPos>()
    private var current: BlockPos? = null
    private var pathFailCounter = 0;
    var running = false
    private var done = false
    private var aotving = false
    var directionYaw = 0f

    fun executePath(inputPath: List<BlockPos>) {
        disable()
        done = false
        if (running || inputPath.isEmpty()) return
        path = inputPath
        current = path[0]
        running = true
        directionYaw = player.rotationYaw
        info("started. aotving: $aotving")
    }

    fun disable() {
        info("disabling")
        pathFailCounter = 0
        RotationUtil.stop()
        running = false
        path = listOf()
        aotving = false
        current = null
        directionYaw = 0f
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!running) return
        if(path.any{it.x == player.getStandingOn().x && it.z == player.getStandingOn().z && (player.getStandingOn().y-it.y) in 0..10}){
            if (player.getStandingOn().x == path[path.size-1].x && player.getStandingOn().z == path[path.size-1].z){
                done = true
                running = false
                aotving = false
                gameSettings.keyBindSprint.setPressed(false)
                gameSettings.keyBindForward.setPressed(false)
                gameSettings.keyBindJump.setPressed(false)
                return
            }
            pathFailCounter = 0
            current = path[path.indexOf(path.find { it.x == player.getStandingOn().x && it.z == player.getStandingOn().z }) + 1]
            RotationUtil.ease(RotationUtil.Rotation(AngleUtil.getAngles(current!!).yaw, 0f), 500)
        }
        else if(player.onGround) pathFailCounter++

        if(pathFailCounter>=100 && player.onGround){
            pathFailCounter = 0
            running = false
            PathingUtil.goto(path[path.size-1])
            return;
        }
        RenderUtil.markers.clear();
        current?.let { RenderUtil.markers.add(it) };
        movePlayer(current!!)
    }

    private fun movePlayer(current: BlockPos) {
        val jump = (player.onGround && current.y > player.posY - 1 &&!BlockUtil.isStairSlab(current)  && !BlockUtil.isStairSlab(
            player.getStandingOn())
                && sqrt((player.getStandingOn().x - current.x).toDouble().pow(2.0) + (player.getStandingOn().z - current.z).toDouble().pow(2.0)) < 1.2)
        val rotation = RotationUtil.Rotation(AngleUtil.getAngles(current.toVec3Top()).yaw, 0f)
        directionYaw = rotation.yaw
        gameSettings.keyBindSprint.setPressed(true)
        gameSettings.keyBindForward.setPressed(true)
    }
}
