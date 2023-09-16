package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.Entity
import kotlin.math.pow

object RotationUtil {
    private var startRotation = Rotation(0f, 0f)
    private var endRotation = Rotation(0f, 0f)
    private var startTime = 0L
    private var endTime = 0L
    private var done = true
    private var lock: Pair<Entity, Double>? = null
    private var isOverriden = false
    private var isNigga = false

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

    fun lock(entity: Entity, durationMillis: Long, eyes: Boolean, override: Boolean = false) {
        if (isOverriden) return
        done = false
        ease(
            AngleUtil.getAngles(
                entity.positionVector.addVector(
                    0.0,
                    if (eyes) entity.eyeHeight.toDouble() else 1.0,
                    0.0
                )
            ), durationMillis, override
        )
        lock = Pair(entity, if (eyes) entity.eyeHeight.toDouble() else 1.0)
    }

    fun onRenderWorldLast() {
        if (done) return
        if (System.currentTimeMillis() <= endTime) {
            player.rotationYaw = interpolate(player.rotationYaw, endRotation.yaw)
            player.rotationPitch = interpolate(player.rotationPitch, endRotation.pitch)
            if(lock==null) return
        }
        if (lock != null && !lock!!.first.isDead) {
            endRotation = AngleUtil.getAngles(lock!!.first.positionVector.addVector(0.0, lock!!.second, 0.0))
            startTime = System.currentTimeMillis();
            endTime = startTime + 150
        } else {
            stop()
        }
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

    data class Rotation(var yaw: Float, var pitch: Float)
}
