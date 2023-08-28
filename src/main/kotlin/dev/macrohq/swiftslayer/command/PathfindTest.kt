package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.error

@Command(value = "pathfindtest", aliases = ["pft"])
class PathfindTest {
    @Main
    private fun main() {
        PathingUtil.goto(swiftSlayer.removeLater!!)
    }

    @SubCommand
    private fun end() {
        RenderUtil.filledBox.add(player.getStandingOn())
        swiftSlayer.removeLater = player.getStandingOn()
    }

    @SubCommand
    private fun clear() {
        RenderUtil.filledBox.clear()
        RenderUtil.markers.clear()
        RenderUtil.lines.clear()
        swiftSlayer.removeLater = null
    }

    @SubCommand
    private fun stop() {
        swiftSlayer.pathExecutor.disable()
    }
}
