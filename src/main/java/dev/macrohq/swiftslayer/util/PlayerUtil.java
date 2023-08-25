package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;

import static dev.macrohq.swiftslayer.SwiftSlayer.mc;

public class PlayerUtil {

    // Returns the Correct BlockPos of the Player.
    public static BlockPos getPosition(){
        return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ);
    }
}
