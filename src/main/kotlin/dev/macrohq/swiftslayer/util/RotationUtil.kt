package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import net.minecraft.entity.Entity
import kotlin.math.pow

object RotationUtil {
    private var startRotation = Rotation(0f, 0f)
    private var endRotation = Rotation(0f, 0f)
    private var startTime = 0L
    private var endTime = 0L
    private var done = true

    private lateinit var entity: Entity
    private var lockAim = false
    private var eyes = false

    fun easeToEntity(entity: Entity, durationMillis: Long, aimLock: Boolean = false, eyes: Boolean) {
        if (!done) return
        done = false
        this.entity = entity
        this.lockAim = aimLock
        this.eyes = eyes
        startRotation = Rotation(player.rotationYaw, player.rotationPitch)
        val rotation = AngleUtil.getAngles(entity.positionVector)
        val neededChange = AngleUtil.getNeededChange(startRotation, rotation)
        endRotation = Rotation(startRotation.yaw + neededChange.yaw, startRotation.pitch + neededChange.pitch)
        startTime = System.currentTimeMillis()
        endTime = startTime + durationMillis
    }

    fun ease(rotation: Rotation, durationMillis: Long) {
        done = false
        startRotation = Rotation(player.rotationYaw, player.rotationPitch)
        val neededChange = AngleUtil.getNeededChange(startRotation, rotation)
        endRotation = Rotation(startRotation.yaw + neededChange.yaw, startRotation.pitch + neededChange.pitch)
        startTime = System.currentTimeMillis()
        endTime = startTime + durationMillis
    }

    private fun lock(entity: Entity, eyes: Boolean) {
        runAsync {
            while(lockAim) {
                if(entity.isDead) break
                val rotation = AngleUtil.getAngles(
                    entity.positionVector.addVector(
                        0.0,
                        if (eyes) entity.eyeHeight.toDouble() else 0.0,
                        0.0
                    )
                )
                player.rotationYaw = rotation.yaw
                player.rotationPitch = rotation.pitch
            }
            stop()
        }
    }

    fun onRenderWorldLast() {
        if (done) return
        if (System.currentTimeMillis() <= endTime) {
            player.rotationYaw = interpolate(startRotation.yaw, endRotation.yaw)
            player.rotationPitch = interpolate(startRotation.pitch, endRotation.pitch)
            return
        }
        player.rotationYaw = endRotation.yaw
        player.rotationPitch = endRotation.pitch
        done = true
        if (lockAim) lock(entity, eyes)
    }

    private fun interpolate(start: Float, end: Float): Float {
        val spentMillis = (System.currentTimeMillis() - startTime).toFloat()
        val relativeProgress = spentMillis / (endTime - startTime)
        return (end - start) * easeOutCubic(relativeProgress) + start
    }

    private fun easeOutCubic(number: Float): Float {
        return (1.0 - (1.0 - number).pow(3.0)).toFloat()
    }

    fun stop(){
        lockAim = false
        done = true
    }

    data class Rotation(val yaw: Float, val pitch: Float)
}
