package net.minecraft.server;

import com.google.common.base.Objects;

public class BaseBlockPosition implements Comparable<BaseBlockPosition> {

    public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
    // PaperSpigot start - Make mutable and protected for MutableBlockPos
    protected int a;
    protected int c;
    protected int d;
    // PaperSpigot end

    public BaseBlockPosition(int i, int j, int k) {
        this.a = i;
        this.c = j;
        this.d = k;
    }

    public BaseBlockPosition(double d0, double d1, double d2) {
        this(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof BaseBlockPosition)) {
            return false;
        } else {
            BaseBlockPosition baseblockposition = (BaseBlockPosition) object;

            return a == baseblockposition.getX() && c == baseblockposition.getY() && d == baseblockposition.getZ();
        }
    }

    public int hashCode() {
        return (c + d * 31) * 31 + a;
    }

    public int g(BaseBlockPosition baseblockposition) {
        return c == baseblockposition.getY() ? (d == baseblockposition.getZ() ? a - baseblockposition.getX() : d - baseblockposition.getZ()) : c - baseblockposition.getY();
    }

    public void setX(int x) {
        this.a = x;
    }

    public void setY(int y) {
        this.c = y;
    }

    public void setZ(int z) {
        this.d = z;
    }

    // PaperSpigot start - Only allow one implementation of these methods (make final)
    public int getX() {
        return this.a;
    }

    public int getY() {
        return this.c;
    }

    public int getZ() {
        return this.d;
    }
    // PaperSpigot end

    public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
        return new BaseBlockPosition(c * baseblockposition.getZ() - d * baseblockposition.getY(), d * baseblockposition.getX() - a * baseblockposition.getZ(), a * baseblockposition.getY() - c * baseblockposition.getX());
    }

    public double c(double d0, double d1, double d2) {
        double d3 = a - d0;
        double d4 = c - d1;
        double d5 = d - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double d(double d0, double d1, double d2) {
        double d3 = a + 0.5D - d0;
        double d4 = c + 0.5D - d1;
        double d5 = d + 0.5D - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double i(BaseBlockPosition baseblockposition) {
        return this.c(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }
    
    public double i(int x, int y, int z) {
        return this.c(x, y, z);
    }

    public String toString() {
        return Objects.toStringHelper(this).add("x", a).add("y", c).add("z", d).toString();
    }

    // Paperspigot - Signature change, Object -> BaseBlockPosition
    public int compareTo(BaseBlockPosition object) {
        return this.g(object);
    }
}
