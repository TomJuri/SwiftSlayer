package dev.macrohq.swiftslayer.mixin.entity;

import dev.macrohq.swiftslayer.SwiftSlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends MixinAbstractClientPlayer {
    @Override
    public void moveFlying(float strafe, float forward, float friction) {
        float yaw =
                SwiftSlayer.Companion.getInstance().getPathExecutor().getEnabled() ?
                SwiftSlayer.Companion.getInstance().getPathExecutor().getDirectionYaw() : this.rotationYaw;
        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4f) {
            if ((f = MathHelper.sqrt_float(f)) < 1.0f) f = 1.0f;
            f = friction / f;
            float f1 = MathHelper.sin(yaw * (float) Math.PI / 180f);
            float f2 = MathHelper.cos(yaw * (float) Math.PI / 180f);
            this.motionX += (strafe *= f) * f2 - (forward *= f) * f1;
            this.motionZ += forward * f2 + strafe * f1;
        }
    }
}