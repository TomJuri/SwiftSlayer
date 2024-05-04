package dev.macrohq.swiftslayer.mixin;

import dev.macrohq.swiftslayer.event.ReceivePacketEvent;
import dev.macrohq.swiftslayer.event.SendPacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class NetworkManagerMixin {
    @Inject(method = "channelRead0*", at = @At("HEAD"))
    private void read(final ChannelHandlerContext context, final Packet<?> packet, final CallbackInfo callback) {
        if (packet.getClass().getSimpleName().startsWith("S"))
            MinecraftForge.EVENT_BUS.post(new ReceivePacketEvent(packet));
    }

    @Inject(method = "sendPacket*", at = @At("HEAD"))
    private void send(Packet<?> packetIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new SendPacketEvent(packetIn));
    }
}