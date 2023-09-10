package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class GenericBossKiller {

    var enabled = false
        private set
    private lateinit var target: EntityLiving
    private var hasRotated = false

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        if (target.isDead) {
            Logger.info("Boss killed.")
            disable()
            return
        }
        if (!hasRotated) {
            if (config.bossKillerWeapon == 1) {
                RotationUtil.easeToEntity(target, 500, aimLock = true, true)
            } else {
                RotationUtil.ease(RotationUtil.Rotation(player.rotationYaw, 90f), 500)
            }
            hasRotated = true
        }
        if (player.getDistanceToEntity(target) <= 5) {
            if (config.bossKillerWeapon == 1)
                KeyBindUtil.leftClick(10)
            else
                KeyBindUtil.rightClick(10)
        } else {
            KeyBindUtil.stopClicking()
        }
    }

    fun enable(target: EntityLiving) {
        if (enabled) return
        Logger.info("Enabling GenericBossKiller")
        enabled = true
    }

    fun disable() {
        if (!enabled) return
        Logger.info("Disabling GenericBossKiller")
        enabled = false
        RotationUtil.stop()
        KeyBindUtil.stopClicking()
        hasRotated = false
    }
}