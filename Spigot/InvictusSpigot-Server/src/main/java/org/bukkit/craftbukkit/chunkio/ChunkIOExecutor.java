package org.bukkit.craftbukkit.chunkio;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.ChunkRegionLoader;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.util.AsynchronousExecutor;

public class ChunkIOExecutor {
    private static final int BASE_THREADS = InvictusConfig.chunkLoadingThreads; // PaperSpigot - Bumped value
    private static final int PLAYERS_PER_THREAD = InvictusConfig.chunkPlayersPerThread;
    private static final AsynchronousExecutor<QueuedChunk, Chunk, Runnable, RuntimeException> instance = new AsynchronousExecutor<>(new ChunkIOProvider(), BASE_THREADS);

    public static Chunk syncChunkLoad(World world, ChunkRegionLoader loader, ChunkProviderServer provider, int x, int z) {
        return instance.getSkipQueue(new QueuedChunk(x, z, loader, world, provider));
    }

    public static void queueChunkLoad(World world, ChunkRegionLoader loader, ChunkProviderServer provider, int x, int z, Runnable runnable) {
        instance.add(new QueuedChunk(x, z, loader, world, provider), runnable);
    }

    // Abuses the fact that hashCode and equals for QueuedChunk only use world and coords
    public static void dropQueuedChunkLoad(World world, int x, int z, Runnable runnable) {
        instance.drop(new QueuedChunk(x, z, null, world, null), runnable);
    }

    public static void adjustPoolSize(int players) {
        instance.setActiveThreads(Math.max(BASE_THREADS, (int) Math.ceil(players / PLAYERS_PER_THREAD)));
    }

    public static void tick() {
        instance.finishActive();
    }
}
