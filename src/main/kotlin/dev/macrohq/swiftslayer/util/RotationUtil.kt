package dev.macrohq.swiftslayer.util

import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


object RotationUtil {
    private var startRotation = Rotation(0f, 0f)
    private var endRotation = Rotation(0f, 0f)
    private var startTime = 0L
    private var endTime = 0L
    var done = true
        private set
    private var lock: Pair<Entity, Double>? = null
    private var isOverriden = false

    fun ease(rotation: Rotation, durationMillis: Long, override: Boolean = false) {
        if (isOverriden) return
        isOverriden = override
        done = false
        startRotation = Rotation(player.rotationYaw, player.rotationPitch)
        val neededChange = AngleUtil.getNeededChange(startRotation, rotation)
        endRotation = Rotation(startRotation.yaw + neededChange.yaw, startRotation.pitch + neededChange.pitch)
        startTime = System.currentTimeMillis()
        endTime = startTime + durationMillis
    }

    fun lock(entity: Entity, durationMillis: Long, override: Boolean = false) {
        if (isOverriden) return
        done = false
        ease(
            AngleUtil.getAngles(
                entity.positionVector.addVector(
                    0.0,
                    0.8,
                    0.0
                )
            ), durationMillis, override
        )
        lock = Pair(entity, 0.8)
    }

    fun onRenderWorldLast() {
        if (done) return
        if (System.currentTimeMillis() <= endTime) {
            player.rotationYaw = interpolate(startRotation.yaw, endRotation.yaw)
            player.rotationPitch = interpolate(startRotation.pitch, endRotation.pitch)
            if(lock == null) return
        }
        if (lock != null && !lock!!.first.isDead) {
            startRotation = Rotation(player.rotationYaw, player.rotationPitch)
            endRotation = AngleUtil.getAngles(lock!!.first.positionVector.addVector(0.0, lock!!.second, 0.0))
            startTime = System.currentTimeMillis()
            endTime = startTime + 150
            return
        }
        stop()
    }

    private fun interpolate(start: Float, end: Float): Float {
        val spentMillis = (System.currentTimeMillis() - startTime).toFloat()
        val relativeProgress = spentMillis / (endTime - startTime)
        return (end - start) * easeOutCubic(relativeProgress) + start
    }

    private fun easeOutCubic(number: Float): Float {
        return (1.0 - (1.0 - number).pow(3.0)).toFloat()
    }

    fun stop() {
        isOverriden = false
        done = true
        lock = null
    }
    fun normalize(value: Float): Float {
        var value = value
        while (180 <= value) {
            value -= 360f
        }

        while (-180 > value) {
            value += 360f
        }

        return value
    }

    fun getYaw(blockPos: BlockPos): Float {
        val deltaX = blockPos.x + 0.5 - Minecraft.getMinecraft().thePlayer.posX
        val deltaZ = blockPos.z + 0.5 - Minecraft.getMinecraft().thePlayer.posZ
        val yawToBlock = atan2(-deltaX, deltaZ)
        val yaw = Math.toDegrees(yawToBlock)
        return yaw.toFloat()
    }

    fun getPitch(blockPos: BlockPos): Float {
        val deltaX = blockPos.x + 0.5 - Minecraft.getMinecraft().thePlayer.posX
        val deltaY =
            blockPos.y + 0.5 - Minecraft.getMinecraft().thePlayer.posY - Minecraft.getMinecraft().thePlayer.getEyeHeight()
        val deltaZ = blockPos.z + 0.5 - Minecraft.getMinecraft().thePlayer.posZ
        val distanceXZ = sqrt(deltaX * deltaX + deltaZ * deltaZ)
        val pitchToBlock = -atan2(deltaY, distanceXZ)
        val pitch = Math.toDegrees(pitchToBlock)
        return pitch.toFloat()
    }
    data class Rotation(var yaw: Float, var pitch: Float)
}
