package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import net.minecraft.entity.EntityLiving
import net.minecraft.util.MathHelper
import kotlin.math.pow

object RotationUtil {
    private var startRotation = Rotation(0f, 0f)
    private var endRotation = Rotation(0f, 0f)
    private var startTime = 0L
    private var endTime = 0L
    private var done = true
    private lateinit var entity: EntityLiving
    private var lockAim = false
    fun easeToEntity(entity: EntityLiving, durationMillis: Long, aimLock: Boolean = false) {
        if (!done) return
        done = false
        this.entity = entity
        this.lockAim = aimLock

        startRotation = Rotation(player.rotationYaw, player.rotationPitch)
        val rotation = AngleUtil.getAngles(entity)
        val neededChange = getNeededChange(startRotation, rotation)
        endRotation = Rotation(startRotation.yaw + neededChange.yaw, startRotation.pitch + neededChange.pitch)
        startTime = System.currentTimeMillis()
        endTime = startTime + durationMillis
    }

    fun ease(rotation: Rotation, durationMillis: Long) {
        if (!done) return
        done = false
        startRotation = Rotation(player.rotationYaw, player.rotationPitch)
        val neededChange = getNeededChange(startRotation, rotation)
        endRotation = Rotation(startRotation.yaw + neededChange.yaw, startRotation.pitch + neededChange.pitch)
        startTime = System.currentTimeMillis()
        endTime = startTime + durationMillis
    }

    fun easeDirection(rotation: Rotation, durationMillis: Long, direction: Direction) {
        if (!done) return
        done = false
        val currentRotation = Rotation(player.rotationYaw, player.rotationPitch)
        val endRotationYaw = if (direction == Direction.LEFT) currentRotation.yaw - rotation.yaw else currentRotation.yaw + rotation.yaw
        startRotation = currentRotation
        endRotation = Rotation(endRotationYaw, rotation.pitch)
        startTime = System.currentTimeMillis()
        endTime = startTime + durationMillis
    }
    private fun lock(entity: EntityLiving) {
        runAsync {
            while(lockAim) {
                if(entity.health<=0) break
                val rotation = AngleUtil.getAngles(entity)
                player.rotationYaw = rotation.yaw
                player.rotationPitch = rotation.pitch
            }
            stop()
        }
    }

    fun lock(entity: EntityLiving) {
        if(!done) return
        done = false
        runAsync {
            while(!done) {
                if(entity.isDead) break
                val rotation = AngleUtil.getAngles(entity)
                player.rotationYaw = rotation.yaw
                player.rotationPitch = rotation.pitch
                Thread.sleep(1)
            }
            done = true
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
        if(lockAim) lock(entity)
    }

    private fun interpolate(start: Float, end: Float): Float {
        val spentMillis = (System.currentTimeMillis() - startTime).toFloat()
        val relativeProgress = spentMillis / (endTime - startTime)
        return (end - start) * easeOutCubic(relativeProgress) + start
    }

    private fun easeOutCubic(number: Float): Float {
        return (1.0 - (1.0 - number).pow(3.0)).toFloat()
    }

    private fun getNeededChange(startRot: Rotation, endRot: Rotation): Rotation {
        var yawChange = MathHelper.wrapAngleTo180_float(endRot.yaw) - MathHelper.wrapAngleTo180_float(startRot.yaw)
        if (yawChange <= -180.0f) yawChange += 360.0f else if (yawChange > 180.0f) yawChange += -360.0f
        return Rotation(yawChange, endRot.pitch - startRot.pitch)
    }

    fun stop() {
        done = true
    }

    data class Rotation(val yaw: Float, val pitch: Float)

    enum class Direction {
        LEFT,
        RIGHT
    }

    fun stop(){
        lockAim = false
        done = true
    }
}
