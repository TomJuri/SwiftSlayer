package dev.macrohq.swiftslayer.util.accessor

import net.minecraft.util.LongHashMap
import net.minecraft.world.chunk.Chunk

interface IChunkProviderClient {
    fun chunkMapping(): LongHashMap<Chunk>
    fun chunkListing(): List<Chunk>
}