package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.pathfinder.movement.CalculationContext
import dev.macrohq.swiftslayer.util.*
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLiving
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


        enabled = lock
        if(blockPoss.isNotEmpty()) {
            PathingUtil.goto(blockPoss[0])
        }

    }


    @SubscribeEvent
    fun onWorldRender(event: RenderWorldLastEvent) {
        if(enabled) {

            // RenderUtil.drawBox(event, BlockUtil.getCornerBlocks(SlayerUtil.getFakeBoss()!!.position, 2, 1, 2).first, Color.GREEN, true)
            //    RenderUtil.drawBox(event, BlockUtil.getCornerBlocks(SlayerUtil.getFakeBoss()!!.position, 2, 1, 2).second, Color.GREEN, true)

            // draw all columns that are higher than 4 blocks
            /* if(BlockUtil.getColumns( mc.thePlayer.position.add(0, -1, 0), 4, 5, 4, 4).isEmpty().not()) {
                for(block: BlockPos in BlockUtil.getColumns( mc.thePlayer.position.add(0, -1, 0), 4, 5, 4, 4)) {
                    RenderUtil.drawBox(event, block, Color.RED, true)



                }

             */

            // check if there are more than 2 columns higher than 4 blocks
            /*  if(BlockUtil.getColumns( mc.thePlayer.position.add(0, -1, 0), 4, 5, 4, 4).size >= 2) {
                    // get all the columns higher than 4 blocks and put in arrayList
                    var cornerBlocks = BlockUtil.getColumns( mc.thePlayer.position.add(0, -1, 0), 4, 5, 4, 4)
                    // this is where things go wrong ig
                    BlockUtil.getAllCornerBlocks(cornerBlocks)
                    for (blockPos: BlockPos in BlockUtil.getAllCornerBlocks(cornerBlocks))
                       RenderUtil.drawBox(event, blockPos, Color.BLUE, true)
                } else {
                    println("nah")
                }
            }

               */
            blockPoss.clear()
            if (BlockUtil.getBlocks(mc.thePlayer.position, 15, 4, 15).isEmpty()) return

               for(block: BlockPos in BlockUtil.getBlocks(mc.thePlayer.position, 15, 5, 15)) {
                if(BlockUtil.isSingleCorner(block)) {
                    if(BlockUtil.blocksBetweenValid(CalculationContext(SwiftSlayer.instance), block,mc.thePlayer.position.add(0, -1, 0))) {
                        RenderUtil.drawBox(event, block, Color.BLUE, true)
                        blockPoss.add(block)
                    }

                }

            }
      /*  GenericBossKiller.blockPoss = BlockUtil.getBlocks(dev.macrohq.swiftslayer.util.mc.thePlayer.position, 5, 5, 5) as ArrayList<BlockPos>

            for (blockk: BlockPos in GenericBossKiller.blockPoss) {
              //  if(BlockUtil.blocksBetweenValid(player.position.add(0, 0, 0), blockk.add(0, 0, 0))) {
                    RenderUtil.drawBox(event, blockk, Color.WHITE, true)
                } */
           // }

            val targetEntityList = EntityUtil.getMobs(SlayerUtil.getMobClass()).toMutableList()
            for(entity: EntityLiving in targetEntityList) {
                RenderUtil.renderText(entity.position.toVec3(), EntityUtil.getMobCost(entity).toInt().toString())
            }
        }


    }

    companion object {
        var enabled: Boolean = false
        var blockPoss: ArrayList<BlockPos> = ArrayList()
    }
}