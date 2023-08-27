package dev.macrohq.swiftslayer.mixin;

import dev.macrohq.swiftslayer.SwiftSlayer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {
    @Redirect(method = "moveFlying", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
    private float moveFlying(Entity instance) {
        return SwiftSlayer.Companion.getInstance().getPathExecutor().getRunning() ?
                SwiftSlayer.Companion.getInstance().getPathExecutor().getDirectionYaw() :
                instance.rotationYaw;
    }
}
