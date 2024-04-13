package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.*
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

@Command("directionTest")
class DirectionTest {



    var mc: Minecraft = Minecraft.getMinecraft()
    @SubCommand
    private fun direction(lock: Boolean) {
        //gameSettings.keyBindSneak.setPressed(true)

        blockPos = BlockUtil.getBlocks( SlayerUtil.getFakeBoss()!!.position.add(0, -1, 0), 2, 1, 2)[0]
        enabled = lock
        PathingUtil.goto(blockPos.add(0, -1, 0))
    }


    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if(enabled) {

               // RenderUtil.drawBox(event, BlockUtil.getCornerBlocks(SlayerUtil.getFakeBoss()!!.position, 2, 1, 2).first, Color.GREEN, true)
            //    RenderUtil.drawBox(event, BlockUtil.getCornerBlocks(SlayerUtil.getFakeBoss()!!.position, 2, 1, 2).second, Color.GREEN, true)
            if(BlockUtil.getBlocks( SlayerUtil.getFakeBoss()!!.position.add(0, -1, 0), 2, 1, 2).isEmpty().not())
                for(block: BlockPos in BlockUtil.getBlocks( SlayerUtil.getFakeBoss()!!.position.add(0, -1, 0), 2, 1, 2)) {
                    RenderUtil.drawBox(event, block, Color.RED, true)
                }


        }

    }

    companion object {
        var enabled: Boolean = false
        lateinit var blockPos: BlockPos
    }
}