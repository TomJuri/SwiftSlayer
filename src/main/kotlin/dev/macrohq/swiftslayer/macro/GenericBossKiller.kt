package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.item.EntityArmorStand
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class GenericBossKiller {

    var enabled = false
        private set
    private lateinit var target: EntityArmorStand
    private var hasRotated = false

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!enabled) return
        if (target.isDead) {
            Logger.info("Boss killed.")
            disable()
            return
        }
        if (player.worldObj.loadedEntityList.filter { it.getDistanceToEntity(target).toDouble() == 0.0 }
                .firstOrNull() == null) return
        if (!hasRotated) {
            if (config.bossKillerWeapon == 1) {
                RotationUtil.lock(player.worldObj.loadedEntityList.filter {
                    it.getDistanceToEntity(target).toDouble() == 0.0
                }.firstOrNull()!!, 500, false)
            } else {
                RotationUtil.ease(RotationUtil.Rotation(player.rotationYaw, 90f), 500)
            }
            hasRotated = true
        }
        if (target.getDistanceToEntity(player) <= 3) {
            if (config.bossKillerWeapon == 1) {
                player.inventory.currentItem = 0
                KeyBindUtil.leftClick(10)
            } else {
                KeyBindUtil.rightClick(10)
            }
        } else {
            KeyBindUtil.stopClicking()
        }
    }

    fun enable() {
        if (enabled) return
        Logger.info("Enabling GenericBossKiller")
        RotationUtil.stop()
        target = player.worldObj.loadedEntityList.filterIsInstance<EntityArmorStand>().filter { SlayerUtil.isBoss(it) }
            .firstOrNull()!!
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