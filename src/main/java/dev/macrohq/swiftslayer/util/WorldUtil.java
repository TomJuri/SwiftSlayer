package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;

public class WorldUtil {

    // Returns the distance between two blockPos
    public static float distanceBetweenBlockPos(BlockPos start, BlockPos end){
        return (float)Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2) + Math.pow(start.getZ() - end.getZ(), 2));
    }
}
