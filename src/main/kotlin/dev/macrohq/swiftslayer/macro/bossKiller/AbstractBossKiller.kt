package dev.macrohq.swiftslayer.macro.bossKiller

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import kotlin.math.abs

abstract class AbstractBossKiller:IBossKiller {

    override var enabled: Boolean = false
    override var paused: Boolean = false
    override var tickCounter: Int = 0
    override var ticksSinceLastMovement: Int = 0




    override fun lookAtEntity(entity: EntityLiving) {
        val angle = Target(angleForWeapon(entity))
        //  var time = SwiftSlayer.instance.config.calculateRotationTime(abs(angle.getAngle().yaw - (mc.thePlayer.rotationYaw % 360)).toDouble())
        val time = SwiftSlayer.instance.config.calculateRotationTime(
            SwiftSlayer.instance.config.calculateDegreeDistance(
                AngleUtil.yawTo360(mc.thePlayer.rotationYaw).toDouble(), mc.thePlayer.rotationPitch.toDouble(), AngleUtil.yawTo360(angle.getAngle().yaw).toDouble(), angle.getAngle().pitch.toDouble()))
        when (config.mobKillerWeapon) {

            0 -> AutoRotation.getInstance().easeTo(angle, time, LockType.NONE, true)
            1 -> AutoRotation.getInstance().easeTo(angle, time, LockType.NONE, true )
            2 -> {}
            3 -> AutoRotation.getInstance().easeTo(angle,time, LockType.NONE, true)
        }
        Logger.info(time)
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
                Logger.info("suck my nuts")
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