package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.feature.implementation.AutoRotation
import dev.macrohq.swiftslayer.util.RotationMath
import dev.macrohq.swiftslayer.util.SlayerUtil
import net.minecraft.entity.EntityLiving
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


@Command("locktest")
class LockTest {
    private lateinit var rotationTargetEntity: EntityLiving


    @SubCommand
    private fun rotate(lock: Boolean) {
        rotationTargetEntity = SlayerUtil.getFakeBoss()!!
       enabled = lock
        println("error or sokmething")
        println(enabled)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {


if (enabled) {
    rotationTargetEntity = SlayerUtil.getFakeBoss()!!
    val boundingBox: AxisAlignedBB = rotationTargetEntity.entityBoundingBox
    val deltaX = boundingBox.maxX - boundingBox.minX
    val deltaY = boundingBox.maxY - boundingBox.minY
    val deltaZ = boundingBox.maxZ - boundingBox.minZ
    val randomPositionOnBoundingBox =
        BlockPos(boundingBox.minX + deltaX , boundingBox.minY + deltaY, boundingBox.minZ + deltaZ)
    SwiftSlayer.instance.rotation.setYaw(RotationMath.getYaw(randomPositionOnBoundingBox), SwiftSlayer.instance.config.macroLockSmoothness.toInt(), true)
    SwiftSlayer.instance.rotation.setPitch(RotationMath.getPitch(randomPositionOnBoundingBox), SwiftSlayer.instance.config.macroLockSmoothness.toInt(), true)

}

    }

    companion object {
        var enabled: Boolean = false
    }
}