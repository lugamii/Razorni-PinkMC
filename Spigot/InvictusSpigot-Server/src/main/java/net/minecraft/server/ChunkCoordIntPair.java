package net.minecraft.server;

public class ChunkCoordIntPair {
    public final int x;
    public final int z;

    public ChunkCoordIntPair(int var1, int var2) {
        this.x = var1;
        this.z = var2;
    }

    public static long a(int var0, int var1) {
        return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
    }

    public int hashCode() {
        int var1 = 1664525 * this.x + 1013904223;
        int var2 = 1664525 * (this.z ^ -559038737) + 1013904223;
        return var1 ^ var2;
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof ChunkCoordIntPair)) {
            return false;
        } else {
            ChunkCoordIntPair var2 = (ChunkCoordIntPair)var1;
            return this.x == var2.x && this.z == var2.z;
        }
    }

    public int a() {
        return (this.x << 4) + 8;
    }

    public int b() {
        return (this.z << 4) + 8;
    }

    public int c() {
        return this.x << 4;
    }

    public int d() {
        return this.z << 4;
    }

    public int e() {
        return (this.x << 4) + 15;
    }

    public int f() {
        return (this.z << 4) + 15;
    }

    public BlockPosition a(int var1, int var2, int var3) {
        return new BlockPosition((this.x << 4) + var1, var2, (this.z << 4) + var3);
    }

    public BlockPosition a(int var1) {
        return new BlockPosition(this.a(), var1, this.b());
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public boolean isEmpty() {
        return false;
    }

}
