package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class BlockUtil {
    public static Vec3 blockPosToVec3(BlockPos block) {
        return new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
    }
}
