package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;


public class PlayerUtil {

    // Returns the Correct BlockPos of the Player.
    public static BlockPos getStandingPosition() {
        return new BlockPos(Ref.player().posX, Ref.player().posY - 1, Ref.player().posZ);
    }
}
