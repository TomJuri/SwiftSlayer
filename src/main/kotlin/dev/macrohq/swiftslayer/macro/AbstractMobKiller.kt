package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.rotation.Rotation
import dev.macrohq.swiftslayer.util.rotation.RotationManager
import net.minecraft.entity.EntityLiving
import kotlin.math.abs

abstract class AbstractMobKiller: IMobKiller {

    override var enabled: Boolean = false
    override var paused: Boolean = false
    override var fireVeilTimer: Timer = Timer(0)

    override var tickCounter: Int = 0
    override var ticksSinceLastClick: Int = 0
    override var ticksSinceLastMovement: Int = 0

    override var blacklistResetTimer: Timer = Timer(1000)




    fun log(message: String) {
        Logger.log("[${this.featureName} - $message]")
    }

    fun note(message: String) {
        Logger.note("[${this.featureName} - $message")
    }

    fun error(message: String) {
        Logger.error("[${this.featureName} - $message")
    }

    override fun lookAtEntity(entity: EntityLiving) {
        when (config.mobKillerWeapon) {
            0 -> RotationManager.getInstance().rotateTo(Rotation(AngleUtil.getAngles(entity).pitch, 90f), 1f)
            else -> {
                RotationManager.getInstance().rotateTo(entity)
            }
        }

    }

    override fun angleForWeapon(entity: EntityLiving): Angle {
        return Angle(0f, 0f)
    }
    override fun useWeapon() {
        when (config.mobKillerWeapon) {
            0 -> KeyBindUtil.rightClick()
            1 -> KeyBindUtil.leftClick()
            2 -> {
                if (fireVeilTimer.isDone) {
                    fireVeilTimer = Timer(4900)
                    KeyBindUtil.rightClick()
                }
            }
            3 -> KeyBindUtil.rightClick()

            else -> {}
        }
    }

    override fun attackDistance(): Int {
        return when (config.mobKillerWeapon) {
            0 -> 3
            1 -> config.rangedRange
            2 -> 4
            else -> {
                4
            }
        }
    }

    override fun holdWeapon() {
        when (config.mobKillerWeapon) {
            0 -> InventoryUtil.holdItem("Spirit Sceptre")
            1 -> player.inventory.currentItem = config.meleeWeaponSlot - 1
            2 -> InventoryUtil.holdItem("Fire Veil Wand")
            3 -> player.inventory.currentItem = config.meleeWeaponSlot - 1
        }
    }

    override fun stopWalking() {
        when (config.mobKillerWeapon) {
            0 -> {}
            1 -> PathingUtil.stop()
            2 -> {}
            3 -> PathingUtil.stop()
        }
    }

    override fun lookDone(): Boolean {
        val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw) - AngleUtil.yawTo360(Target(currentTarget!!).getAngle().yaw))
        val pitchDiff = abs(mc.thePlayer.rotationPitch - Target(currentTarget!!).getAngle().pitch)
        when(config.mobKillerWeapon) {
            1 -> {
                return pitchDiff < 2 &&   yawDiff < 5
            }
            2 -> {
                return pitchDiff < 4 && yawDiff < 10
            }
            else -> {
                return true
            }
        }
    }




}