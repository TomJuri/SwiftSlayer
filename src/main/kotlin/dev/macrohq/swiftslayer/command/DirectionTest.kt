package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.player
import me.kbrewster.eventbus.Subscribe
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLiving
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import kotlin.math.abs
import kotlin.math.atan2


@Command("directionTest")
class DirectionTest {


    var mc: Minecraft = Minecraft.getMinecraft()
    @SubCommand
    private fun direction(lock: Boolean) {
        //gameSettings.keyBindSneak.setPressed(true)
        enabled = lock
        Logger.info(player.totalArmorValue)

    }

    fun isInFOV(entity: EntityLiving): Boolean {
        val playerLook: Vec3 = mc.thePlayer.lookVec
        val relativePosition: Vec3 = entity.getPositionVector().subtract(mc.thePlayer.positionVector)

        var angle = atan2(relativePosition.zCoord, relativePosition.xCoord) - atan2(playerLook.zCoord, playerLook.xCoord)

        angle = abs(Math.toDegrees(angle))

        // Adjust angle to be within 0-180 degrees
        if (angle > 180) {
            angle = 360 - angle
        }


        // Check if angle is within FOV
        return (angle <= mc.gameSettings.fovSetting / 2 && mc.thePlayer.canEntityBeSeen(entity))
    }


    @Subscribe
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

            /*  GenericBossKiller.blockPoss = BlockUtil.getBlocks(dev.macrohq.swiftslayer.util.mc.thePlayer.position, 5, 5, 5) as ArrayList<BlockPos>

            for (blockk: BlockPos in GenericBossKiller.blockPoss) {
              //  if(BlockUtil.blocksBetweenValid(player.position.add(0, 0, 0), blockk.add(0, 0, 0))) {
                    RenderUtil.drawBox(event, blockk, Color.WHITE, true)
                } */
            // }


        }


    }


    companion object {
        var enabled: Boolean = false
        var blockPoss: ArrayList<BlockPos> = ArrayList()
    }
}