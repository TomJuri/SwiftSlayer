package dev.macrohq.swiftslayer.util;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil {
    private static Rotation startRotation = new Rotation(0f, 0f);
    private static Rotation endRotation = new Rotation(0f, 0f);
    private static long startTime = 0L;
    private static long endTime = 0L;
    public static boolean done = true;

    public static void ease(Rotation rotation, long durationMillis) {
        if (!done) return;
        done = false;
        startRotation = new Rotation(Ref.player().rotationYaw, Ref.player().rotationPitch);
        Rotation neededChange = getNeededChange(startRotation, rotation);
        endRotation = new Rotation(startRotation.getYaw() + neededChange.getYaw(), startRotation.getPitch() + neededChange.getPitch());
        startTime = System.currentTimeMillis();
        endTime = startTime + durationMillis;
    }

    public static void easeDirection(Rotation rotation, long durationMillis, Direction direction) {
        if (!done) return;
        done = false;
        Rotation currentRotation = new Rotation(Ref.player().rotationYaw, Ref.player().rotationPitch);
        float endRotationYaw = direction == Direction.LEFT ? currentRotation.getYaw() - rotation.getYaw() : currentRotation.getYaw() + rotation.getYaw();
        startRotation = currentRotation;
        endRotation = new Rotation(endRotationYaw, rotation.getPitch());
        startTime = System.currentTimeMillis();
        endTime = startTime + durationMillis;
    }

    public static void onRenderWorldLast() {
        if (done) return;
        if (System.currentTimeMillis() <= endTime) {
            Ref.player().rotationYaw = interpolate(startRotation.getYaw(), endRotation.getYaw());
            Ref.player().rotationPitch = interpolate(startRotation.getPitch(), endRotation.getPitch());
            return;
        }
        Ref.player().rotationYaw = endRotation.getYaw();
        Ref.player().rotationPitch = endRotation.getPitch();
        done = true;
    }

    private static float interpolate(float start, float end) {
        float spentMillis = (float) (System.currentTimeMillis() - startTime);
        float relativeProgress = spentMillis / (endTime - startTime);
        return (end - start) * easeOutCubic(relativeProgress) + start;
    }

    private static float easeOutCubic(float number) {
        return (float) (1.0 - Math.pow(1.0 - number, 3.0));
    }

    private static Rotation getNeededChange(Rotation startRot, Rotation endRot) {
        float yawChange = MathHelper.wrapAngleTo180_float(endRot.getYaw()) - MathHelper.wrapAngleTo180_float(startRot.getYaw());
        if (yawChange <= -180.0f) yawChange += 360.0f;
        else if (yawChange > 180.0f) yawChange += -360.0f;
        return new Rotation(yawChange, endRot.getPitch() - startRot.getPitch());
    }

    public static Rotation getAngles(Vec3 vec) {
        double diffX = vec.xCoord - Ref.player().posX;
        double diffY = vec.yCoord - (Ref.player().posY + Ref.player().getEyeHeight());
        double diffZ = vec.zCoord - Ref.player().posZ;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90f;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new Rotation(
                Ref.player().rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Ref.player().rotationYaw),
                Ref.player().rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Ref.player().rotationPitch)
        );
    }

    public static class Rotation {
        private final float yaw;
        private final float pitch;

        public Rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }
    }

    public enum Direction {
        LEFT,
        RIGHT
    }
}
