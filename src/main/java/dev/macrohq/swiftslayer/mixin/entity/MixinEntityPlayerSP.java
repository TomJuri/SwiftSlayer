package dev.macrohq.swiftslayer.mixin.entity;

import dev.macrohq.swiftslayer.SwiftSlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends MixinAbstractClientPlayer {
    @Override
    public void moveFlying(float strafe, float forward, final float friction) {
        final float yaw =
                SwiftSlayer.Companion.getInstance().getPathExecutor().getEnabled() ?
                SwiftSlayer.Companion.getInstance().getPathExecutor().getDirectionYaw() : rotationYaw;
        float f = (strafe * strafe) + (forward * forward);
        if (1.0E-4f <= f) {
            if (1.0f > (f = MathHelper.sqrt_float(f))) {
                f = 1.0f;
            }
            f = friction / f;
            final float f1 = MathHelper.sin((yaw * (float) Math.PI) / 180.0f);
            final float f2 = MathHelper.cos((yaw * (float) Math.PI) / 180.0f);
            motionX += (((strafe *= f)) * f2) - (((forward *= f)) * f1);
            motionZ += (forward * f2) + (strafe * f1);
        }
    }
}