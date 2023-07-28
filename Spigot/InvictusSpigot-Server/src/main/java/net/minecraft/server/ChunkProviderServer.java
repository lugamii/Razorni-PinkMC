package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.craftbukkit.util.LongObjectHashMap;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.io.IOException;
import java.util.List;
import java.util.Random;
// CraftBukkit end

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger b = LogManager.getLogger();
    public LongSet unloadQueue = new LongArraySet();
    public Chunk emptyChunk;
    public IChunkProvider chunkProvider;
    public IChunkLoader chunkLoader;
    public boolean forceChunkLoad = false; // CraftBukkit - true -> false

    protected Chunk lastChunkByPos = null;
    public LongObjectHashMap<Chunk> chunks = new LongObjectHashMap<Chunk>() {
        @Override
        public Chunk get(long key) {
            if (lastChunkByPos != null && key == lastChunkByPos.chunkHashKey)
                return lastChunkByPos;
            return lastChunkByPos = super.get(key);
        }

        @Override
        public Chunk remove(long key) {
            if (lastChunkByPos != null && key == lastChunkByPos.chunkHashKey)
                lastChunkByPos = null;
            return super.remove(key);
        }
    };

    public WorldServer world;

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
        emptyChunk = new EmptyChunk(worldserver, 0, 0);
        world = worldserver;
        chunkLoader = ichunkloader;
        chunkProvider = ichunkprovider;
    }

    public boolean isChunkLoaded(int i, int j) {
        return chunks.containsKey(LongHash.toLong(i, j)); // CraftBukkit
    }

    public boolean isChunkLoaded(long hash) {
        return chunks.containsKey(hash);
    }

    // CraftBukkit start - Change return type to Collection and return the values of our chunk map
    public java.util.Collection<Chunk> a() {
        // return this.chunkList;
        return chunks.values();
        // CraftBukkit end
    }

    public void queueUnload(int i, int j) {
        if (!InvictusConfig.unloadChunks)
            return;
        long hash = LongHash.toLong(i, j);

        // PaperSpigot start - Asynchronous lighting updates
        Chunk chunk = chunks.get(hash);
        if (chunk != null) {
            if (chunk.world.paperSpigotConfig.useAsyncLighting && (chunk.pendingLightUpdates.get() > 0 || chunk.world.getTime() - chunk.lightUpdateTime < 20))
                return;
            for (List<Entity> entities : chunk.entitySlices) {
                for (Entity entity : entities) {
                    if (entity.loadChunks) {
                        return;
                    }
                }
            }
            if (world.worldProvider.e()) {
                if (!world.c(i, j)) {
                    unloadQueue.add(hash); // Vortex
                    chunk.mustSave = true;
                }
            } else {
                unloadQueue.add(hash); // Vortex
                chunk.mustSave = true;
            }
        }
    }

    public void b() {
        for (Chunk chunk : chunks.values()) {
            queueUnload(chunk.locX, chunk.locZ);
        }
    }

    // CraftBukkit start - Add async variant, provide compatibility
    public Chunk getChunkIfLoaded(int x, int z) {
        return chunks.get(LongHash.toLong(x, z));
    }

    public Chunk getChunkIfLoaded(long hash) {
        return chunks.get(hash);
    }

    public Chunk getChunkAt(int i, int j) {
        return getChunkAt(LongHash.toLong(i, j), i ,j, null);
    }

    public Chunk getChunkAt(int i, int j, Runnable runnable) {
        return getChunkAt(LongHash.toLong(i, j), i, j, runnable);
    }

    public Chunk getChunkAt(long hash, int i, int j) {
        return getChunkAt(hash, i, j, null);
    }

    public Chunk getChunkAt(long hash, int i, int j, Runnable runnable) {
        unloadQueue.remove(hash);
        Chunk chunk = this.chunks.get(hash);
        ChunkRegionLoader loader = null;

        if (this.chunkLoader instanceof ChunkRegionLoader) {
            loader = (ChunkRegionLoader) this.chunkLoader;

        }
        // We can only use the queue for already generated chunks
        if (chunk == null && loader != null && loader.chunkExists(world, i, j)) {
            if (runnable != null) {
                ChunkIOExecutor.queueChunkLoad(world, loader, this, i, j, runnable);
                return null;
            } else {
                chunk = ChunkIOExecutor.syncChunkLoad(world, loader, this, i, j);
            }
        } else if (chunk == null) {
            chunk = originalGetChunkAt(i, j);
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) {
            runnable.run();
        }

        return chunk;
    }

    public Chunk originalGetChunkAt(int i, int j) {
        long key = LongHash.toLong(i, j);
        this.unloadQueue.remove(key);
        Chunk chunk = this.chunks.get(key);
        boolean newChunk = false;
        // CraftBukkit end

        Server server = world.getServer();

        if (chunk == null) {
            chunk = this.loadChunk(i, j);
            if (chunk == null) {
                if (this.chunkProvider == null) {
                    chunk = this.emptyChunk;
                } else {
                    try {
                        chunk = this.chunkProvider.getOrCreateChunk(i, j);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

                        crashreportsystemdetails.a("Location", String.format("%d,%d", i, j));
                        crashreportsystemdetails.a("Position hash", key); // CraftBukkit - Use LongHash
                        crashreportsystemdetails.a("Generator", this.chunkProvider.getName());
                        throw new ReportedException(crashreport);
                    }
                }
                newChunk = true; // CraftBukkit
            }

            this.chunks.put(key, chunk);

            chunk.addEntities();

            // CraftBukkit start
            if (server != null) {
                /*
                 * If it's a new world, the first few chunks are generated inside
                 * the World constructor. We can't reliably alter that, so we have
                 * no way of creating a CraftWorld/CraftServer at that point.
                 */
                server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, newChunk));
            }

            // Update neighbor counts
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                    if (neighbor != null) {
                        neighbor.setNeighborLoaded(-x, -z);
                        chunk.setNeighborLoaded(x, z);
                    }
                }
            }
            // CraftBukkit end
            chunk.loadNearby(this, this, i, j);
        }

        return chunk;
    }

    public Chunk getOrCreateChunk(long hash, int i, int j) {
        Chunk chunk = chunks.get(hash);
        chunk = chunk == null ? (!world.ad() && !forceChunkLoad ? emptyChunk : getChunkAt(hash, i, j)) : chunk;

        if (chunk == emptyChunk) return chunk;
        if (i != chunk.locX || j != chunk.locZ) {
            // Paper start
            String msg = "Chunk (" + chunk.locX + ", " + chunk.locZ + ") stored at  (" + i + ", " + j + ") in world '" + world.getWorld().getName() + "'";
            b.error(msg);
            b.error(chunk.getClass().getName());
            // Paper end
        }
        return chunk;
    }

    public Chunk getOrCreateChunk(int i, int j) {
        return getOrCreateChunk(LongHash.toLong(i, j), i, j);
    }

    public Chunk loadChunk(int i, int j) {
        if (this.chunkLoader == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.chunkLoader.a(this.world, i, j);

                if (chunk != null) {
                    chunk.setLastSaved(this.world.getTime());
                    if (this.chunkProvider != null) {
                        this.chunkProvider.recreateStructures(chunk, i, j);
                    }
                }

                return chunk;
            } catch (Exception exception) {
                // Paper start
                String msg = "Couldn't load chunk";
                ChunkProviderServer.b.error(msg, exception);
                // Paper end
                return null;
            }
        }
    }

    public void saveChunkNOP(Chunk chunk) {
        if (this.chunkLoader != null) {
            try {
                this.chunkLoader.b(this.world, chunk);
            } catch (Exception exception) {
                ChunkProviderServer.b.error("Couldn't save entities", exception);
            }
        }
    }

    public void saveChunk(Chunk chunk) {
        if (this.chunkLoader != null) {
            try {
                chunk.setLastSaved(this.world.getTime());
                this.chunkLoader.a(this.world, chunk);
            } catch (IOException ioexception) {
                ChunkProviderServer.b.error("Couldn't save chunk", ioexception);
            } catch (ExceptionWorldConflict exceptionworldconflict) {
                ChunkProviderServer.b.error("Couldn't save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
            }
        }
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        Chunk chunk = this.getOrCreateChunk(i, j);

        if (!chunk.isDone()) {
            chunk.n();
            if (this.chunkProvider != null) {
                this.chunkProvider.getChunkAt(ichunkprovider, i, j);

                // CraftBukkit start
                BlockSand.instaFall = true;
                Random random = new Random();
                random.setSeed(world.getSeed());
                long xRand = random.nextLong() / 2L * 2L + 1L;
                long zRand = random.nextLong() / 2L * 2L + 1L;
                random.setSeed((long) i * xRand + (long) j * zRand ^ world.getSeed());

                org.bukkit.World world = this.world.getWorld();
                if (world != null) {
                    this.world.populating = true;
                    try {
                        for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                            populator.populate(world, random, chunk.bukkitChunk);
                        }
                    } finally {
                        this.world.populating = false;
                    }
                }
                BlockSand.instaFall = false;
                this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.world.ChunkPopulateEvent(chunk.bukkitChunk));
                // CraftBukkit end

                chunk.e();
            }
        }

    }

    public boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j) {
        if (this.chunkProvider != null && this.chunkProvider.a(ichunkprovider, chunk, i, j)) {
            getOrCreateChunk(i, j).e();
            return true;
        } else {
            return false;
        }
    }

    public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
        // CraftBukkit start
        int i = 0;
        for (Chunk chunk : this.chunks.values()) {
            if (flag)
                saveChunkNOP(chunk);
            if (chunk.a(flag)) {
                saveChunk(chunk);
                chunk.f(false);
                i++;
                if (!flag && i >= 6)
                    return false;
            }
        }
        return true;
    }

    public void c() {
        if (this.chunkLoader != null) {
            this.chunkLoader.b();
        }
    }

    public boolean unloadChunks() {
        if (!world.savingDisabled) {
            // CraftBukkit start
            Server server = world.getServer();
            LongIterator iterator = unloadQueue.iterator();
            for (int i = 0; i < 100 && iterator.hasNext(); i++) {
                long chunkcoordinates = iterator.nextLong();
                iterator.remove();

                Chunk chunk = chunks.get(chunkcoordinates);
                if (chunk == null) {
                    continue;
                }

                ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
                server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {

                    chunk.removeEntities();
                    saveChunk(chunk);
                    saveChunkNOP(chunk);
                    chunks.remove(chunkcoordinates); // CraftBukkit

                    // Update neighbor counts
                    for (int x = -2; x < 3; x++) {
                        for (int z = -2; z < 3; z++) {
                            if (x == 0 && z == 0) {
                                continue;
                            }

                            Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                            if (neighbor != null) {
                                neighbor.setNeighborUnloaded(-x, -z);
                                chunk.setNeighborUnloaded(x, z);
                            }
                        }
                    }
                }
            }
            // CraftBukkit end

            if (chunkLoader != null) {
                chunkLoader.a();
            }
        }

        return chunkProvider.unloadChunks();
    }

    public boolean canSave() {
        return !this.world.savingDisabled;
    }

    public String getName() {
        // CraftBukkit - this.chunks.count() -> .size()
        return "ServerChunkCache: " + this.chunks.size() + " Drop: " + this.unloadQueue.size();
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.chunkProvider.getMobsFor(enumcreaturetype, blockposition);
    }

    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
        return this.chunkProvider.findNearestMapFeature(world, s, blockposition);
    }

    public int getLoadedChunks() {
        // CraftBukkit - this.chunks.count() -> this.chunks.size()
        return this.chunks.size();
    }

    public void recreateStructures(Chunk chunk, int i, int j) {}

    public Chunk getChunkAt(BlockPosition blockposition) {
        return this.getOrCreateChunk(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }
}
