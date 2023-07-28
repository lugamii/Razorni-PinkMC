package net.minecraft.server;

import org.bukkit.craftbukkit.util.LongHash;

import java.util.List;

public interface IChunkProvider {
    boolean isChunkLoaded(int paramInt1, int paramInt2);

    default boolean isChunkLoaded(long hash) {
        return isChunkLoaded(LongHash.msw(hash), LongHash.lsw(hash));
    }

    Chunk getOrCreateChunk(int paramInt1, int paramInt2);

    default Chunk getOrCreateChunk(long hash, int i, int j) {
        return getOrCreateChunk(i, j);
    }

    Chunk getChunkAt(BlockPosition paramBlockPosition);

    void getChunkAt(IChunkProvider paramIChunkProvider, int paramInt1, int paramInt2);

    boolean a(IChunkProvider paramIChunkProvider, Chunk paramChunk, int paramInt1, int paramInt2);

    boolean saveChunks(boolean paramBoolean, IProgressUpdate paramIProgressUpdate);

    boolean unloadChunks();

    boolean canSave();

    String getName();

    List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType paramEnumCreatureType, BlockPosition paramBlockPosition);

    BlockPosition findNearestMapFeature(World paramWorld, String paramString, BlockPosition paramBlockPosition);

    int getLoadedChunks();

    void recreateStructures(Chunk paramChunk, int paramInt1, int paramInt2);

    void c();
}
