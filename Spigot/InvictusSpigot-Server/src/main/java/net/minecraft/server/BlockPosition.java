package net.minecraft.server;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

public class BlockPosition extends BaseBlockPosition {
    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);

    private static final int c = 1 + MathHelper.c(MathHelper.b(30000000));

    private static final int d = c;

    private static final int e = 64 - c - d;

    private static final int f = d;

    private static final int g = f + e;

    private static final long h = (1L << c) - 1L;

    private static final long i = (1L << e) - 1L;

    private static final long j = (1L << d) - 1L;

    public BlockPosition(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPosition(double d0, double d1, double d2) {
        super(d0, d1, d2);
    }

    public BlockPosition(Entity entity) {
        this(entity.locX, entity.locY, entity.locZ);
    }

    public BlockPosition(Vec3D vec3d) {
        this(vec3d.a, vec3d.b, vec3d.c);
    }

    public BlockPosition(BaseBlockPosition baseblockposition) {
        this(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public BlockPosition a(double d0, double d1, double d2) {
        return (d0 == 0.0D && d1 == 0.0D && d2 == 0.0D) ? this : new BlockPosition(getX() + d0, getY() + d1, getZ() + d2);
    }

    public BlockPosition a(int i, int j, int k) {
        return (i == 0 && j == 0 && k == 0) ? this : new BlockPosition(getX() + i, getY() + j, getZ() + k);
    }

    public BlockPosition a(BaseBlockPosition baseblockposition) {
        return (baseblockposition.getX() == 0 && baseblockposition.getY() == 0 && baseblockposition.getZ() == 0) ? this : new BlockPosition(getX() + baseblockposition.getX(), getY() + baseblockposition.getY(), getZ() + baseblockposition.getZ());
    }

    public BlockPosition b(BaseBlockPosition baseblockposition) {
        return (baseblockposition.getX() == 0 && baseblockposition.getY() == 0 && baseblockposition.getZ() == 0) ? this : new BlockPosition(getX() - baseblockposition.getX(), getY() - baseblockposition.getY(), getZ() - baseblockposition.getZ());
    }

    public BlockPosition up() {
        return up(1);
    }

    public BlockPosition up(int i) {
        return shift(EnumDirection.UP, i);
    }

    public BlockPosition down() {
        return down(1);
    }

    public BlockPosition down(int i) {
        return shift(EnumDirection.DOWN, i);
    }

    public BlockPosition north() {
        return north(1);
    }

    public BlockPosition north(int i) {
        return shift(EnumDirection.NORTH, i);
    }

    public BlockPosition south() {
        return south(1);
    }

    public BlockPosition south(int i) {
        return shift(EnumDirection.SOUTH, i);
    }

    public BlockPosition west() {
        return west(1);
    }

    public BlockPosition west(int i) {
        return shift(EnumDirection.WEST, i);
    }

    public BlockPosition east() {
        return east(1);
    }

    public BlockPosition east(int i) {
        return shift(EnumDirection.EAST, i);
    }

    public BlockPosition shift(EnumDirection enumdirection) {
        return shift(enumdirection, 1);
    }

    public BlockPosition shift(EnumDirection enumdirection, int i) {
        return (i == 0) ? this : new BlockPosition(getX() + enumdirection.getAdjacentX() * i, getY() + enumdirection.getAdjacentY() * i, getZ() + enumdirection.getAdjacentZ() * i);
    }

    public BlockPosition c(BaseBlockPosition baseblockposition) {
        return new BlockPosition(getY() * baseblockposition.getZ() - getZ() * baseblockposition.getY(), getZ() * baseblockposition.getX() - getX() * baseblockposition.getZ(), getX() * baseblockposition.getY() - getY() * baseblockposition.getX());
    }

    public long asLong() {
        return (getX() & h) << g | (getY() & i) << f | (getZ() & j);
    }

    public static long asLong(int x, int y, int z) {
        return (x & h) << g | (y & i) << f | (z & j);
    }

    public static BlockPosition fromLong(long i) {
        return new BlockPosition((int)(i << 64 - g - c >> 64 - c), (int)(i << 64 - f - e >> 64 - e), (int)(i << 64 - d >> 64 - d));
    }

    public static Iterable<BlockPosition> a(final BlockPosition blockposition, final BlockPosition blockposition1) {
        return new Iterable<BlockPosition>() {
            public Iterator<BlockPosition> iterator() {
                return new AbstractIterator<BlockPosition>() {
                    private BlockPosition b = null;

                    protected BlockPosition computeNext() {
                        if (this.b == null) {
                            this.b = blockposition;
                            return this.b;
                        }
                        if (this.b.equals(blockposition1))
                            return endOfData();
                        int i = this.b.getX();
                        int j = this.b.getY();
                        int k = this.b.getZ();
                        if (i < blockposition1.getX()) {
                            i++;
                        } else if (j < blockposition1.getY()) {
                            i = blockposition.getX();
                            j++;
                        } else if (k < blockposition1.getZ()) {
                            i = blockposition.getX();
                            j = blockposition.getY();
                            k++;
                        }
                        this.b = new BlockPosition(i, j, k);
                        return this.b;
                    }
                };
            }
        };
    }

    public static Iterable<MutableBlockPosition> b(final BlockPosition blockposition, final BlockPosition blockposition1) {
        return new Iterable<MutableBlockPosition>() {
            public Iterator<BlockPosition.MutableBlockPosition> iterator() {
                return new AbstractIterator<MutableBlockPosition>() {
                    private MutableBlockPosition b = null;

                    protected MutableBlockPosition computeNext() {
                        if (this.b == null) {
                            this.b = new MutableBlockPosition(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                            return this.b;
                        }
                        if (this.b.equals(blockposition1))
                            return endOfData();
                        int i = this.b.getX();
                        int j = this.b.getY();
                        int k = this.b.getZ();
                        if (i < blockposition1.getX()) {
                            i++;
                        } else if (j < blockposition1.getY()) {
                            i = blockposition.getX();
                            j++;
                        } else if (k < blockposition1.getZ()) {
                            i = blockposition.getX();
                            j = blockposition.getY();
                            k++;
                        }
                        this.b.setX(i);
                        this.b.setY(j);
                        this.b.setZ(k);
                        return this.b;
                    }
                };
            }
        };
    }

    public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
        return c(baseblockposition);
    }

    public static final class MutableBlockPosition extends BlockPosition {
        public void setX(int x) {
            this.a = x;
        }

        public void setY(int y) {
            ((BaseBlockPosition) this).c = y;
        }

        public void setZ(int z) {
            ((BaseBlockPosition) this).d = z;
        }

        public MutableBlockPosition() {
            this(0, 0, 0);
        }

        public MutableBlockPosition(int i, int j, int k) {
            super(0, 0, 0);
            setX(i);
            setY(j);
            setZ(k);
        }

        public MutableBlockPosition setValues(int x, int y, int z) {
            return c(x, y, z);
        }

        public MutableBlockPosition c(int i, int j, int k) {
            setX(i);
            setY(j);
            setZ(k);
            return this;
        }

        public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
            return c(baseblockposition);
        }
    }
}
