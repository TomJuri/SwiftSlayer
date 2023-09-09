package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.*
import net.minecraft.util.BlockPos

@Command(value = "pathfindtest", aliases = ["pft"])
class PathfindTest {
    private var path = mutableListOf<BlockPos>()
    @Main
    private fun main() {
        mobKiller.enable()
//        batphoneHandler.enable(true)
//        RenderUtil.markers.clear()
//        RenderUtil.markers.addAll(AStarPathfinder(player.getStandingOnFloor(), swiftSlayer.removeLater!!).findPath(2000))
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
        swiftSlayer.removeLater = null
    }

    @SubCommand
    private fun stop() {
        mobKiller.disable()
        PathingUtil.stop()
        RotationUtil.stop()
        batphoneHandler.disable()
    }
}
