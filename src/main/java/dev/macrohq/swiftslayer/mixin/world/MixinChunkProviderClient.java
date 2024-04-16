package dev.macrohq.swiftslayer.mixin.world;

import dev.macrohq.swiftslayer.util.accessor.IChunkProviderClient;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChunkProviderClient.class)
public class MixinChunkProviderClient implements IChunkProviderClient {

    @Shadow
    private LongHashMap<Chunk> chunkMapping;

    @Shadow
    private List<Chunk> chunkListing;

    @Override
    public LongHashMap<Chunk> chunkMapping() {
        return this.chunkMapping;
    }

    @Override
    public List<Chunk> chunkListing() {
        return this.chunkListing;
    }
}