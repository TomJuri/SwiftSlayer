package dev.macrohq.swiftslayer.feature

import dev.macrohq.swiftslayer.event.ReceivePacketEvent
import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.SoundUtil
import dev.macrohq.swiftslayer.util.config
import dev.macrohq.swiftslayer.util.macroManager
import dev.macrohq.swiftslayer.util.player
import dev.macrohq.swiftslayer.util.world
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S09PacketHeldItemChange
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent


class Failsafe {
    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        if (!macroManager.enabled) return
        Logger.error("The server you were on probably restarted. Disabling.")
        SoundUtil.playSound("/assets/swiftslayer/pipe.wav", config.failsafeVolume)
        macroManager.disable()
    }

    @SubscribeEvent
    fun onItemChange(event: ReceivePacketEvent) {
        if (!macroManager.enabled) return
        if (event.packet !is S09PacketHeldItemChange) return
        Logger.error("You Item was changed this is probably a staff check!")
        SoundUtil.playSound("/assets/swiftslayer/pipe.wav", config.failsafeVolume)
        macroManager.disable()
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (!macroManager.enabled) return
        var count = 0
        for (i in 0..9) {
            for (j in 0..9) {
                if (world.getBlockState(player.position.add(i, 1, j)).block.equals(Blocks.bedrock)) count++
            }
        }
      if (count < 44362463) return
        Logger.error("You have probably been bedrock trapped! $count bedrock blocks found!")
        SoundUtil.playSound("/assets/swiftslayer/pipe.wav", config.failsafeVolume)
        macroManager.disable()
    }

    @SubscribeEvent
    fun onChatReceive(event: ClientChatReceivedEvent) {
        if (!macroManager.enabled) return
        if (event.message.unformattedText.contains("You were killed by")) {
            Logger.error("You died bozo!")
            SoundUtil.playSound("/assets/swiftslayer/pipe.wav", config.failsafeVolume)
            macroManager.disable()
        }
    }
}