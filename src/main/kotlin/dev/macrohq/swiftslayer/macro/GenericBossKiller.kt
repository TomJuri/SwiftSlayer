package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class GenericBossKiller {

  var enabled = false
    private set
  private var target: EntityLiving? = null

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!enabled) return
    target = SlayerUtil.getBoss()?.first
    if (target == null) return
    if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD) {
      Logger.info("Boss killed.")
      disable()
      return
    }

    AutoRotation.getInstance().easeTo(Target(target!!), 700, LockType.INSTANT, true)

    var y = target!!.getStandingOnCeil().y
    while (world.getBlockState(BlockPos(target!!.posX, y.toDouble(), target!!.posZ)).block == Blocks.air) {
      y--
    }

    if (player.getDistanceToEntity(target!!) > 4.5) {
      PathingUtil.goto(BlockPos(target!!.posX, y.toDouble(), target!!.posZ))
    } else {
      PathingUtil.stop()
    }

    // Melee
    if (config.bossKillerWeapon == 0) {
      player.inventory.currentItem = config.meleeWeaponSlot - 1
      KeyBindUtil.leftClick(8)

      // Hyperion
    } else if (config.bossKillerWeapon == 1) {
      if (!InventoryUtil.holdItem("Heroic Spirit Sceptre")) {
        Logger.error("Hyperion not found in hotbar.")
        // macroManager.disable()
        return
      }
      AutoRotation.getInstance().disable()
      //RotationUtil.ease(RotationUtil.Rotation(player.rotationYaw, 90f), 350, true)
      AutoRotation.getInstance().easeTo(Target(Angle(player.rotationYaw, 90f)), 500, LockType.NONE, true)
      KeyBindUtil.rightClick(8)
    }
  }

  fun enable() {
    if (enabled) return
    Logger.info("Enabling GenericBossKiller")
    AutoRotation.getInstance().disable()
    PathingUtil.stop()
    target = null
    enabled = true
  }

  fun disable() {
    if (!enabled) return
    Logger.info("Disabling GenericBossKiller")
    enabled = false
    AutoRotation.getInstance().disable()
    PathingUtil.stop()
    KeyBindUtil.stopClicking()
  }
}