package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.feature.AbstractFeature
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class LockRotationUtil: AbstractFeature() {
    override val featureName: String = "LockRotation"
    override val isPassiveFeature: Boolean = false
    var mc: Minecraft = Minecraft.getMinecraft()
    var updateYaw: Boolean = false
    var updatePitch: Boolean = false
    private var yawGoal = 0f
    private var pitchGoal = 0f
    private var yawSmooth = 0
    private var pitchSmooth = 0
    var isOverriden = false
    var lastYaws: ArrayDeque<Float> = ArrayDeque<Float>(80)

    companion object{
        private var instance: LockRotationUtil? = null
        fun getInstance(): LockRotationUtil {
            if(instance == null){
                instance = LockRotationUtil()
            }
            return instance!!
        }
    }
    fun setYaw(yaw: Float, smoothing: Int, override: Boolean = false) {
        var mutYaw = yaw
        while (mutYaw >= 180) {
            mutYaw-=360
        }

        while (mutYaw < -180) {
            mutYaw+=360
        }

        getInstance().isOverriden = override
        yawGoal = mutYaw
        yawSmooth = smoothing
        updateYaw = true
    }

    fun setPitch(pitch: Float, smoothing: Int, override: Boolean = false) {

        val mutPitch = max(-90.0f, min(90.0f, pitch))

        getInstance().isOverriden = override
        pitchGoal = mutPitch
        pitchSmooth = smoothing
        updatePitch = true
    }

    fun reset() {
        updateYaw = false
        updatePitch = false
    }

     override fun disable() {
        isOverriden = false
        yawGoal = 0f
         pitchGoal = 0f
         updateYaw = false
         updatePitch = false
         pitchSmooth = 0
         yawSmooth = 0

    }

     override fun canEnable(): Boolean {
        return this.enabled
    }
    @SubscribeEvent
    fun clientTick(event: ClientTickEvent?) {

        if (updateYaw) {

            mc.thePlayer.rotationYaw = RotationMath.interpolate(yawGoal, mc.thePlayer.rotationYaw, 1f / yawSmooth)
            if (abs(mc.thePlayer.rotationYaw - yawGoal) < Math.random() * 2) {
                updateYaw = false
            }
        }

        if (updatePitch) {
            mc.thePlayer.rotationPitch =
                RotationMath.interpolate(pitchGoal, mc.thePlayer.rotationPitch, 1f / pitchSmooth)
            if (abs(mc.thePlayer.rotationPitch - pitchGoal) < Math.random() * 2) {
                updatePitch = false
            }
        }


    }


}
