package net.minecraft.server;

import java.util.Objects;

public class LongHashMap<V> {

    private transient LongHashMapEntry<V>[] entries = new LongHashMapEntry[4096];
    private transient int count;
    private int c;
    private int d = 3072;

    public LongHashMap() {
        this.c = this.entries.length - 1;
    }

    private static int g(long i) {
        return a((int) (i ^ i >>> 32));
    }

    private static int a(int i) {
        i ^= i >>> 20 ^ i >>> 12;
        return i ^ i >>> 7 ^ i >>> 4;
    }

    private static int a(int i, int j) {
        return i & j;
    }

    public int count() {
        return this.count;
    }

    public V getEntry(long i) {
        int j = g(i);

        for (LongHashMapEntry<V> longhashmap_longhashmapentry = this.entries[a(j, this.c)]; longhashmap_longhashmapentry != null; longhashmap_longhashmapentry = longhashmap_longhashmapentry.c) {
            if (longhashmap_longhashmapentry.a == i) {
                return longhashmap_longhashmapentry.b;
            }
        }

        return null;
    }

    public boolean contains(long i) {
        return this.c(i) != null;
    }

    final LongHashMapEntry<V> c(long i) {
        int j = g(i);

        for (LongHashMapEntry<V> longhashmap_longhashmapentry = this.entries[a(j, this.c)]; longhashmap_longhashmapentry != null; longhashmap_longhashmapentry = longhashmap_longhashmapentry.c) {
            if (longhashmap_longhashmapentry.a == i) {
                return longhashmap_longhashmapentry;
            }
        }

        return null;
    }

    public void put(long i, V v0) {
        int j = g(i);
        int k = a(j, this.c);

        for (LongHashMapEntry<V> longhashmap_longhashmapentry = this.entries[k]; longhashmap_longhashmapentry != null; longhashmap_longhashmapentry = longhashmap_longhashmapentry.c) {
            if (longhashmap_longhashmapentry.a == i) {
                longhashmap_longhashmapentry.b = v0;
                return;
            }
        }

        this.a(j, i, v0, k);
    }

    private void b(int i) {
        LongHashMapEntry<V>[] alonghashmap_longhashmapentry = this.entries;
        int j = alonghashmap_longhashmapentry.length;

        if (j == 1073741824) {
            this.d = Integer.MAX_VALUE;
        } else {
            LongHashMapEntry<V>[] alonghashmap_longhashmapentry1 = new LongHashMapEntry[i];

            this.a(alonghashmap_longhashmapentry1);
            this.entries = alonghashmap_longhashmapentry1;
            this.c = this.entries.length - 1;
            this.d = (int) ((float) i * 0.75F);
        }
    }

    private void a(LongHashMapEntry<V>[] alonghashmap_longhashmapentry) {
        LongHashMapEntry<V>[] alonghashmap_longhashmapentry1 = this.entries;
        int i = alonghashmap_longhashmapentry.length;

        for (int j = 0; j < alonghashmap_longhashmapentry1.length; ++j) {
            LongHashMapEntry<V> longhashmap_longhashmapentry = alonghashmap_longhashmapentry1[j];

            if (longhashmap_longhashmapentry != null) {
                alonghashmap_longhashmapentry1[j] = null;

                LongHashMapEntry<V> longhashmap_longhashmapentry1;

                do {
                    longhashmap_longhashmapentry1 = longhashmap_longhashmapentry.c;
                    int k = a(longhashmap_longhashmapentry.d, i - 1);

                    longhashmap_longhashmapentry.c = alonghashmap_longhashmapentry[k];
                    alonghashmap_longhashmapentry[k] = longhashmap_longhashmapentry;
                    longhashmap_longhashmapentry = longhashmap_longhashmapentry1;
                } while (longhashmap_longhashmapentry1 != null);
            }
        }

    }

    public V remove(long i) {
        LongHashMapEntry<V> longhashmap_longhashmapentry = this.e(i);

        return longhashmap_longhashmapentry == null ? null : longhashmap_longhashmapentry.b;
    }

    final LongHashMapEntry<V> e(long i) {
        int j = g(i);
        int k = a(j, this.c);
        LongHashMapEntry<V> longhashmap_longhashmapentry = this.entries[k];

        LongHashMapEntry<V> longhashmap_longhashmapentry1;
        LongHashMapEntry<V> longhashmap_longhashmapentry2;

        for (longhashmap_longhashmapentry1 = longhashmap_longhashmapentry; longhashmap_longhashmapentry1 != null; longhashmap_longhashmapentry1 = longhashmap_longhashmapentry2) {
            longhashmap_longhashmapentry2 = longhashmap_longhashmapentry1.c;
            if (longhashmap_longhashmapentry1.a == i) {
                --this.count;
                if (longhashmap_longhashmapentry == longhashmap_longhashmapentry1) {
                    this.entries[k] = longhashmap_longhashmapentry2;
                } else {
                    longhashmap_longhashmapentry.c = longhashmap_longhashmapentry2;
                }

                return longhashmap_longhashmapentry1;
            }

            longhashmap_longhashmapentry = longhashmap_longhashmapentry1;
        }

        return null;
    }

    private void a(int i, long j, V v0, int k) {
        LongHashMapEntry<V> longhashmap_longhashmapentry = this.entries[k];

        this.entries[k] = new LongHashMapEntry(i, j, v0, longhashmap_longhashmapentry);
        if (this.count++ >= this.d) {
            this.b(2 * this.entries.length);
        }

    }

    static class LongHashMapEntry<V> {

        final long a;
        V b;
        LongHashMapEntry<V> c;
        final int d;

        LongHashMapEntry(int i, long j, V v0, LongHashMapEntry<V> longhashmap_longhashmapentry) {
            this.b = v0;
            this.c = longhashmap_longhashmapentry;
            this.a = j;
            this.d = i;
        }

        public final long a() {
            return this.a;
        }

        public final V b() {
            return this.b;
        }

        public final boolean equals(Object object) {
            if (object instanceof LongHashMap.LongHashMapEntry) {
                LongHashMapEntry longhashmap_longhashmapentry = (LongHashMapEntry) object;
                if (Objects.equals(a(), longhashmap_longhashmapentry.a())) {
                    return Objects.equals(b(), longhashmap_longhashmapentry.b());
                }

            }
            return false;
        }

        public final int hashCode() {
            return LongHashMap.g(this.a);
        }

        public final String toString() {
            return this.a() + "=" + this.b();
        }
    }
}
