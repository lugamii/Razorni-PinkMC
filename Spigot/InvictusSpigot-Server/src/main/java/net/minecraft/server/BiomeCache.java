package net.minecraft.server;

import com.google.common.collect.Lists;
import org.bukkit.craftbukkit.util.LongHash;

import java.util.List;

public class BiomeCache {

    private final WorldChunkManager a;
    private long b;
    private final LongHashMap<BiomeCacheBlock> c = new LongHashMap<>();
    private final List<BiomeCacheBlock> d = Lists.newArrayList();

    public BiomeCache(WorldChunkManager worldchunkmanager) {
        this.a = worldchunkmanager;
    }

    public BiomeCacheBlock a(int i, int j) {
        i >>= 4;
        j >>= 4;
        long k = LongHash.toLong(i, j);
        BiomeCacheBlock biomecache_biomecacheblock = this.c.getEntry(k);

        if (biomecache_biomecacheblock == null) {
            biomecache_biomecacheblock = new BiomeCacheBlock(i, j);
            this.c.put(k, biomecache_biomecacheblock);
            this.d.add(biomecache_biomecacheblock);
        }

        biomecache_biomecacheblock.e = MinecraftServer.az();
        return biomecache_biomecacheblock;
    }

    public BiomeBase a(int i, int j, BiomeBase biomebase) {
        BiomeBase biomebase1 = this.a(i, j).a(i, j);

        return biomebase1 == null ? biomebase : biomebase1;
    }

    public void a() {
        long i = MinecraftServer.az();
        long j = i - this.b;

        if (j > 7500L || j < 0L) {
            this.b = i;

            for (int k = 0; k < this.d.size(); ++k) {
                BiomeCacheBlock biomecache_biomecacheblock = this.d.get(k);
                long l = i - biomecache_biomecacheblock.e;

                if (l > 30000L || l < 0L) {
                    this.d.remove(k--);
                    this.c.remove(LongHash.toLong(biomecache_biomecacheblock.c, biomecache_biomecacheblock.d));
                }
            }
        }

    }

    public BiomeBase[] c(int i, int j) {
        return this.a(i, j).b;
    }

    public class BiomeCacheBlock {

        public float[] a = new float[256];
        public BiomeBase[] b = new BiomeBase[256];
        public int c;
        public int d;
        public long e;

        public BiomeCacheBlock(int i, int j) {
            this.c = i;
            this.d = j;
            BiomeCache.this.a.getWetness(this.a, i << 4, j << 4, 16, 16);
            BiomeCache.this.a.a(this.b, i << 4, j << 4, 16, 16, false);
        }

        public BiomeBase a(int i, int j) {
            return this.b[i & 15 | (j & 15) << 4];
        }
    }
}
