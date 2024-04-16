package dev.macrohq.swiftslayer.event

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.pathfinder.helper.BlockStateAccessor
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

// a warpper for events that dont desrve their own class but is needed to make stuff work
class GameEventHandler(private val ss: SwiftSlayer) {
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        // fun fact this is not always false so dont remove it
        // i bypassed kotlin nullsafe
        if (ss.playerContext.world == null) return
        try {
            ss.bsa = BlockStateAccessor(ss)
        } catch (ex: Exception) {
            ss.bsa = null
            ex.printStackTrace()
        }
    }
}