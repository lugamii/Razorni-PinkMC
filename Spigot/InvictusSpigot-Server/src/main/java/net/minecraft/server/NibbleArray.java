package net.minecraft.server;

public class NibbleArray {

    private final byte[] a;

    public NibbleArray() {
        this.a = new byte[2048];
    }

    public NibbleArray(byte[] abyte) {
        this.a = abyte;
        if (abyte.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + abyte.length);
        }
    }

    public int a(int i, int j, int k) {
        return this.a(j << 8 | k << 4 | i);
    }

    public void a(int i, int j, int k, int l) {
        this.a(j << 8 | k << 4 | i, l);
    }

    public int a(int i) {
        int j = i >> 1;

        return ((i & 1) == 0) ? this.a[j] & 15 : this.a[j] >> 4 & 15;
    }

    public void a(int i, int j) {
        int k = i >> 1;

        if ((i & 1) == 0) {
            this.a[k] = (byte) (this.a[k] & 240 | j & 15);
        } else {
            this.a[k] = (byte) (this.a[k] & 15 | (j & 15) << 4);
        }
    }

    public byte[] a() {
        return this.a;
    }

    public NibbleArray clone() {
        return new NibbleArray(this.a.clone());
    }
}
