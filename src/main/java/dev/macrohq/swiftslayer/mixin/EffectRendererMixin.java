package dev.macrohq.swiftslayer.mixin;


import dev.macrohq.swiftslayer.event.ParticleSpawnEvent;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EffectRenderer.class)
public class EffectRendererMixin {

@Inject(method = "spawnEffectParticle", at = @At("HEAD"))
    private void spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double p_178927_10_, double p_178927_12_, int[] p_178927_14_, CallbackInfoReturnable<EntityFX> cir) {
    MinecraftForge.EVENT_BUS.post(new ParticleSpawnEvent(particleId, xCoord, yCoord, zCoord));
}
}
