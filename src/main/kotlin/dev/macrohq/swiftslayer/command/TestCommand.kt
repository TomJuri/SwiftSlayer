package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.feature.helper.Target
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.feature.implementation.LockType
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.RenderUtil
import dev.macrohq.swiftslayer.util.getStandingOnCeil
import dev.macrohq.swiftslayer.util.player
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

@Command("test", aliases = ["set"])
class TestCommand {
  private var rotationTargetBlock: BlockPos? = null

  @Main
  private fun main() {
    Logger.note("Test")
  }

  @SubCommand
  private fun rotate(lock: Boolean = false) {
    if (rotationTargetBlock == null) return
    if(!AutoRotation.getInstance().enabled) {
      AutoRotation.getInstance().easeTo(Target(rotationTargetBlock!!), 500, if (lock) LockType.SMOOTH else LockType.NONE, false, 400)
    }else{
      AutoRotation.getInstance().disable()
    }
  }

  @SubCommand(aliases = ["srt"])
  private fun setRotationTarget() {
    rotationTargetBlock = player.getStandingOnCeil()
  }

  @SubCommand
  private fun clear() {
    rotationTargetBlock = null
  }

  @SubscribeEvent
  fun onRender(event: RenderWorldLastEvent) {
    if (rotationTargetBlock != null) {
      RenderUtil.drawBox(event, rotationTargetBlock!!, Color.GREEN, true)
    }
  }
}