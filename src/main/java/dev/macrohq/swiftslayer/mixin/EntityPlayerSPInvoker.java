package dev.macrohq.swiftslayer.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayerSP.class)
public interface EntityPlayerSPInvoker {
    @Invoker("isHeadspaceFree")
    boolean invokeIsHeadspaceFree(BlockPos pos, int height);
    @Invoker("isCurrentViewEntity")
    boolean invokeIsCurrentViewEntity();
}