package net.minecraft.server;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerChunkMap {

    private static final Logger a = LogManager.getLogger();
    private final WorldServer world;
    private final List<EntityPlayer> managedPlayers = Lists.newArrayList();
    private final LongHashMap<PlayerChunk> d = new LongHashMap<>(); // Vortex - dont change
    private final List<PlayerChunk> e = new ArrayList<>();
    // private long h;
    private final int[][] i = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    // private final Queue<PlayerChunkMap.PlayerChunk> f = new
    // java.util.concurrent.ConcurrentLinkedQueue<PlayerChunkMap.PlayerChunk>(); //
    // CraftBukkit ArrayList -> ConcurrentLinkedQueue
    private int g;
    // private boolean wasNotEmpty; // CraftBukkit - add field; Vortex -
    // remove field;

    public PlayerChunkMap(WorldServer worldserver, int viewDistance /* Spigot */) {
        this.world = worldserver;
        this.a(viewDistance); // Spigot
    }

    public static int getFurthestViewableBlock(int i) {
        return i * 16 - 16;
    }

    public WorldServer a() {
        return this.world;
    }

    public void flush() {
        for (PlayerChunk o : this.e) {
            o.b();
        }

        this.e.clear();
    }

    public boolean a(int i, int j) {
        return this.d.getEntry(i + 2147483647L | j + 2147483647L << 32) != null;
    }

    private PlayerChunk a(int i, int j, boolean flag) {
        long k = i + 2147483647L | j + 2147483647L << 32;
        PlayerChunk playerchunkmap_playerchunk = this.d.getEntry(k);
        if (playerchunkmap_playerchunk == null && flag) {
            playerchunkmap_playerchunk = new PlayerChunk(i, j);
            this.d.put(k, playerchunkmap_playerchunk);
        }

        return playerchunkmap_playerchunk;
    }
    // CraftBukkit end

    // CraftBukkit start - add method
    public final boolean isChunkInUse(int x, int z) {
        PlayerChunk pi = a(x, z, false);
        if (pi != null) {
            return pi.b.size() > 0;
        }
        return false;
    }

    public void flagDirty(BlockPosition blockposition) {
        int i = blockposition.getX() >> 4;
        int j = blockposition.getZ() >> 4;
        PlayerChunk playerchunkmap_playerchunk = this.a(i, j, false);

        if (playerchunkmap_playerchunk != null) {
            playerchunkmap_playerchunk.a(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
        }

    }

    public void flagDirty(final int x, final int y, final int z) {
        int i = x >> 4;
        int j = z >> 4;
        PlayerChunk playerchunkmap_playerchunk = this.a(i, j, false);

        if (playerchunkmap_playerchunk != null) {
            playerchunkmap_playerchunk.a(x & 15, y, z & 15);
        }

    }

    public void addPlayer(EntityPlayer entityplayer) {
        int i = MathHelper.floor(entityplayer.locX) >> 4;
        int j = MathHelper.floor(entityplayer.locZ) >> 4;

        entityplayer.d = entityplayer.locX;
        entityplayer.e = entityplayer.locZ;

        List<ChunkCoordIntPair> chunkList = new ArrayList<>();

        final int view = entityplayer.viewDistance;
        for (int k = i - view; k <= i + view; ++k) {
            for (int l = j - view; l <= j + view; ++l) {
                chunkList.add(new ChunkCoordIntPair(k, l));
            }
        }

        chunkList.sort(new ChunkCoordComparator(entityplayer));
        for (ChunkCoordIntPair pair : chunkList) {
            this.a(pair.x, pair.z, true).a(entityplayer);
        }
        // CraftBukkit end

        this.managedPlayers.add(entityplayer);
        this.b(entityplayer);
    }

    public void b(EntityPlayer entityplayer) {
        ArrayList<ChunkCoordIntPair> arraylist = Lists.newArrayList(entityplayer.chunkCoordIntPairQueue);
        int i = 0;
        int j = entityplayer.viewDistance; // PaperSpigot - Player view distance API
        final int k = MathHelper.floor(entityplayer.locX) >> 4;
        final int l = MathHelper.floor(entityplayer.locZ) >> 4;

        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = this.a(k, l, true).location;

        entityplayer.chunkCoordIntPairQueue.clear();
        if (arraylist.contains(chunkcoordintpair)) {
            entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
        }

        int k1;

        for (k1 = 1; k1 <= j * 2; ++k1) {
            for (int l1 = 0; l1 < 2; ++l1) {
                int[] aint = this.i[i++ % 4];

                for (int i2 = 0; i2 < k1; ++i2) {
                    i1 += aint[0];
                    j1 += aint[1];
                    chunkcoordintpair = this.a(k + i1, l + j1, true).location;
                    if (arraylist.contains(chunkcoordintpair)) {
                        entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
                    }
                }
            }
        }

        i %= 4;

        for (k1 = 0; k1 < j * 2; ++k1) {
            i1 += this.i[i][0];
            j1 += this.i[i][1];
            chunkcoordintpair = this.a(k + i1, l + j1, true).location;
            if (arraylist.contains(chunkcoordintpair)) {
                entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
            }
        }

    }

    public void removePlayer(EntityPlayer entityplayer) {
        final int i = MathHelper.floor(entityplayer.d) >> 4;
        final int j = MathHelper.floor(entityplayer.e) >> 4;
        final int view = entityplayer.viewDistance;
        for (int k = i - view; k <= i + view; ++k) {
            for (int l = j - view; l <= j + view; ++l) {
                PlayerChunk playerchunkmap_playerchunk = this.a(k, l, false);

                if (playerchunkmap_playerchunk != null) {
                    playerchunkmap_playerchunk.b(entityplayer);
                }
            }
        }

        this.managedPlayers.remove(entityplayer);
    }

    private boolean a(int i, int j, int k, int l, int i1) {
        int j1 = i - k;
        int k1 = j - l;

        return j1 >= -i1 && j1 <= i1 && k1 >= -i1 && k1 <= i1;
    }

    public void movePlayer(EntityPlayer entityplayer) {
        int i = MathHelper.floor(entityplayer.locX) >> 4;
        int j = MathHelper.floor(entityplayer.locZ) >> 4;
        double d0 = entityplayer.d - entityplayer.locX;
        double d1 = entityplayer.e - entityplayer.locZ;

        if ((d0 * d0 + d1 * d1) >= 64.0D) {
            int k = MathHelper.floor(entityplayer.d) >> 4;
            int l = MathHelper.floor(entityplayer.e) >> 4;
            int i1 = entityplayer.viewDistance; // PaperSpigot - Player view distance API
            int j1 = i - k;
            int k1 = j - l;
            List<ChunkCoordIntPair> chunksToLoad = new ArrayList<>(); // CraftBukkit

            if (j1 != 0 || k1 != 0) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (!this.a(l1, i2, k, l, i1)) {
                            chunksToLoad.add(new ChunkCoordIntPair(l1, i2)); // CraftBukkit
                        }

                        if (!this.a(l1 - j1, i2 - k1, i, j, i1)) {
                            PlayerChunk playerchunkmap_playerchunk = this.a(l1 - j1, i2 - k1, false);

                            if (playerchunkmap_playerchunk != null) {
                                playerchunkmap_playerchunk.b(entityplayer);
                            }
                        }
                    }
                }

                this.b(entityplayer);
                entityplayer.d = entityplayer.locX;
                entityplayer.e = entityplayer.locZ;

                // CraftBukkit start - send nearest chunks first
                chunksToLoad.sort(new ChunkCoordComparator(entityplayer));
                for (ChunkCoordIntPair pair : chunksToLoad) {
                    this.a(pair.x, pair.z, true).a(entityplayer);
                }

                if (j1 > 1 || j1 < -1 || k1 > 1 || k1 < -1) {
                    entityplayer.chunkCoordIntPairQueue.sort(new ChunkCoordComparator(entityplayer));
                }
                // CraftBukkit end
            }
        }
    }

    public boolean a(EntityPlayer entityplayer, int i, int j) {
        PlayerChunk playerchunkmap_playerchunk = this.a(i, j, false);

        return playerchunkmap_playerchunk != null && playerchunkmap_playerchunk.b.contains(entityplayer) && !entityplayer.chunkCoordIntPairQueue.contains(playerchunkmap_playerchunk.location);
    }

    public void a(int i) {
        i = MathHelper.clamp(i, 3, 32);
        if (i != this.g) {
            int j = i - this.g;
            ArrayList<EntityPlayer> arraylist = Lists.newArrayList(this.managedPlayers);

            for (EntityPlayer entityplayer : arraylist) {
                int k = (int) entityplayer.locX >> 4;
                int l = (int) entityplayer.locZ >> 4;
                int i1;
                int j1;

                if (j > 0) {
                    for (i1 = k - i; i1 <= k + i; ++i1) {
                        for (j1 = l - i; j1 <= l + i; ++j1) {
                            PlayerChunk playerchunkmap_playerchunk = this.a(i1, j1, true);

                            if (!playerchunkmap_playerchunk.b.contains(entityplayer)) {
                                playerchunkmap_playerchunk.a(entityplayer);
                            }
                        }
                    }
                } else {
                    for (i1 = k - this.g; i1 <= k + this.g; ++i1) {
                        for (j1 = l - this.g; j1 <= l + this.g; ++j1) {
                            if (!this.a(i1, j1, k, l, i)) {
                                this.a(i1, j1, true).b(entityplayer);
                            }
                        }
                    }
                }
            }

            this.g = i;
        }
    }
    // PaperSpigot end

    // PaperSpigot start - Player view distance API
    public void updateViewDistance(EntityPlayer player, int viewDistance) {
        viewDistance = MathHelper.clamp(viewDistance, 3, 32);
        if (viewDistance != player.viewDistance) {
            int cx = (int) player.locX >> 4;
            int cz = (int) player.locZ >> 4;

            if (viewDistance - player.viewDistance > 0) {
                for (int x = cx - viewDistance; x <= cx + viewDistance; ++x) {
                    for (int z = cz - viewDistance; z <= cz + viewDistance; ++z) {
                        PlayerChunk playerchunkmap_playerchunk = this.a(x, z, true);

                        if (!playerchunkmap_playerchunk.b.contains(player)) {
                            playerchunkmap_playerchunk.a(player);
                        }
                    }
                }
            } else {
                for (int x = cx - player.viewDistance; x <= cx + player.viewDistance; ++x) {
                    for (int z = cz - player.viewDistance; z <= cz + player.viewDistance; ++z) {
                        if (!this.a(x, z, cx, cz, viewDistance)) {
                            this.a(x, z, true).b(player);
                        }
                    }
                }
            }

            player.viewDistance = viewDistance;
        }
    }

    // CraftBukkit start - Sorter to load nearby chunks first
    private static class ChunkCoordComparator implements java.util.Comparator<ChunkCoordIntPair> {
        private final int x;
        private final int z;

        public ChunkCoordComparator(EntityPlayer entityplayer) {
            x = MathHelper.floor(entityplayer.locX) >> 4;
            z = MathHelper.floor(entityplayer.locZ) >> 4;
        }

        public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b) {
            if (a.equals(b)) {
                return 0;
            }

            // Subtract current position to set center point
            int ax = a.x - this.x;
            int az = a.z - this.z;
            int bx = b.x - this.x;
            int bz = b.z - this.z;

            int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));
            if (result != 0) {
                return result;
            }

            if (ax < 0) {
                if (bx < 0) {
                    return bz - az;
                } else {
                    return -1;
                }
            } else {
                if (bx < 0) {
                    return 1;
                } else {
                    return az - bz;
                }
            }
        }
    }

    class PlayerChunk {

        private final List<EntityPlayer> b = Lists.newArrayList();
        private final ChunkCoordIntPair location;
        // CraftBukkit start - add fields
        private final HashMap<EntityPlayer, Runnable> players = new HashMap<>();
        private final short[] dirtyBlocks = new short[64];
        private int dirtyCount;
        private int f;
        private long g;
        private boolean loaded = false;
        private final Runnable loadedRunnable = () -> loaded = true;
        // CraftBukkit end

        public void resend() {
            if (this.dirtyCount == 0)
                (PlayerChunkMap.this.a().getPlayerChunkMap()).e.add(this);
            this.dirtyCount = 64;
            this.f = 65535;
        }

        public PlayerChunk(int i, int j) {
            this.location = new ChunkCoordIntPair(i, j);
            PlayerChunkMap.this.a().chunkProviderServer.getChunkAt(i, j, loadedRunnable); // CraftBukkit
        }

        public void a(final EntityPlayer entityplayer) { // CraftBukkit - added final to argument
            if (this.b.contains(entityplayer)) {
                PlayerChunkMap.a.debug("Failed to add player. {} already is in chunk {}, {}", entityplayer, this.location.x, this.location.z);
            } else {
                if (this.b.isEmpty()) {
                    this.g = PlayerChunkMap.this.world.getTime();
                }

                this.b.add(entityplayer);
                // CraftBukkit start - use async chunk io
                Runnable playerRunnable = null;
                if (this.loaded) {
                    entityplayer.chunkCoordIntPairQueue.add(this.location);
                } else {
                    playerRunnable = () -> entityplayer.chunkCoordIntPairQueue.add(location);
                    PlayerChunkMap.this.a().chunkProviderServer.getChunkAt(this.location.x, this.location.z,
                            playerRunnable);
                }

                this.players.put(entityplayer, playerRunnable);
                // CraftBukkit end
            }
        }

        public void b(EntityPlayer entityplayer) {
            if (this.b.contains(entityplayer)) {
                // CraftBukkit start - If we haven't loaded yet don't load the chunk just so we
                // can clean it up
                if (!this.loaded) {
                    ChunkIOExecutor.dropQueuedChunkLoad(PlayerChunkMap.this.a(), this.location.x, this.location.z,
                            this.players.get(entityplayer));
                    this.b.remove(entityplayer);
                    this.players.remove(entityplayer);

                    if (this.b.isEmpty()) {
                        ChunkIOExecutor.dropQueuedChunkLoad(PlayerChunkMap.this.a(), this.location.x, this.location.z,
                                this.loadedRunnable);
                        PlayerChunkMap.this.d
                                .remove(this.location.x + 2147483647L | this.location.z + 2147483647L << 32);
                        // PlayerChunkMap.this.f.remove(this);
                    }

                    return;
                }
                // CraftBukkit end
                Chunk chunk = world.getChunkAt(this.location.x, this.location.z);

                if (chunk.isReady()) {
                    entityplayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 0)); // Vortex - Optimized chunk loading
                }

                this.players.remove(entityplayer); // CraftBukkit
                this.b.remove(entityplayer);
                entityplayer.chunkCoordIntPairQueue.remove(this.location);
                if (this.b.isEmpty()) {

                    this.a(chunk);
                    PlayerChunkMap.this.d.remove(location.x + 2147483647L | this.location.z + 2147483647L << 32);
                    // PlayerChunkMap.this.f.remove(this);
                    if (this.dirtyCount > 0) {
                        e.remove(this);
                    }

                    PlayerChunkMap.this.a().chunkProviderServer.queueUnload(this.location.x, this.location.z);
                }

            }
        }

        public void a() {
            this.a(world.getChunkAt(this.location.x, this.location.z));
        }

        private void a(Chunk chunk) {
            chunk.c(chunk.w() + world.getTime() - this.g);
            this.g = world.getTime();
        }

        public void a(int i, int j, int k) {
            if (this.dirtyCount == 0) {
                e.add(this);
            }

            this.f |= 1 << (j >> 4);
            if (this.dirtyCount < 64) {
                short short0 = (short) (i << 12 | k << 8 | j);

                for (int l = 0; l < this.dirtyCount; ++l) {
                    if (this.dirtyBlocks[l] == short0) {
                        return;
                    }
                }

                this.dirtyBlocks[this.dirtyCount++] = short0;
            }

        }

        public void a(Packet<?> packet) {
            for (EntityPlayer entityplayer : b) {
                if (!entityplayer.chunkCoordIntPairQueue.contains(this.location)) {
                    entityplayer.playerConnection.sendPacket(packet);
                }
            }
        }

        public void b() {
            if (this.dirtyCount != 0) {
                int i;
                int j;
                int k;

                if (this.dirtyCount == 1) {
                    i = (this.dirtyBlocks[0] >> 12 & 15) + this.location.x * 16;
                    j = this.dirtyBlocks[0] & 255;
                    k = (this.dirtyBlocks[0] >> 8 & 15) + this.location.z * 16;
                    // Vortex - Once block getting instead of 3
                    Chunk ch = world.getChunkAtWorldCoords(i, k);
                    IBlockData block = world.getType(ch, i, j, k, true);
                    BlockPosition pos = new BlockPosition(i, j, k);
                    this.a(new PacketPlayOutBlockChange(pos, block));
                    final Block bl = block.getBlock();
                    if (bl.isTileEntity()) {
                        this.a(PlayerChunkMap.this.world.getTileEntity(pos));
                    }
                } else {
                    int l;

                    if (this.dirtyCount == 64) {
                        i = this.location.x * 16;
                        j = this.location.z * 16;
                        a(new PacketPlayOutMapChunk(world.getChunkAt(this.location.x, this.location.z), false, this.f)); // invictusspigot - Optimized chunk loading
                        for (k = 0; k < 16; ++k) {
                            if ((this.f & 1 << k) != 0) {
                                l = k << 4;
                                List<TileEntity> list = world.getTileEntities(i, l, j, i + 16, l + 16, j + 16);

                                for (TileEntity tileEntity : list) {
                                    this.a(tileEntity);
                                }
                            }
                        }
                    } else {
                        this.a(new PacketPlayOutMultiBlockChange(this.dirtyCount, this.dirtyBlocks, PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z)));

                        for (i = 0; i < this.dirtyCount; ++i) {
                            j = (this.dirtyBlocks[i] >> 12 & 15) + this.location.x * 16;
                            k = this.dirtyBlocks[i] & 255;
                            l = (this.dirtyBlocks[i] >> 8 & 15) + this.location.z * 16;

                            Chunk ch = world.getChunkAtWorldCoords(i, k);
                            Block block = world.getType(ch, i, j, k, true).getBlock();
                            if (block.isTileEntity()) {
                                this.a(PlayerChunkMap.this.world.getTileEntity(new BlockPosition(j, k, l)));
                            }
                        }
                    }
                }

                this.dirtyCount = 0;
                this.f = 0;
            }
        }

        private void a(TileEntity tileentity) {
            if (tileentity != null) {
                Packet<?> packet = tileentity.getUpdatePacket();
                if (packet != null) {
                    this.a(packet);
                }
            }

        }
    }

    public void resend(int x, int z) {
        PlayerChunk chunk = a(x, z, false);
        if (chunk != null)
            chunk.resend();
    }
    // CraftBukkit end
}
