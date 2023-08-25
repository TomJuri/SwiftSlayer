package dev.macrohq.swiftslayer.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class AngleUtil {
    public static RotationUtil.Rotation getYawPitch(Vec3 vec) {
        float dx = (float) (vec.xCoord - Ref.player().posX);
        float dy = (float) (vec.yCoord - (Ref.player().posY + Ref.player().eyeHeight));
        float dz = (float) (vec.zCoord - Ref.player().posZ);
        float yaw = (float) -Math.toDegrees(Math.atan2(dx,dz));
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx*dx + dz*dz)));
        return new RotationUtil.Rotation(yaw, pitch);
    }

    public static float getDiffBetweenBlockPos(BlockPos first, BlockPos second) {
        return getYawPitch(BlockUtil.blockPosToVec3(first)).getYaw() - getYawPitch(BlockUtil.blockPosToVec3(second)).getYaw();
    }

    public static RotationUtil.Rotation getYawPitch(BlockPos block) {
        return getYawPitch(BlockUtil.blockPosToVec3(block));
    }
}
