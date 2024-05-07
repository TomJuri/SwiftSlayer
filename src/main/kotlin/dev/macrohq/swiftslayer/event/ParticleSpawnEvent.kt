package dev.macrohq.swiftslayer.event

import net.minecraftforge.fml.common.eventhandler.Event

data class ParticleSpawnEvent(val particleId: Int, val xCoord: Double, val yCoord: Double, val zCoord: Double): Event() {
}