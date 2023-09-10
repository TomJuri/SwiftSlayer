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
    private var lock: Pair<Entity, Double>? = null

    fun ease(rotation: Rotation, durationMillis: Long) {
        done = false
        startRotation = Rotation(player.rotationYaw, player.rotationPitch)
        val neededChange = AngleUtil.getNeededChange(startRotation, rotation)
        endRotation = Rotation(startRotation.yaw + neededChange.yaw, startRotation.pitch + neededChange.pitch)
        startTime = System.currentTimeMillis()
        endTime = startTime + durationMillis
    }

    fun lock(entity: Entity, durationMillis: Long, eyes: Boolean) {
        done = false
        ease(
            AngleUtil.getAngles(
                entity.positionVector.addVector(
                    0.0,
                    if (eyes) entity.eyeHeight.toDouble() else 1.0,
                    0.0
                )
            ), durationMillis
        )
        lock = Pair(entity, if (eyes) entity.eyeHeight.toDouble() else 0.0)
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
        if (lock != null) {
            runAsync {
                while (lock != null) {
                    if (lock!!.first.isDead) break
                    val rotation = AngleUtil.getAngles(lock!!.first.positionVector.addVector(0.0, lock!!.second, 0.0))
                    player.rotationYaw = rotation.yaw
                    player.rotationPitch = rotation.pitch
                }
                stop()
            }
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
        done = true
        lock = null
    }

    data class Rotation(val yaw: Float, val pitch: Float)
}
