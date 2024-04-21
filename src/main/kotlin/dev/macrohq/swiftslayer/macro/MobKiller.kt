package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.abs

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
  private lateinit var angle: Target
  private var tickCounter: Int = 0
  private var ticksSinceLastClick: Int = 0
  private var ticksSinceLastMovement: Int = 0

  @SubscribeEvent
  fun onTick(event: TickEvent.ClientTickEvent) {
    if (!enabled) return
    InventoryUtil.closeGUI()
    if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_ALIVE) {
      disable()
      return
    }

      if(tickCounter == 20) {
        tickCounter = 1
      } else {
        tickCounter++
    }

      ticksSinceLastClick++

    if(BlockUtil.getXZDistance(player.lastTickPositionCeil(), player.getStandingOnCeil()) < 0.25) {
      ticksSinceLastMovement++
    } else {
      ticksSinceLastMovement = 0
    }

    if(ticksSinceLastMovement > 60) {
      ticksSinceLastMovement = 0
      blacklist.add(currentTarget)
      state = State.CHOOSE_TARGET
      Logger.info("stuck for 3 seconds!")
      return
    }

    Logger.info(state.name)
    if(blacklistResetTimer.isDone) {
      blacklist.clear()
      blacklistResetTimer = Timer(1000)
    }
    when (state) {
      State.CHOOSE_TARGET -> {
        RenderUtil.entites.clear()
        val targetEntityList = EntityUtil.getMobs(SlayerUtil.getMobClass()).toMutableList()
        targetEntityList.removeAll(blacklist)
        if(targetEntityList.isEmpty()) return
        currentTarget = targetEntityList.first()
        RenderUtil.entites.add(currentTarget)
        state = State.GOTO_TARGET
        return
      }

      State.GOTO_TARGET -> {
        AutoRotation.getInstance().disable()
        if(player.canEntityBeSeen(currentTarget)) {
        PathingUtil.goto(currentTarget.getStandingOnCeil(), currentTarget)
        }
        else {
          PathingUtil.goto(currentTarget.getStandingOnCeil())
        }

        state = State.VERIFY_PATHFINDING
        return
      }

      State.VERIFY_PATHFINDING -> {
        if (PathingUtil.hasFailed || currentTarget.isDead) {
          PathingUtil.stop()
          blacklist.add(currentTarget)
          state = State.CHOOSE_TARGET
          return

        }
        if (player.getDistanceToEntity(currentTarget) < attackDistance()) {
          stopWalking()
          state = State.LOOK_AT_TARGET
        }

        if(PathingUtil.isDone && player.getDistanceToEntity(currentTarget) > attackDistance()) {
          Logger.info("Failed pathfinding :( Ignoring this entity")
          blacklist.add(currentTarget)
          state = State.CHOOSE_TARGET
        }
        return
      }

      State.LOOK_AT_TARGET -> {
        AutoRotation.getInstance().disable()
       AutoRotation.getInstance().disable()
        if(currentTarget != null) {
          lookAtEntity(currentTarget)
        } else {
          Logger.info("failed to look at target!")
          state = State.CHOOSE_TARGET
        }
        state = State.VERIFY_LOOKING
        return
      }

      State.VERIFY_LOOKING -> {

          if(mc.objectMouseOver.entityHit != null && player.getDistanceToEntity(currentTarget) <= attackDistance() && player.canEntityBeSeen(currentTarget)) {
            holdWeapon()
            state = State.KILL_TARGET
            return
          } else if(mc.objectMouseOver.entityHit == null && player.getDistanceToEntity(currentTarget) > attackDistance()) {
            state = State.GOTO_TARGET
            return
          } else if (mc.objectMouseOver.entityHit == null && player.getDistanceToEntity(currentTarget) <= attackDistance()) {
            state = State.GOTO_TARGET
            return
          }
        }


      State.KILL_TARGET -> {
        if(ticksSinceLastClick > 3) {
          useWeapon()
          ticksSinceLastClick = 0
        }

        if (currentTarget.isDead || currentTarget.health < 1) {
          blacklist.add(currentTarget)
          state = State.CHOOSE_TARGET
          return
        }
        state = State.VERIFY_LOOKING
        return
      }

    }

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
    AutoRotation.getInstance().disable()
    state = State.CHOOSE_TARGET
  }

  private fun lookAtEntity(entity: EntityLiving) {
    angle = Target(angleForWeapon(entity))
    when (config.mobKillerWeapon) {

      0 -> AutoRotation.getInstance().easeTo(angle, SwiftSlayer.instance.config.getRandomRotationTime().toInt(), LockType.NONE, true)
      1 -> AutoRotation.getInstance().easeTo(angle, SwiftSlayer.instance.config.getRandomRotationTime().toInt(), LockType.NONE, true )
      2 -> {}
      3 -> AutoRotation.getInstance().easeTo(angle, SwiftSlayer.instance.config.getRandomRotationTime().toInt(), LockType.NONE, true)
    }
  }

  private fun angleForWeapon(entity: EntityLiving): Angle {
    return when (config.mobKillerWeapon) {
      0 -> AngleUtil.getAngle(entity.position.add(0, (entity.height*0.6).toInt(), 0))
      1 -> AngleUtil.getAngle(entity.position.add(0, (entity.height*0.6).toInt(), 0))
      3 -> AngleUtil.getAngle(entity.position.add(0, (entity.height*0.6).toInt(), 0))
      else -> Angle(0f,0f)
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
      3 -> KeyBindUtil.rightClick()

      else -> {}
    }
  }

   fun attackDistance(): Int {
    return when (config.mobKillerWeapon) {
      0 -> 6
      1 -> 3
      2 -> 4
      3 -> 3
      else -> 6
    }
  }

  private fun holdWeapon() {
    when (config.mobKillerWeapon) {
      0 -> InventoryUtil.holdItem("Spirit Sceptre")
      1 -> player.inventory.currentItem = config.meleeWeaponSlot - 1
      2 -> InventoryUtil.holdItem("Fire Veil Wand")
      3 -> player.inventory.currentItem = config.meleeWeaponSlot - 1
    }
  }

  private fun stopWalking() {
    when (config.mobKillerWeapon) {
      0 -> {}
      1 -> PathingUtil.stop()
      2 -> {}
      3 -> PathingUtil.stop()
    }
  }

  private fun lookDone(): Boolean {
    val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw) - AngleUtil.yawTo360(Target(currentTarget).getAngle().yaw))
    val pitchDiff = abs(mc.thePlayer.rotationPitch - Target(currentTarget).getAngle().pitch)
    return when (config.mobKillerWeapon) {
      0 -> pitchDiff < 2
      1 -> yawDiff < 10 && pitchDiff < 5
      2 -> true
      3 -> yawDiff < 10 && pitchDiff < 5
      else -> true
    }
  }

  private enum class State {
    CHOOSE_TARGET, GOTO_TARGET, VERIFY_PATHFINDING, LOOK_AT_TARGET, VERIFY_LOOKING, KILL_TARGET,
  }

  companion object {
    lateinit var currentTarget: EntityLiving
  }
}

