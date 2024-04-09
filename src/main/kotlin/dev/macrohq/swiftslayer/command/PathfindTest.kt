package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.PathingUtil
import dev.macrohq.swiftslayer.util.RenderUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import dev.macrohq.swiftslayer.util.SlayerUtil
import dev.macrohq.swiftslayer.util.autoBatphone
import dev.macrohq.swiftslayer.util.getStandingOnCeil
import dev.macrohq.swiftslayer.util.mobKiller
import dev.macrohq.swiftslayer.util.player
import dev.macrohq.swiftslayer.util.revenant
import dev.macrohq.swiftslayer.util.swiftSlayer

@Command(value = "pathfindtest", aliases = ["pft"])
class PathfindTest {
  @Main
  private fun main() {
    val ctx = CalculationContext(SwiftSlayer.instance)
  }

  @SubCommand
  private fun end() {
    RenderUtil.filledBox.remove(swiftSlayer.removeLater)
    RenderUtil.filledBox.add(player.getStandingOnCeil())
    swiftSlayer.removeLater = player.getStandingOnCeil()
  }

  @SubCommand
  private fun clear() {
    RenderUtil.filledBox.clear()
    RenderUtil.markers.clear()
    RenderUtil.lines.clear()
    RenderUtil.points.clear()
    RenderUtil.entites.clear()
    swiftSlayer.removeLater = null
  }

  @SubCommand
  private fun stop() {
    mobKiller.disable()
    PathingUtil.stop()
    RotationUtil.stop()
    autoBatphone.disable()
    KeyBindUtil.stopClicking()
    revenant.disable()
  }
}
