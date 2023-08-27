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
        RenderUtil.lines.clear()
        val astar = AStarPathfinder(swiftSlayer.removeLater!!, swiftSlayer.removeLater0!!)
        val path = astar.findPath(10000)
        Logger.info(path.isEmpty())
        if (path.isEmpty()) {
            error("No path found!")
            return
        }
        path.forEach { RenderUtil.lines.add(it) }
        swiftSlayer.pathExecutor.executePath(path)
    }

    @SubCommand
    private fun start() {
        RenderUtil.filledBox.add(player.getStandingOn())
        swiftSlayer.removeLater = player.getStandingOn()
    }

    @SubCommand
    private fun end() {
        RenderUtil.filledBox.add(player.getStandingOn())
        swiftSlayer.removeLater0 = player.getStandingOn()
    }

    @SubCommand
    private fun clear() {
        RenderUtil.filledBox.clear()
        RenderUtil.markers.clear()
        RenderUtil.lines.clear()
        swiftSlayer.pathExecutor.disable()
        swiftSlayer.removeLater = null
        swiftSlayer.removeLater0 = null
    }
}
