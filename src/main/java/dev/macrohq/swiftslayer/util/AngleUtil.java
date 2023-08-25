package dev.macrohq.swiftslayer.util;

import dev.macrohq.swiftslayer.classes.Angle;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;


public class AngleUtil {
    // Converts minecraft's yaw to 360 yaw (but inverse)
    public static float get360Yaw(float yaw){
        return ((yaw%360)+360)%360;
    }

    // Returns Minecraft Angle (Yaw, Pitch) To Vec3 Coordinates
    public static Angle getYawPitch(Vec3 vec){
        float dx = (float) (vec.xCoord - Ref.mc().thePlayer.posX);
        float dy = (float) (vec.yCoord - (Ref.mc().thePlayer.posY + Ref.mc().thePlayer.eyeHeight));
        float dz = (float) (vec.zCoord - Ref.mc().thePlayer.posZ);

        float yaw = (float) -Math.toDegrees(Math.atan2(dx,dz));
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx*dx + dz*dz)));
        return new Angle(yaw, pitch);
    }

    // Returns Minecraft Angle (Yaw, Pitch) To BlockPos
    public static Angle getYawPitch(BlockPos block){
        return getYawPitch(BlockUtil.blockPosToVec3(block).addVector(0.5,0.5,0.5));
    }
}
