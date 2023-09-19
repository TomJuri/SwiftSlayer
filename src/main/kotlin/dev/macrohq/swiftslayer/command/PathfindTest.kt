package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.BlockPos

@Command(value = "pathfindtest", aliases = ["pft"])
class PathfindTest {
    @Main
    private fun main() {
//        val entity = EntityUtil.getMobs(EntityZombie::class.java)[0] as EntityZombie
//        val blocks = BlockUtil.neighbourGenerator(entity.position.down(), 6, 1, 6)
//        val betterBlocks = mutableListOf<BlockPos>()
//        val parentPos = entity.position.down()
//        for(block in blocks){
//            if(!AStarPathfinder.Node(block, null).isWalkable()) continue
//            if(block.distanceSq(parentPos) !in 16.0..25.0) continue
//
//            betterBlocks.add(block)
//
//        }
//        RenderUtil.entites.clear()
//        RenderUtil.markers.clear()
//
//        RenderUtil.entites.add(entity)
//        RenderUtil.markers.add(betterBlocks.minBy { player.getDistanceSqToCenter(it) })
        revenant.enable()
//        RenderUtil.filledBox.clear()
//        RenderUtil.lines.clear()
//        val blocks = AStarPathfinder(player.getStandingOnFloor(), swiftSlayer.removeLater!!).findPath(1000)
//        RenderUtil.filledBox.addAll(blocks)
//        blocks.forEach { RenderUtil.lines.add(it.toVec3Top()) }
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
