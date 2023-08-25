package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class BlockUtil {

    // Converts BlockPos to Vec3
    public static Vec3 blockPosToVec3(BlockPos block){
        return new Vec3(block.getX(), block.getY(), block.getZ());
    }
}
