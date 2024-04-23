package dev.macrohq.swiftslayer.macro.bossKiller

import dev.macrohq.swiftslayer.feature.helper.Angle
import net.minecraft.entity.EntityLiving

interface IBossKiller {
    var enabled: Boolean
    var paused: Boolean

    var tickCounter: Int
    var ticksSinceLastMovement: Int

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