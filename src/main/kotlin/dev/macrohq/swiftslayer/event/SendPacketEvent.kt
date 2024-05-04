package dev.macrohq.swiftslayer.event

import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.Event

data class SendPacketEvent(val packet: Packet<*>) : Event()