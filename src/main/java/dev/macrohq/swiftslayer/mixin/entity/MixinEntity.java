package dev.macrohq.swiftslayer.mixin.entity;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public double motionZ;
    @Shadow
    public double motionX;
    @Shadow
    public float rotationYaw;

    @Shadow
    public abstract void moveFlying(float strafe, float forward, float friction);
}