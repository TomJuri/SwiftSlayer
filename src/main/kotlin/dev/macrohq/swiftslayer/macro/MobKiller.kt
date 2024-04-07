package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.AngleUtil
import dev.macrohq.swiftslayer.util.EntityUtil
import dev.macrohq.swiftslayer.util.InventoryUtil
import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.PathingUtil
import dev.macrohq.swiftslayer.util.RenderUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import dev.macrohq.swiftslayer.util.SlayerUtil
import dev.macrohq.swiftslayer.util.Timer
import dev.macrohq.swiftslayer.util.config
import dev.macrohq.swiftslayer.util.getStandingOnCeil
import dev.macrohq.swiftslayer.util.lastTickPositionCeil
import dev.macrohq.swiftslayer.util.mc
import dev.macrohq.swiftslayer.util.player
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.abs
import kotlin.math.sqrt

class MobKiller {
  private var blacklist = mutableListOf<EntityLiving>()
  var enabled = false
    private set
  private var state: State = State.CHOOSE_TARGET
  private var targetEntity: EntityLiving? = null
  private var blacklistResetTimer = Timer(0)
  private var stuckTimer = Timer(Long.MAX_VALUE)
  private var lookTimer = Timer(Long.MAX_VALUE)
  private var fireVeilTimer = Timer(Long.MAX_VALUE)
  private lateinit var angle: RotationUtil.Rotation

  @SubscribeEvent
  fun onTick(event: TickEvent.ClientTickEvent) {
    if (!enabled) return
    InventoryUtil.closeGUI()
    if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_ALIVE) {
      disable()
      return
    }

    if (sqrt(player.lastTickPositionCeil().distanceSq(player.getStandingOnCeil())) > 1)
      stuckTimer = Timer(1500)

    if (blacklistResetTimer.isDone) {
      blacklist.clear()
      blacklistResetTimer = Timer(1000)
    }

    when (state) {
      State.CHOOSE_TARGET -> {
        RenderUtil.entites.clear()
        val targetEntityList = EntityUtil.getMobs(SlayerUtil.getMobClass()).toMutableList()
        targetEntityList.removeAll(blacklist)
        if (targetEntityList.isEmpty()) return
        targetEntity = targetEntityList[0]
        RenderUtil.entites.add(targetEntity!!)
      }

      State.GOTO_TARGET -> PathingUtil.goto(targetEntity!!.getStandingOnCeil())

      State.VERIFY_PATHFINDING -> {
        if (PathingUtil.hasFailed || targetEntity!!.isDead || stuckTimer.isDone) {
          PathingUtil.stop()
          stuckTimer = Timer(1500)
          blacklist.add(targetEntity!!)
          state = State.CHOOSE_TARGET
          return
        }
        if ((PathingUtil.isDone || player.getDistanceToEntity(targetEntity) < attackDistance()) && player.canEntityBeSeen(targetEntity)) {
          stopWalking()
          state = State.LOOK_AT_TARGET
        }
        return
      }

      State.LOOK_AT_TARGET -> {
        RotationUtil.stop()
        lookAtEntity(targetEntity!!)
        lookTimer = Timer(1500)
      }

      State.VERIFY_LOOKING -> {
        if (lookTimer.isDone) {
          state = State.LOOK_AT_TARGET
          lookTimer = Timer(Long.MAX_VALUE)
          state = State.CHOOSE_TARGET
          blacklist.add(targetEntity!!)
          RotationUtil.stop()
          return
        }
        if (lookDone()) {
          lookTimer = Timer(Long.MAX_VALUE)
          RotationUtil.stop()
          holdWeapon()
        } else {
          return
        }
      }

      State.KILL_TARGET -> {
        useWeapon()
        blacklist.add(targetEntity as EntityLiving)
      }
    }
    state = State.entries[(state.ordinal + 1) % State.entries.size]
  }

  fun enable() {
    Logger.info("Enabling MobKiller.")
    enabled = true
    stuckTimer = Timer(Long.MAX_VALUE)
    lookTimer = Timer(Long.MAX_VALUE)
    fireVeilTimer = Timer(Long.MAX_VALUE)
    blacklistResetTimer = Timer(0)
    state = State.CHOOSE_TARGET
  }

  fun disable() {
    Logger.info("Disabling MobKiller.")
    enabled = false
    PathingUtil.stop()
    RotationUtil.stop()
  }

  private fun lookAtEntity(entity: EntityLiving) {
    angle = angleForWeapon(entity)
    when (config.mobKillerWeapon) {
      0 -> RotationUtil.ease(angle, 200, true)
      1 -> RotationUtil.ease(angle, 200, true)
      2 -> {}
      3 -> RotationUtil.ease(angle, 200, true);
    }
  }

  private fun angleForWeapon(entity: EntityLiving): RotationUtil.Rotation {
    return when (config.mobKillerWeapon) {
      0 -> RotationUtil.Rotation(AngleUtil.getAngles(targetEntity!!).yaw, 45f)
      1 -> AngleUtil.getAngles(entity.positionVector.addVector(0.0, 0.8, 0.0))
      3 -> AngleUtil.getAngles(entity.positionVector.addVector(0.0, 0.8, 0.0));
      else -> RotationUtil.Rotation(0f, 0f)
    }
  }

  private fun useWeapon() {
    when (config.mobKillerWeapon) {
      0 -> KeyBindUtil.rightClick()
      1 -> KeyBindUtil.leftClick()
      2 -> {
        if (fireVeilTimer.isDone) {
          fireVeilTimer = Timer(4900)
          KeyBindUtil.rightClick()
        }
      }
      3 -> KeyBindUtil.rightClick();

      else -> {}
    }
  }

  private fun attackDistance(): Int {
    return when (config.mobKillerWeapon) {
      0 -> 6
      1 -> 3
      2 -> 4
      3 -> 3;
      else -> 6
    }
  }

  private fun holdWeapon() {
    when (config.mobKillerWeapon) {
      0 -> InventoryUtil.holdItem("Spirit Sceptre")
      1 -> player.inventory.currentItem = config.meleeWeaponSlot - 1
      2 -> InventoryUtil.holdItem("Fire Veil Wand")
      3 -> player.inventory.currentItem = config.meleeWeaponSlot - 1;
    }
  }

  private fun stopWalking() {
    when (config.mobKillerWeapon) {
      0 -> {}
      1 -> PathingUtil.stop()
      2 -> {}
      3 -> PathingUtil.stop();
    }
  }

  private fun lookDone(): Boolean {
    val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw) - AngleUtil.yawTo360(angle.yaw))
    val pitchDiff = abs(mc.thePlayer.rotationPitch - angle.pitch)
    return when (config.mobKillerWeapon) {
      0 -> pitchDiff < 2
      1 -> yawDiff < 10 && pitchDiff < 5
      2 -> true
      3 -> yawDiff < 10 && pitchDiff < 5;
      else -> true
    }
  }

  private enum class State {
    CHOOSE_TARGET, GOTO_TARGET, VERIFY_PATHFINDING, LOOK_AT_TARGET, VERIFY_LOOKING, KILL_TARGET,
  }
}
