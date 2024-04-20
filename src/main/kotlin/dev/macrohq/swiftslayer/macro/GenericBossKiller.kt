package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Angle
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.BossKillerMovement
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.EntityLiving
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

private var timeout = Timer(0)



class GenericBossKiller {

  var enabled = false
    private set
  private var target: EntityLiving? = null

  @SubscribeEvent
  fun onTick(event: ClientTickEvent) {
    if (!enabled) return
    //target = SlayerUtil.getBoss()?.first
    target = SlayerUtil.getFakeBoss()
    if (target == null) return
    if (SlayerUtil.getState() == SlayerUtil.SlayerState.BOSS_DEAD) {
      Logger.info("Boss killed.")
      LockRotationUtil.getInstance().disable()
      disable()
      return
    }
    if(tickCounter > 20) {
      tickCounter = 0
    } else {
      tickCounter++
    }
    println(ticksSinceLastMovement)
    if(BossKillerMovement.getInstance().getDistanceBetweenBlocks(playerLastPos, mc.thePlayer.position) > 0.8) {
      ticksSinceLastMovement = 0
    } else {
      ticksSinceLastMovement++
    }
    //AutoRotation.getInstance().easeTo(Target(target!!), 700, LockType.INSTANT, true)
    val boundingBox: AxisAlignedBB = target!!.entityBoundingBox

    val randomPositionOnBoundingBox = target!!.position.add(0, (target!!.height*0.75).toInt(), 0)
    SwiftSlayer.instance.rotation.setYaw(RotationMath.getYaw(randomPositionOnBoundingBox), SwiftSlayer.instance.config.macroLockSmoothness.toInt(), true)
    if(!player.isAirBorne && !target!!.isAirBorne) {
    SwiftSlayer.instance.rotation.setPitch(RotationMath.getPitch(randomPositionOnBoundingBox), SwiftSlayer.instance.config.macroLockSmoothness.toInt(), true) }

    var y = target!!.getStandingOnCeil().y
    while (world.getBlockState(BlockPos(target!!.posX, y.toDouble(), target!!.posZ)).block == Blocks.air) {
      y--
    }

    if(SwiftSlayer.instance.config.movementType == 0 && inCorner && BossKillerMovement.getInstance().getDistanceBetweenBlocks(player.position, chosenCorner) > 1) {
      inCorner = false
    }

    if( SwiftSlayer.instance.config.movementType == 0 && blockPoss.isNotEmpty() && !inCorner && PathingUtil.isDone) {
      PathingUtil.goto(blockPoss[0].add(0, -1, 0))
      gameSettings.keyBindSneak.setPressed(true)
      chosenCorner = blockPoss[0]
      inCorner = true
    }

    if( SwiftSlayer.instance.config.movementType == 0 && inCorner) {
      if(BossKillerMovement.getInstance().getDistanceBetweenBlocks(player.position, chosenCorner) > 2) {
        PathingUtil.goto(chosenCorner)
      }
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
      if(SwiftSlayer.instance.rotation.canEnable()) SwiftSlayer.instance.rotation.disable()

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
      if(mc.thePlayer.getDistanceToEntity(target!!) < 4) {
        KeyBindUtil.leftClick(8)
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
        if(BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), block, mc.thePlayer.position.add(0, -1, 0))) {
          blockPoss.add(block)
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
    SwiftSlayer.instance.rotation.disable()
    blockPoss.clear()
    inCorner = false
    tryUnstuck = false
    gameSettings.keyBindSneak.setPressed(false)
    gameSettings.keyBindBack.setPressed(false)
    gameSettings.keyBindRight.setPressed(false)
  }

  companion object {
    var blockPoss: ArrayList<BlockPos> = ArrayList<BlockPos>()
    var inCorner: Boolean = false
    lateinit var chosenCorner: BlockPos
    var ticksSinceLastMovement: Int = 0
    var tryUnstuck: Boolean = false
     lateinit var playerLastPos: BlockPos
    var tickCounter: Int = 0

  }
}