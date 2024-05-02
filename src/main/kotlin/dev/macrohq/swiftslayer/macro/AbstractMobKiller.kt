package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import kotlin.math.abs

abstract class AbstractMobKiller:IMobKiller {

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
        var angle = Target(angleForWeapon(entity))
        AutoRotation.disable()
        //  var time = SwiftSlayer.config.calculateRotationTime(abs(angle.getAngle().yaw - (mc.thePlayer.rotationYaw % 360)).toDouble())
        var time = SwiftSlayer.config.calculateRotationTime(SwiftSlayer.config.calculateDegreeDistance(AngleUtil.yawTo360(mc.thePlayer.rotationYaw).toDouble(), mc.thePlayer.rotationPitch.toDouble(), AngleUtil.yawTo360(angle.getAngle().yaw).toDouble(), angle.getAngle().pitch.toDouble()))
        if(time > 50) time = time else time = 50
        when (config.mobKillerWeapon) {

            0 -> AutoRotation.easeTo(angle, time, LockType.NONE, true)
            1 -> AutoRotation.easeTo(angle, time, LockType.NONE, true, 200, easeFunction = EaseUtil::easeOutBack )
            2 -> {}
            3 -> AutoRotation.easeTo(angle, time, LockType.NONE, true)
        }
      //  Logger.info(time)
    }

    override fun angleForWeapon(entity: EntityLiving): Angle {
        return when (config.mobKillerWeapon) {
            0 -> AngleUtil.getAngle(entity.position.add(0, (entity.height*0.6).toInt(), 0))
            1 -> AngleUtil.getAngle(entity.position.add(0, (entity.height*0.6).toInt(), 0))
            3 -> AngleUtil.getAngle(entity.position.add(0, (entity.height*0.6).toInt(), 0))
            else -> Angle(0f,0f)
        }
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
            0 -> 6
            1 -> 3
            2 -> 4
            3 -> 3
            else -> 6
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
        return when (config.mobKillerWeapon) {
            0 -> pitchDiff < 2
            1 -> yawDiff < 10 && pitchDiff < 5
            2 -> true
            3 -> yawDiff < 10 && pitchDiff < 5
            else -> true
        }
    }




}