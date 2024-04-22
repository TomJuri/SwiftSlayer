package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.pathfinder.calculate.path.AStarPathFinder
import dev.macrohq.swiftslayer.pathfinder.goal.Goal
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.*
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

@Command("test", aliases = ["set"])
class TestCommand {
  private var target: BlockPos? = null
  private var blocks = mutableListOf<BlockPos>()

  @Main
  private fun main() {
//    Logger.note("Test")
    if(target == null){
      Logger.note("Target Block is null.")
      return
    }

    val ss = SwiftSlayer.instance
    val currPos = ss.playerContext.playerPosition
    val ctx = CalculationContext(ss)
    val goal = Goal(target!!.x, target!!.y, target!!.z, ctx)
    val pather = AStarPathFinder(currPos.x, currPos.y, currPos.z, goal, ctx)
    val path = pather.calculatePath()
  }

  @SubCommand
  private fun rotate(lock: Boolean = false) {
    if (target == null) return
    if(!AutoRotation.getInstance().enabled) {
      AutoRotation.getInstance().easeTo(Target(target!!), 500, if (lock) LockType.SMOOTH else LockType.NONE, false, 400)
    }else{
      AutoRotation.getInstance().disable()
    }
  }

  @SubCommand(aliases = ["srt", "end"])
  private fun setRotationTarget() {
    target = player.getStandingOnCeil()
  }

  @SubCommand
  private fun btn(){
    if(target == null){
      Logger.note("Target is null")
      return
    }

    val ctx = CalculationContext(SwiftSlayer.instance)
    val blocks = BlockUtil.bresenham(ctx, player.getStandingOnCeil().toVec3(), target!!.toVec3())
    if(blocks.isEmpty()){
      Logger.note("List is null")
      return
    }

    this.blocks.clear()
    this.blocks.addAll(blocks)
    Logger.note("canwalk: ${BlockUtil.blocksBetweenValid(ctx, player.getStandingOnCeil(), target!!)}")
  }

  @SubCommand
  private fun clear(clear: Boolean) {
   blocks.clear()
   if(clear) target = null
  }

  @SubscribeEvent
  fun onRender(event: RenderWorldLastEvent) {
    if (target != null) {
      RenderUtil.drawBox(event, target!!, Color.GREEN, true)
    }
    blocks.forEach {
      RenderUtil.drawBox(event, it, Color.CYAN, true)
    }
  }
}