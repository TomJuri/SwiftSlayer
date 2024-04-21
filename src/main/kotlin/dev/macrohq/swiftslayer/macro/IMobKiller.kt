package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.util.Timer
import net.minecraft.entity.EntityLiving

interface IMobKiller {
    var enabled: Boolean
    var paused: Boolean
    var fireVeilTimer: Timer
    var blacklist: MutableList<EntityLiving>
    var blacklistResetTimer: Timer

    var tickCounter: Int
    var ticksSinceLastClick: Int
    var ticksSinceLastMovement: Int

    val featureName: String
    val targetEntityClass: Class<out EntityLiving>
    var currentTarget: EntityLiving?

    fun disable()
    fun enable()
    fun pause()

    fun lookAtEntity(entity: EntityLiving)
    fun angleForWeapon(entity: EntityLiving): Angle
    fun useWeapon()
    fun attackDistance(): Int
    fun holdWeapon()
    fun stopWalking()
    fun lookDone(): Boolean

}