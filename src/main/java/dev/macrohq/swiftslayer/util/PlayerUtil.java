package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;


public class PlayerUtil {

    // Returns the Correct BlockPos of the Player.
    public static BlockPos getPosition(){
        return new BlockPos(Ref.mc().thePlayer.posX, Ref.mc().thePlayer.posY-1, Ref.mc().thePlayer.posZ);
    }
}
