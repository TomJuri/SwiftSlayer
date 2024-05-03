package dev.macrohq.swiftslayer.mixin;

import dev.macrohq.swiftslayer.util.SwiftEventBus;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EventBus.class, remap = false)
public class EventBusMixin {
    @Inject(method = "post", at = @At("HEAD"))
    private void post(Event event, CallbackInfoReturnable<Boolean> cir) {
        SwiftEventBus.post(event);
    }
}