package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.SlayerUtil
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLiving
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent


@Command("locktest")
class LockTest {
    private lateinit var rotationTargetEntity: EntityLiving


    @SubCommand
    private fun rotate(lock: Boolean) {
        rotationTargetEntity = SlayerUtil.getFakeBoss()!!
       enabled = lock
        println(enabled)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {


if (enabled) {

    rotationTargetEntity = SlayerUtil.getFakeBoss()!!

    if(rotationTargetEntity.isDead) enabled = false

    val randomPositionOnBoundingBox =
        rotationTargetEntity.position.add(0, (rotationTargetEntity.height*0.75).toInt(), 0)

    if (Minecraft.getMinecraft().objectMouseOver.entityHit == rotationTargetEntity) return


}

    }

    companion object {
        var enabled: Boolean = false
    }
}