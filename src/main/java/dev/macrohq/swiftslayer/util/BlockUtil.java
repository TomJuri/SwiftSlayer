package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BlockUtil {
    public static Vec3 blockPosToVec3(BlockPos block) {
        return new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
    }

    public static List<BlockPos> neighbourGenerator(BlockPos mainBlock, int size){
    return neighbourGenerator(mainBlock, size, size, size);
    }

    // Plan to reuse this so dont delete :angiest:
    public static List<BlockPos> neighbourGenerator(BlockPos mainBlock, int xD, int yD, int zD){
        List<BlockPos> neighbours = new ArrayList<>();
        for(int x = -xD; x <= xD; x++){
            for(int y = -yD; y <= yD; y++){
                for(int z = -zD; z <= zD; z++){
                    neighbours.add(new BlockPos(mainBlock.getX()+x, mainBlock.getY()+y, mainBlock.getZ()+z));
                }
            }
        }
        return neighbours;
    }
}
