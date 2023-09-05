package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.pathfinding.AStar
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.util.BlockPos

@Command(value = "pathfindtest", aliases = ["pft"])
class PathfindTest {
    private var path = mutableListOf<BlockPos>()
    @Main
    private fun main() {
        mobKiller.enable()
//        RenderUtil.markers.clear()
//        RenderUtil.markers.addAll(AStarPathfinder(player.getStandingOnFloor(), EntityUtil.getMobs(EntityWolf::class.java, 20000)[0].position.down()).findPath(2000))
//        PathingUtil.goto(swiftSlayer.removeLater!!)
//        world.loadedEntityList.filterIsInstance<EntityZombie>().forEach{
//            println("maxHealth: ${it.maxHealth}, currnetHealth: ${it.health}")
//        }
//        RenderUtil.entites.clear()
//        RenderUtil.entites.add(EntityUtil.getMobs(EntityWolf::class.java, 32000)[0])
//        RenderUtil.markers.addAll(BlockUtil.neighbourGenerator(player.getStandingOnFloor(), -1, 1, -2, 1, -1, 1))
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
    }
}
