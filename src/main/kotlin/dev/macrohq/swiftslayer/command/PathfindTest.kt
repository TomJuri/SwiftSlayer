package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.macro.MobKiller
import dev.macrohq.swiftslayer.pathfinding.AStar
import dev.macrohq.swiftslayer.pathfinding.AStarPathfinder
import dev.macrohq.swiftslayer.util.*
import dev.macrohq.swiftslayer.util.Logger.error
import dev.macrohq.swiftslayer.util.Logger.info
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.abs

@Command(value = "pathfindtest", aliases = ["pft"])
class PathfindTest {
    private var path = mutableListOf<BlockPos>()
    @Main
    private fun main() {
        mobKiller.enable()
//        PathingUtil.goto(swiftSlayer.removeLater!!)
//        RenderUtil.entites.clear()
//        RenderUtil.entites.add(EntityUtil.getMobs(EntityZombie::class.java, 1999)[0])
//        val swiftYaw = AngleUtil.getAngles(swiftSlayer.removeLater!!).yaw
//        val playerYaw = AngleUtil.yawTo360(player.rotationYaw)
//        val yawChange = abs(MathHelper.wrapAngleTo180_float(AngleUtil.getAngles(swiftSlayer.removeLater!!).yaw - AngleUtil.yawTo360(player.rotationYaw)))
//        val pitchChange = abs(-player.rotationPitch + AngleUtil.getAngles(swiftSlayer.removeLater!!).pitch)
//        info("yawChange: $yawChange, pitchChange: $pitchChange")
    }

    @SubCommand
    private fun end() {
        RenderUtil.filledBox.remove(swiftSlayer.removeLater)
        RenderUtil.filledBox.add(player.getStandingOn())
        swiftSlayer.removeLater = player.getStandingOn()
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
