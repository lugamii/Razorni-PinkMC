package net.minecraft.server;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class BlockMinecartTrackAbstract extends Block {

    protected final boolean a;

    protected BlockMinecartTrackAbstract(boolean flag) {
        super(Material.ORIENTABLE);
        this.a = flag;
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.a(CreativeModeTab.e);
    }

    public static boolean e(World world, BlockPosition blockposition) {
        return d(world.getType(blockposition));
    }

    public static boolean e(World world, int x, int y, int z) {
        return d(world.getType(x, y, z));
    }

    public static boolean d(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        return block == Blocks.RAIL || block == Blocks.GOLDEN_RAIL || block == Blocks.DETECTOR_RAIL
                || block == Blocks.ACTIVATOR_RAIL;
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public boolean c() {
        return false;
    }

    public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, vec3d, vec3d1);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata.getBlock() == this ? iblockdata.get(this.n()) : null;

        if (blockminecarttrackabstract_enumtrackposition != null && blockminecarttrackabstract_enumtrackposition.c()) {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        } else {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }

    }

    public boolean d() {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata = this.a(world, blockposition, iblockdata, true);
        if (this.a) {
            this.doPhysics(world, blockposition, iblockdata, this);
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata.get(this.n());
        boolean flag = !World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());

        if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.ASCENDING_EAST
                && !World.a(world, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ())) {
            flag = true;
        } else if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.ASCENDING_WEST
                && !World.a(world, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ())) {
            flag = true;
        } else if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.ASCENDING_NORTH
                && !World.a(world, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1)) {
            flag = true;
        } else if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.ASCENDING_SOUTH
                && !World.a(world, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1)) {
            flag = true;
        }

        if (flag && !world.isEmpty(blockposition)) { // CraftBukkit - SPIGOT-424, MC-73474
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        } else {
            this.b(world, blockposition, iblockdata, block);
        }

    }

    protected void b(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
    }

    protected IBlockData a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return new MinecartTrackLogic(world, blockposition, iblockdata).a(world.isBlockIndirectlyPowered(blockposition), flag).b();
    }

    public int k() {
        return 0;
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        if (iblockdata.get(this.n()).c()) {
            world.applyPhysics(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ(), this);
        }

        if (this.a) {
            world.applyPhysics(blockposition.getX(), blockposition.getY(), blockposition.getZ(), this);
            world.applyPhysics(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ(), this);
        }

    }

    public abstract IBlockState<EnumTrackPosition> n();

    public static enum EnumTrackPosition implements INamable {

        NORTH_SOUTH(0, "north_south"), EAST_WEST(1, "east_west"), ASCENDING_EAST(2, "ascending_east"),
        ASCENDING_WEST(3, "ascending_west"), ASCENDING_NORTH(4, "ascending_north"),
        ASCENDING_SOUTH(5, "ascending_south"), SOUTH_EAST(6, "south_east"), SOUTH_WEST(7, "south_west"),
        NORTH_WEST(8, "north_west"), NORTH_EAST(9, "north_east");

        private static final EnumTrackPosition[] k = new EnumTrackPosition[values().length];

        static {
            for (EnumTrackPosition blockminecarttrackabstract_enumtrackposition : values()) {
                EnumTrackPosition.k[blockminecarttrackabstract_enumtrackposition.a()] = blockminecarttrackabstract_enumtrackposition;
            }

        }

        private final int l;
        private final String m;

        private EnumTrackPosition(int i, String s) {
            this.l = i;
            this.m = s;
        }

        public static EnumTrackPosition a(int i) {
            if (i < 0 || i >= EnumTrackPosition.k.length) {
                i = 0;
            }

            return EnumTrackPosition.k[i];
        }

        public int a() {
            return this.l;
        }

        public String toString() {
            return this.m;
        }

        public boolean c() {
            return this == EnumTrackPosition.ASCENDING_NORTH
                    || this == EnumTrackPosition.ASCENDING_EAST
                    || this == EnumTrackPosition.ASCENDING_SOUTH
                    || this == EnumTrackPosition.ASCENDING_WEST;
        }

        public String getName() {
            return this.m;
        }
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumTrackPosition.values().length];

        static {
            try {
                SyntheticClass_1.a[EnumTrackPosition.NORTH_SOUTH.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.EAST_WEST.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.ASCENDING_EAST.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.ASCENDING_WEST.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.ASCENDING_NORTH.ordinal()] = 5;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.ASCENDING_SOUTH.ordinal()] = 6;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.SOUTH_EAST.ordinal()] = 7;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.SOUTH_WEST.ordinal()] = 8;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.NORTH_WEST.ordinal()] = 9;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumTrackPosition.NORTH_EAST.ordinal()] = 10;
            } catch (NoSuchFieldError ignored) {
            }

        }
    }

    public class MinecartTrackLogic {

        private final World b;
        private final BlockPosition c;
        private final BlockMinecartTrackAbstract d;
        private final boolean f;
        private final List<BlockPosition> g = Lists.newArrayList();
        private IBlockData e;

        public MinecartTrackLogic(World world, BlockPosition blockposition, IBlockData iblockdata) {
            this.b = world;
            this.c = blockposition;
            this.e = iblockdata;
            this.d = (BlockMinecartTrackAbstract) iblockdata.getBlock();
            EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata
                    .get(BlockMinecartTrackAbstract.this.n());

            this.f = this.d.a;
            this.a(blockminecarttrackabstract_enumtrackposition);
        }

        private void a(EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
            this.g.clear();
            switch (SyntheticClass_1.a[blockminecarttrackabstract_enumtrackposition
                    .ordinal()]) {
                case 1:
                    this.g.add(this.c.north());
                    this.g.add(this.c.south());
                    break;

                case 2:
                    this.g.add(this.c.west());
                    this.g.add(this.c.east());
                    break;

                case 3:
                    this.g.add(this.c.west());
                    this.g.add(this.c.east().up());
                    break;

                case 4:
                    this.g.add(this.c.west().up());
                    this.g.add(this.c.east());
                    break;

                case 5:
                    this.g.add(this.c.north().up());
                    this.g.add(this.c.south());
                    break;

                case 6:
                    this.g.add(this.c.north());
                    this.g.add(this.c.south().up());
                    break;

                case 7:
                    this.g.add(this.c.east());
                    this.g.add(this.c.south());
                    break;

                case 8:
                    this.g.add(this.c.west());
                    this.g.add(this.c.south());
                    break;

                case 9:
                    this.g.add(this.c.west());
                    this.g.add(this.c.north());
                    break;

                case 10:
                    this.g.add(this.c.east());
                    this.g.add(this.c.north());
            }

        }

        private void c() {
            for (int i = 0; i < this.g.size(); ++i) {
                MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic = this.b(this.g.get(i));

                if (blockminecarttrackabstract_minecarttracklogic != null
                        && blockminecarttrackabstract_minecarttracklogic.a(this)) {
                    this.g.set(i, blockminecarttrackabstract_minecarttracklogic.c);
                } else {
                    this.g.remove(i--);
                }
            }

        }

        private boolean a(BlockPosition blockposition) {
            return BlockMinecartTrackAbstract.e(this.b, blockposition)
                    || BlockMinecartTrackAbstract.e(this.b, blockposition.getX(), blockposition.getY() + 1, blockposition.getZ())
                    || BlockMinecartTrackAbstract.e(this.b, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
        }

        private MinecartTrackLogic b(BlockPosition blockposition) {
            IBlockData iblockdata = this.b.getType(blockposition);

            if (BlockMinecartTrackAbstract.d(iblockdata)) {
                return BlockMinecartTrackAbstract.this.new MinecartTrackLogic(this.b, blockposition, iblockdata);
            } else {
                BlockPosition blockposition1 = blockposition.up();

                iblockdata = this.b.getType(blockposition1);
                if (BlockMinecartTrackAbstract.d(iblockdata)) {
                    return BlockMinecartTrackAbstract.this.new MinecartTrackLogic(this.b, blockposition1, iblockdata);
                } else {
                    blockposition1 = blockposition.down();
                    iblockdata = this.b.getType(blockposition1);
                    return BlockMinecartTrackAbstract.d(iblockdata)
                            ? BlockMinecartTrackAbstract.this.new MinecartTrackLogic(this.b, blockposition1, iblockdata)
                            : null;
                }
            }
        }

        private boolean a(MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic) {
            return this.c(blockminecarttrackabstract_minecarttracklogic.c);
        }

        private boolean c(BlockPosition blockposition) {
            for (BlockPosition blockposition1 : this.g) {
                if (blockposition1.getX() == blockposition.getX() && blockposition1.getZ() == blockposition.getZ()) {
                    return true;
                }
            }
            return false;
        }

        protected int a() {
            int i = 0;
            for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                if (this.a(this.c.shift(enumdirection))) {
                    ++i;
                }
            }
            return i;
        }

        private boolean b(MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic) {
            return this.a(blockminecarttrackabstract_minecarttracklogic) || this.g.size() != 2;
        }

        private void c(MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic) {
            this.g.add(blockminecarttrackabstract_minecarttracklogic.c);
            BlockPosition blockposition = this.c.north();
            BlockPosition blockposition1 = this.c.south();
            BlockPosition blockposition2 = this.c.west();
            BlockPosition blockposition3 = this.c.east();
            boolean flag = this.c(blockposition);
            boolean flag1 = this.c(blockposition1);
            boolean flag2 = this.c(blockposition2);
            boolean flag3 = this.c(blockposition3);
            EnumTrackPosition blockminecarttrackabstract_enumtrackposition = null;

            if (flag || flag1) {
                blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_SOUTH;
            }

            if (flag2 || flag3) {
                blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.EAST_WEST;
            }

            if (!this.f) {
                if (flag1 && flag3 && !flag && !flag2) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_EAST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.NORTH_SOUTH) {
                if (BlockMinecartTrackAbstract.e(this.b, blockposition.getX(), blockposition.getY() + 1, blockposition.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_NORTH;
                }

                if (BlockMinecartTrackAbstract.e(this.b, blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_SOUTH;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.EAST_WEST) {
                if (BlockMinecartTrackAbstract.e(this.b, blockposition3.getX(), blockposition3.getY() + 1, blockposition3.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_EAST;
                }

                if (BlockMinecartTrackAbstract.e(this.b, blockposition2.getX(), blockposition2.getY() + 1, blockposition2.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_WEST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == null) {
                blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_SOUTH;
            }

            this.e = this.e.set(this.d.n(), blockminecarttrackabstract_enumtrackposition);
            this.b.setTypeAndData(this.c, this.e, 3);
        }

        private boolean d(BlockPosition blockposition) {
            MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic = this
                    .b(blockposition);

            if (blockminecarttrackabstract_minecarttracklogic == null) {
                return false;
            } else {
                blockminecarttrackabstract_minecarttracklogic.c();
                return blockminecarttrackabstract_minecarttracklogic.b(this);
            }
        }

        public MinecartTrackLogic a(boolean flag, boolean flag1) {
            BlockPosition blockposition = this.c.north();
            BlockPosition blockposition1 = this.c.south();
            BlockPosition blockposition2 = this.c.west();
            BlockPosition blockposition3 = this.c.east();
            boolean flag2 = this.d(blockposition);
            boolean flag3 = this.d(blockposition1);
            boolean flag4 = this.d(blockposition2);
            boolean flag5 = this.d(blockposition3);
            EnumTrackPosition blockminecarttrackabstract_enumtrackposition = null;

            if ((flag2 || flag3) && !flag4 && !flag5) {
                blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_SOUTH;
            }

            if ((flag4 || flag5) && !flag2 && !flag3) {
                blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.EAST_WEST;
            }

            if (!this.f) {
                if (flag3 && flag5 && !flag2 && !flag4) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_EAST;
                }

                if (flag3 && flag4 && !flag2 && !flag5) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_WEST;
                }

                if (flag2 && flag4 && !flag3 && !flag5) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_WEST;
                }

                if (flag2 && flag5 && !flag3 && !flag4) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_EAST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == null) {
                if (flag2 || flag3) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_SOUTH;
                }

                if (flag4 || flag5) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.EAST_WEST;
                }

                if (!this.f) {
                    if (flag) {
                        if (flag3 && flag5) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_EAST;
                        }

                        if (flag4 && flag3) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_WEST;
                        }

                        if (flag5 && flag2) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_EAST;
                        }

                        if (flag2 && flag4) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_WEST;
                        }
                    } else {
                        if (flag2 && flag4) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_WEST;
                        }

                        if (flag5 && flag2) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_EAST;
                        }

                        if (flag4 && flag3) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_WEST;
                        }

                        if (flag3 && flag5) {
                            blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.SOUTH_EAST;
                        }
                    }
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.NORTH_SOUTH) {
                if (BlockMinecartTrackAbstract.e(this.b, blockposition.getX(), blockposition.getY() + 1, blockposition.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_NORTH;
                }

                if (BlockMinecartTrackAbstract.e(this.b, blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_SOUTH;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == EnumTrackPosition.EAST_WEST) {
                if (BlockMinecartTrackAbstract.e(this.b, blockposition3.getX(), blockposition3.getY() + 1, blockposition3.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_EAST;
                }

                if (BlockMinecartTrackAbstract.e(this.b, blockposition2.getX(), blockposition2.getY() + 1, blockposition2.getZ())) {
                    blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.ASCENDING_WEST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == null) {
                blockminecarttrackabstract_enumtrackposition = EnumTrackPosition.NORTH_SOUTH;
            }

            this.a(blockminecarttrackabstract_enumtrackposition);
            this.e = this.e.set(this.d.n(), blockminecarttrackabstract_enumtrackposition);
            if (flag1 || this.b.getType(this.c) != this.e) {
                this.b.setTypeAndData(this.c, this.e, 3);

                for (BlockPosition blockPosition : this.g) {
                    MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic = this.b(blockPosition);

                    if (blockminecarttrackabstract_minecarttracklogic != null) {
                        blockminecarttrackabstract_minecarttracklogic.c();
                        if (blockminecarttrackabstract_minecarttracklogic.b(this)) {
                            blockminecarttrackabstract_minecarttracklogic.c(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockData b() {
            return this.e;
        }
    }
}
