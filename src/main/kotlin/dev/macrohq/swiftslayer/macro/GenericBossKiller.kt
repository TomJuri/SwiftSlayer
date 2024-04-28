package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs

private var timeout = Timer(0)



class GenericBossKiller {

  var enabled = false
    private set
  private var target: EntityLiving? = null
  private var waitTimer = Timer(50)

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!enabled) return
    //target = SlayerUtil.getBoss()?.first
    target = SlayerUtil.getFakeBoss()
    if (target == null) return
    if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD) {
      Logger.info("Boss killed.")
      disable()
      return
    }
    if(tickCounter > 20) {
      tickCounter = 0
    } else {
      tickCounter++
    }
    println(ticksSinceLastMovement)
    if(BlockUtil.getXZDistance(playerLastPos, mc.thePlayer.position) > 0.8) {
      ticksSinceLastMovement = 0
    } else {
      ticksSinceLastMovement++
    }
    val boundingBox: AxisAlignedBB = target!!.entityBoundingBox
    var angle = Target(target!!)
    var time = SwiftSlayer.instance.config.calculateRotationTime(SwiftSlayer.instance.config.calculateDegreeDistance(AngleUtil.yawTo360(mc.thePlayer.rotationYaw).toDouble(), mc.thePlayer.rotationPitch.toDouble(), AngleUtil.yawTo360(angle.getAngle().yaw).toDouble(), angle.getAngle().pitch.toDouble()))
    var randomPositionOnBoundingBox = target!!.position.add(0, (target!!.height*0.75).toInt(), 0)
    if(mc.objectMouseOver.entityHit != target!! && waitTimer.isDone) {
      AutoRotation.getInstance().easeTo(Target(randomPositionOnBoundingBox), time, LockType.NONE, true)
    } else if (mc.objectMouseOver.entityHit == target!!) {
      waitTimer = Timer(40)
    }

    if(!player.onGround) AutoRotation.getInstance().disable()
    var y = target!!.getStandingOnCeil().y
    while (world.getBlockState(BlockPos(target!!.posX, y.toDouble(), target!!.posZ)).block == Blocks.air) {
      y--
    }

    if(SwiftSlayer.instance.config.movementType == 0 && !inCorner && chosenCorner == null && PathingUtil.isDone) {
     if(blockPoss.isEmpty()) return
      chosenCorner = blockPoss[0]
      PathingUtil.goto(chosenCorner!!)
      inCorner = true
      gameSettings.keyBindSneak.setPressed(true)

    }

    if(inCorner && BlockUtil.getXZDistance(chosenCorner!!, player.getStandingOnCeil()) > 1 && PathingUtil.isDone) {
      PathingUtil.stop()
      PathingUtil.goto(chosenCorner!!)
      gameSettings.keyBindSneak.setPressed(true)
    }


    if(SwiftSlayer.instance.config.movementType == 1 && timeout.isDone) {
      gameSettings.keyBindForward.setPressed(false)

      if(ticksSinceLastMovement > 100) {
        gameSettings.keyBindSneak.setPressed(false)
        gameSettings.keyBindBack.setPressed(false)
        gameSettings.keyBindRight.setPressed(false)
        timeout = Timer(1000)

      }
      else if(tickCounter > 10) {
        gameSettings.keyBindSneak.setPressed(true)
        gameSettings.keyBindBack.setPressed(true)
        gameSettings.keyBindRight.setPressed(true)
      } else {
        gameSettings.keyBindBack.setPressed(false)
        gameSettings.keyBindRight.setPressed(false)
      }


    } else if(SwiftSlayer.instance.config.movementType == 1 && !timeout.isDone){
      gameSettings.keyBindForward.setPressed(true)
      if(AutoRotation.getInstance().enabled) AutoRotation.getInstance().disable()
    }




    /*
    if (player.getDistanceToEntity(target!!) > 4.5) {
      PathingUtil.goto(BlockPos(target!!.posX, y.toDouble(), target!!.posZ))
    } else {
      // PathingUtil.stop()
    }

     */


/*
    if(player.getDistanceToEntity(target!!) < 4 && timeout.isDone) {
      if(BlockUtil.getBlocks( target!!.position.add(0, 0, 0), 4, 2, 4).isEmpty().not())
        PathingUtil.goto(BlockUtil.getBlocks( target!!.position.add(0, 0, 0), 5, 2, 5)[0].add(0, 0, 0))
      timeout = Timer(1000)
    }
 */

    // Melee
    if (config.bossKillerWeapon == 0) {
      player.inventory.currentItem = config.meleeWeaponSlot - 1
      if(mc.thePlayer.getDistanceToEntity(target!!) < 4 && player.canEntityBeSeen(target!!) && mc.objectMouseOver.entityHit == target!!)  {
        KeyBindUtil.leftClick(8)
      } else {
        KeyBindUtil.stopClicking()
      }


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

    playerLastPos = mc.thePlayer.position
  }

  @SubscribeEvent
  fun onWorld(event: RenderWorldLastEvent) {
    if(!enabled) return

    blockPoss.clear()
    if(BlockUtil.getBlocks(mc.thePlayer.position, 15, 5, 15).isEmpty()) return

    for(block: BlockPos in BlockUtil.getBlocks(mc.thePlayer.position, 15, 5, 15)) {
      if(BlockUtil.isSingleCorner(block)) {
        if(BlockUtil.blocksBetweenValid( block, mc.thePlayer.position.add(0, -1, 0))) {
          if(abs(player.position.y - block.y) < 5) {
            blockPoss.add(block)
          }
        }

      }

    }


  }
  fun enable() {
    if (enabled) return
    Logger.info("Enabling GenericBossKiller")
    AutoRotation.getInstance().disable()
    PathingUtil.stop()
    target = null
    enabled = true
    playerLastPos = mc.thePlayer.position
  }

  fun disable() {
    if (!enabled) return
    Logger.info("Disabling GenericBossKiller")
    enabled = false
    AutoRotation.getInstance().disable()
    PathingUtil.stop()
    KeyBindUtil.stopClicking()
    blockPoss.clear()
    inCorner = false
    tryUnstuck = false
    gameSettings.keyBindSneak.setPressed(false)
    gameSettings.keyBindBack.setPressed(false)
    gameSettings.keyBindRight.setPressed(false)
    chosenCorner = null
  }

  companion object {
    var blockPoss: ArrayList<BlockPos> = ArrayList<BlockPos>()
    var inCorner: Boolean = false
     var chosenCorner: BlockPos? = null
    var ticksSinceLastMovement: Int = 0
    var tryUnstuck: Boolean = false
     lateinit var playerLastPos: BlockPos
    var tickCounter: Int = 0

  }
}