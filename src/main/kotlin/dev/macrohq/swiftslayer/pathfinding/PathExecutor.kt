package dev.macrohq.swiftslayer.pathfinding

import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.rotation.RotationManager
import me.kbrewster.eventbus.Subscribe
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PathExecutor {
  var enabled = false
    private set
  var directionYaw = 0f
    private set
  private var path = listOf<BlockPos>()
  private var next: BlockPos? = null
  private var pathFailCounter = 0
  private var aotving = false
  private var goal: BlockPos? = null
  private var rotate: Boolean = true

  @Subscribe
  fun onTick(event: ClientTickEvent) {
    if (!enabled) return
    if(RotationManager.getInstance().currentThread.isAlive) {
      gameSettings.keyBindSprint.setPressed(false)
      gameSettings.keyBindForward.setPressed(false)
      gameSettings.keyBindJump.setPressed(false)
      return
    }




    if (!isOnPath()) {
      if (pathFailCounter >= 100) {
        Logger.error("Path execution failed, retrying.")
        disable()
        PathingUtil.goto(path[path.size - 1])
        return
      }
      if (player.onGround) pathFailCounter++
    }
    pathFailCounter = 0

    if (isOnPath() && hasReachedEnd()) {
      disable()
      return
    }

    if (isOnPath()) {
      next = path[path.indexOf(getStandingOn()!!) + 1]
      if(rotate && !shouldJump()) {
        if (goal != null) {
        RotationManager.getInstance().rotateTo(goal!!.toVec3Top(), 2f)
        } else {
          RotationManager.getInstance().rotateTo(next!!.toVec3Top(), 2f)
        }
      }

      RenderUtil.markers.clear()
      RenderUtil.markers.add(next!!)
    }

    if (canAOTV()) {
      if (InventoryUtil.holdItem("Aspect of the Void")) {
        KeyBindUtil.rightClick()
        aotving = true
        return
      }
      Logger.error("No AOTV in hotbar.")
    }

    if (aotving && !hasAOTVed()) {
      Logger.log("Waiting for AOTV to arrive.")
      return
    } else if (aotving && hasAOTVed()) {
      Logger.log("AOTV Finished.")
      aotving = false
    }

   // val rotation = RotationUtil.Rotation(AngleUtil.getAngles(next!!.toVec3Top()).yaw, 20f)
    val rotation = Angle(AngleUtil.getAngle(next!!.toVec3Top()).yaw, 20f)
    directionYaw = rotation.yaw
   gameSettings.keyBindSprint.setPressed(AngleUtil.yawTo360(player.rotationYaw) in AngleUtil.yawTo360(player.rotationYaw) - 45..AngleUtil.yawTo360(player.rotationYaw) + 45)
    gameSettings.keyBindForward.setPressed(true)
    gameSettings.keyBindJump.setPressed(shouldJump())

  }

  fun enable(pathIn: List<BlockPos>, target: EntityLiving? = null, rotate: Boolean = true) {
    if (pathIn.isEmpty()) return
    disable()
    path = pathIn
    next = path[0]
    directionYaw = player.rotationYaw
    pathFailCounter = 0
    aotving = false
    enabled = true
    if(target != null) {
      goal = target.position
    }
    this.rotate = rotate

  }

  fun disable() {
    enabled = false
    gameSettings.keyBindSprint.setPressed(false)
    gameSettings.keyBindForward.setPressed(false)
    gameSettings.keyBindJump.setPressed(false)
    goal = null
    rotate = true
  }

  private fun canAOTV(): Boolean {
    val d = sqrt(player.getDistanceSqToCenter(next)) > 9 && !aotving && path.indexOf(next) != path.size - 1
    val yp = AngleUtil.getAngles(next!!.up().up())
    val yawDiff = abs(AngleUtil.yawTo360(player.rotationYaw) - AngleUtil.yawTo360(yp.yaw))
    val pitchDiff = abs(mc.thePlayer.rotationPitch - yp.pitch)
    return d && yawDiff < 5 && pitchDiff < 2 && false
  }

  private fun shouldJump() = player.onGround && (next!!.y + 0.5 - player.posY) >= 0.5 && (sqrt(
    (player.posX - next!!.x).pow(
      2.0
    ) + (player.posZ - next!!.z).pow(2.0)
  ) < 2)

  private fun isOnPath() = getStandingOn() != null
  private fun getStandingOn() = path.find { it.x == player.getStandingOnCeil().x && it.z == player.getStandingOnCeil().z }
  private fun hasReachedEnd() = player.getStandingOnCeil().x == path[path.size - 1].x && player.getStandingOnCeil().z == path[path.size - 1].z
  private fun hasAOTVed() = sqrt(player.lastTickPositionCeil().distanceSq(player.getStandingOnCeil())) > 4

}