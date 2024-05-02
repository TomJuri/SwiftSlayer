package dev.macrohq.swiftslayer.mixin;

import dev.macrohq.swiftslayer.SwiftSlayer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.LoaderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LoadController.class, remap = false)
public class LoadControllerMixin {
    @Inject(method = "distributeStateMessage(Lnet/minecraftforge/fml/common/LoaderState;[Ljava/lang/Object;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/eventbus/EventBus;post(Ljava/lang/Object;)V"))
    private void distributeStateMessage(LoaderState state, Object[] eventData, CallbackInfo ci) {
        if (state == LoaderState.INITIALIZATION)
            SwiftSlayer.INSTANCE.init();
    }
}