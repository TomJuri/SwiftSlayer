package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.error
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.util.BlockPos

@Command(value = "pathfindtest", aliases = ["pft"])
class PathfindTest {
    private var path = mutableListOf<BlockPos>()
    @Main
    private fun main() {
//        PathingUtil.goto(swiftSlayer.removeLater!!)
//        val astar = AStarPathfinder(player.getStandingOn(), swiftSlayer.removeLater!!)
//        path = astar.findPath(2000).toMutableList()
//        RenderUtil.lines.addAll(path)
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
        RenderUtil.lines.clear()
        RenderUtil.markers.clear()
        val block = path.minByOrNull { it.distanceSq(player.getStandingOn()) }
        RenderUtil.markers.add(block!!)
        path = path.dropWhile { path.indexOf(it) <= path.indexOf(block) }.toMutableList()
        val astar = AStarPathfinder(player.getStandingOn(), block).findPath(2000)
        path.addAll(0, astar)
        RenderUtil.lines.addAll(path)
    }
}
