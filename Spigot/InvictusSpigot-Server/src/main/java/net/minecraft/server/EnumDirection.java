package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum EnumDirection implements INamable {

    DOWN(0, 1, -1, "down", EnumAxisDirection.NEGATIVE, EnumAxis.Y, new BaseBlockPosition(0, -1, 0)), UP(1, 0, -1, "up", EnumAxisDirection.POSITIVE, EnumAxis.Y, new BaseBlockPosition(0, 1, 0)), NORTH(2, 3, 2, "north", EnumAxisDirection.NEGATIVE, EnumAxis.Z, new BaseBlockPosition(0, 0, -1)), SOUTH(3, 2, 0, "south", EnumAxisDirection.POSITIVE, EnumAxis.Z, new BaseBlockPosition(0, 0, 1)), WEST(4, 5, 1, "west", EnumAxisDirection.NEGATIVE, EnumAxis.X, new BaseBlockPosition(-1, 0, 0)), EAST(5, 4, 3, "east", EnumAxisDirection.POSITIVE, EnumAxis.X, new BaseBlockPosition(1, 0, 0));

    private final int g;
    private final int h;
    private final int i;
    private final String j;
    private final EnumAxis k;
    private final EnumAxisDirection l;
    private final BaseBlockPosition m;
    private static final EnumDirection[] n = new EnumDirection[6];
    private static final EnumDirection[] o = new EnumDirection[4];

    EnumDirection(int i, int j, int k, String s, EnumAxisDirection enumdirection_enumaxisdirection, EnumAxis enumdirection_enumaxis, BaseBlockPosition baseblockposition) {
        this.g = i;
        this.i = k;
        this.h = j;
        this.j = s;
        this.k = enumdirection_enumaxis;
        this.l = enumdirection_enumaxisdirection;
        this.m = baseblockposition;
    }

    public int a() {
        return this.g;
    }

    public int b() {
        return this.i;
    }

    public EnumAxisDirection c() {
        return this.l;
    }

    public EnumDirection opposite() {
        return fromType1(this.h);
    }

    public EnumDirection e() {
        switch (SyntheticClass_1.b[this.ordinal()]) {
            case 1:
                return EnumDirection.EAST;

            case 2:
                return EnumDirection.SOUTH;

            case 3:
                return EnumDirection.WEST;

            case 4:
                return EnumDirection.NORTH;

            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    public EnumDirection f() {
        switch (SyntheticClass_1.b[this.ordinal()]) {
            case 1:
                return EnumDirection.WEST;

            case 2:
                return EnumDirection.NORTH;

            case 3:
                return EnumDirection.EAST;

            case 4:
                return EnumDirection.SOUTH;

            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public int getAdjacentX() {
        return this.k == EnumAxis.X ? this.l.a() : 0;
    }

    public int getAdjacentY() {
        return this.k == EnumAxis.Y ? this.l.a() : 0;
    }

    public int getAdjacentZ() {
        return this.k == EnumAxis.Z ? this.l.a() : 0;
    }

    public String j() {
        return this.j;
    }

    public EnumAxis k() {
        return this.k;
    }

    public static EnumDirection fromType1(int i) {
        return EnumDirection.n[MathHelper.a(i % EnumDirection.n.length)];
    }

    public static EnumDirection fromType2(int i) {
        return EnumDirection.o[MathHelper.a(i % EnumDirection.o.length)];
    }

    public static EnumDirection fromAngle(double d0) {
        return fromType2(MathHelper.floor(d0 / 90.0D + 0.5D) & 3);
    }

    public static EnumDirection a(Random random) {
        return values()[random.nextInt(values().length)];
    }

    public String toString() {
        return this.j;
    }

    public String getName() {
        return this.j;
    }

    public static EnumDirection a(EnumAxisDirection enumdirection_enumaxisdirection, EnumAxis enumdirection_enumaxis) {
        for (EnumDirection enumdirection : values()) {
            if (enumdirection.c() == enumdirection_enumaxisdirection && enumdirection.k() == enumdirection_enumaxis) {
                return enumdirection;
            }
        }

        throw new IllegalArgumentException("No such direction: " + enumdirection_enumaxisdirection + " " + enumdirection_enumaxis);
    }

    static {
        for (EnumDirection enumdirection : values()) {
            EnumDirection.n[enumdirection.g] = enumdirection;
            if (enumdirection.k().c()) {
                EnumDirection.o[enumdirection.i] = enumdirection;
            }
        }

    }

    static class SyntheticClass_1 {

        static final int[] a;
        static final int[] b;
        static final int[] c = new int[EnumDirectionLimit.values().length];

        static {
            try {
                SyntheticClass_1.c[EnumDirectionLimit.HORIZONTAL.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                SyntheticClass_1.c[EnumDirectionLimit.VERTICAL.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            b = new int[EnumDirection.values().length];

            try {
                SyntheticClass_1.b[EnumDirection.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                SyntheticClass_1.b[EnumDirection.EAST.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            try {
                SyntheticClass_1.b[EnumDirection.SOUTH.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

            try {
                SyntheticClass_1.b[EnumDirection.WEST.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror5) {
                ;
            }

            try {
                SyntheticClass_1.b[EnumDirection.UP.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror6) {
                ;
            }

            try {
                SyntheticClass_1.b[EnumDirection.DOWN.ordinal()] = 6;
            } catch (NoSuchFieldError nosuchfielderror7) {
                ;
            }

            a = new int[EnumAxis.values().length];

            try {
                SyntheticClass_1.a[EnumAxis.X.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror8) {
                ;
            }

            try {
                SyntheticClass_1.a[EnumAxis.Y.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror9) {
                ;
            }

            try {
                SyntheticClass_1.a[EnumAxis.Z.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror10) {
                ;
            }

        }
    }

    public BaseBlockPosition getBaseBlockPosition()
    {
        return m;
    }

    public static enum EnumDirectionLimit implements Predicate<EnumDirection>, Iterable<EnumDirection> {

        HORIZONTAL, VERTICAL;

        EnumDirectionLimit() {}

        public EnumDirection[] a() {
            switch (SyntheticClass_1.c[this.ordinal()]) {
                case 1:
                    return new EnumDirection[] { EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};

                case 2:
                    return new EnumDirection[] { EnumDirection.UP, EnumDirection.DOWN};

                default:
                    throw new Error("Someone's been tampering with the universe!");
            }
        }

        public EnumDirection a(Random random) {
            EnumDirection[] aenumdirection = this.a();

            return aenumdirection[random.nextInt(aenumdirection.length)];
        }

        public boolean a(EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k().d() == this;
        }

        public Iterator<EnumDirection> iterator() {
            return Iterators.forArray(this.a());
        }

        public boolean apply(EnumDirection object) {
            return this.a(object);
        }
    }

    public static enum EnumAxisDirection {

        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        private final int c;
        private final String d;

        EnumAxisDirection(int i, String s) {
            this.c = i;
            this.d = s;
        }

        public int a() {
            return this.c;
        }

        public String toString() {
            return this.d;
        }
    }

    public static enum EnumAxis implements Predicate<EnumDirection>, INamable {

        X("x", EnumDirectionLimit.HORIZONTAL), Y("y", EnumDirectionLimit.VERTICAL), Z("z", EnumDirectionLimit.HORIZONTAL);

        private static final Map<String, EnumAxis> d = Maps.newHashMap();
        private final String e;
        private final EnumDirectionLimit f;

        EnumAxis(String s, EnumDirectionLimit enumdirection_enumdirectionlimit) {
            this.e = s;
            this.f = enumdirection_enumdirectionlimit;
        }

        public String a() {
            return this.e;
        }

        public boolean b() {
            return this.f == EnumDirectionLimit.VERTICAL;
        }

        public boolean c() {
            return this.f == EnumDirectionLimit.HORIZONTAL;
        }

        public String toString() {
            return this.e;
        }

        public boolean a(EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k() == this;
        }

        public EnumDirectionLimit d() {
            return this.f;
        }

        public String getName() {
            return this.e;
        }

        public boolean apply(EnumDirection object) {
            return this.a(object);
        }

        static {
            for (EnumAxis enumdirection_enumaxis : values()) {
                EnumAxis.d.put(enumdirection_enumaxis.a().toLowerCase(), enumdirection_enumaxis);
            }

        }
    }
}