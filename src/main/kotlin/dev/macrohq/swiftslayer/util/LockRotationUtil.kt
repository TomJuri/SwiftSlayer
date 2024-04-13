package dev.macrohq.swiftslayer.util;

import dev.macrohq.swiftslayer.feature.AbstractFeature
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs

public class LockRotationUtil: AbstractFeature() {
    override val featureName: String = "LockRotation"
    override val isPassiveFeature: Boolean = false
    var mc: Minecraft = Minecraft.getMinecraft()
    var updateYaw: Boolean = false
    var updatePitch: Boolean = false
    private var yawGoal = 0f
    private var pitchGoal = 0f
    private var yawSmooth = 0
    private var pitchSmooth = 0
    public var isOverriden = false

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

        LockRotationUtil.getInstance().isOverriden = override;
        yawGoal = yaw
        yawSmooth = smoothing
        updateYaw = true
    }

    fun setPitch(pitch: Float, smoothing: Int, override: Boolean = false) {
        LockRotationUtil.getInstance().isOverriden = override;
        pitchGoal = pitch
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
            if (abs(mc.thePlayer.rotationYaw - yawGoal) < Math.random() / 2) {
                updateYaw = false
            }
        }

        if (updatePitch) {
            mc.thePlayer.rotationPitch =
                RotationMath.interpolate(pitchGoal, mc.thePlayer.rotationPitch, 1f / pitchSmooth)
            if (abs(mc.thePlayer.rotationPitch - pitchGoal) < Math.random() / 2) {
                updatePitch = false
            }
        }


    }
}
