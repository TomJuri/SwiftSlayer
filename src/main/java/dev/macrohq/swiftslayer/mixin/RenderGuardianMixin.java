package dev.macrohq.swiftslayer.mixin;

import dev.macrohq.swiftslayer.SwiftSlayer;
import kotlin.Pair;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RenderGuardian.class)
public abstract class RenderGuardianMixin {
    @Shadow protected abstract Vec3 func_177110_a(EntityLivingBase entityLivingBaseIn, double d, float f);
    @Inject(method = "doRender(Lnet/minecraft/entity/monster/EntityGuardian;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;subtract(Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/Vec3;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderGuardianBeamMixin(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        Vec3 start = func_177110_a(entity.getTargetedEntity(), (double) entity.getTargetedEntity().height * 0.5D, partialTicks);
        Vec3 end = func_177110_a(entity, entity.getEyeHeight(), partialTicks);
        SwiftSlayer.Companion.getInstance().getEndermanBossKiller().lasers = new Pair<>(start, end);
        SwiftSlayer.Companion.getInstance().getEndermanBossKiller().setLastLaser(System.currentTimeMillis());
    }
}