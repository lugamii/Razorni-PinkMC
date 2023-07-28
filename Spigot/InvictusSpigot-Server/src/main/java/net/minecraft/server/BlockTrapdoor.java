package net.minecraft.server;

import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.HashMap;

public class BlockTrapdoor extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);
    public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
    public static final BlockStateEnum<EnumTrapdoorHalf> HALF = BlockStateEnum.of("half", EnumTrapdoorHalf.class);
    public static final HashMap<BlockPosition, AxisAlignedBB> trapdoorCache = new HashMap<>();

    protected BlockTrapdoor(Material material) {
        super(material);
        this.j(this.blockStateList.getBlockData().set(BlockTrapdoor.FACING, EnumDirection.NORTH).set(BlockTrapdoor.OPEN, false).set(BlockTrapdoor.HALF, EnumTrapdoorHalf.BOTTOM));
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.a(CreativeModeTab.d);
    }

    protected static EnumDirection b(int i) {
        switch (i & 3) {
            case 0:
                return EnumDirection.NORTH;

            case 1:
                return EnumDirection.SOUTH;

            case 2:
                return EnumDirection.WEST;

            case 3:
            default:
                return EnumDirection.EAST;
        }
    }

    protected static int a(EnumDirection enumdirection) {
        switch (SyntheticClass_1.a[enumdirection.ordinal()]) {
            case 1:
                return 0;

            case 2:
                return 1;

            case 3:
                return 2;

            case 4:
            default:
                return 3;
        }
    }

    private static boolean c(Block block) {
        return block.material.k() && block.d() || block == Blocks.GLOWSTONE || block instanceof BlockStepAbstract || block instanceof BlockStairs;
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !iblockaccess.getType(blockposition).get(BlockTrapdoor.OPEN);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        AxisAlignedBB axisalignedbb = trapdoorCache.get(blockposition);
        if (axisalignedbb != null) {
            return axisalignedbb;
        } else {
            updateShape(world, blockposition);
            AxisAlignedBB shape = super.a(world, blockposition, iblockdata);
            trapdoorCache.put(blockposition, shape);
            return shape;
        }
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        d(iblockaccess.getType(blockposition));
    }

    public void j() {
        this.a(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
    }

    public void d(IBlockData iblockdata) {
        if (iblockdata.getBlock() == this) {
            if (iblockdata.get(BlockTrapdoor.HALF) == EnumTrapdoorHalf.TOP) {
                a(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                a(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
            }

            if (iblockdata.get(BlockTrapdoor.OPEN)) {
                EnumDirection enumdirection = iblockdata.get(BlockTrapdoor.FACING);

                if (enumdirection == EnumDirection.NORTH) {
                    a(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
                } else if (enumdirection == EnumDirection.SOUTH) {
                    a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
                } else if (enumdirection == EnumDirection.WEST) {
                    a(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else if (enumdirection == EnumDirection.EAST) {
                    a(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
                }
            }

        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        trapdoorCache.remove(blockposition); //When the trapdoor gets interacted with we need to remove it from the cache so it can be recalculated
        if (this.material != Material.ORE) {
            iblockdata = iblockdata.a(BlockTrapdoor.OPEN);
            world.setTypeAndData(blockposition, iblockdata, 2);
            world.a(entityhuman, iblockdata.get(BlockTrapdoor.OPEN) ? 1003 : 1006, blockposition, 0);
        }
        return true;
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        BlockPosition blockposition1 = blockposition.shift(iblockdata.get(BlockTrapdoor.FACING).opposite());

        if (!c(world.getType(blockposition1).getBlock())) {
            trapdoorCache.remove(blockposition);
            world.setAir(blockposition);
            this.b(world, blockposition, iblockdata, 0);
        } else {
            boolean flag = world.isBlockIndirectlyPowered(blockposition);

            if (flag || block.isPowerSource()) {
                // CraftBukkit start
                org.bukkit.World bworld = world.getWorld();
                org.bukkit.block.Block bblock = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());

                int power = bblock.getBlockPower();
                int oldPower = iblockdata.get(OPEN) ? 15 : 0;

                if (oldPower == 0 ^ power == 0 || block.isPowerSource()) {
                    BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bblock, oldPower, power);
                    world.getServer().getPluginManager().callEvent(eventRedstone);
                    flag = eventRedstone.getNewCurrent() > 0;
                }
                // CraftBukkit end
                boolean flag1 = iblockdata.get(BlockTrapdoor.OPEN);

                if (flag1 != flag) {
                    trapdoorCache.remove(blockposition);
                    world.setTypeAndData(blockposition, iblockdata.set(BlockTrapdoor.OPEN, flag), 2);
                    world.a(null, flag ? 1003 : 1006, blockposition, 0);
                }
            }

        }
    }

    public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        updateShape(world, blockposition);
        return super.a(world, blockposition, vec3d, vec3d1);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData();

        if (enumdirection.k().c()) {
            iblockdata = iblockdata.set(BlockTrapdoor.FACING, enumdirection).set(BlockTrapdoor.OPEN, false);
            iblockdata = iblockdata.set(BlockTrapdoor.HALF, f1 > 0.5F ? EnumTrapdoorHalf.TOP : EnumTrapdoorHalf.BOTTOM);
        }

        return iblockdata;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return !enumdirection.k().b() && c(world.getType(blockposition.shift(enumdirection.opposite())).getBlock());
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockTrapdoor.FACING, b(i)).set(BlockTrapdoor.OPEN, (i & 4) != 0).set(BlockTrapdoor.HALF, (i & 8) == 0 ? EnumTrapdoorHalf.BOTTOM : EnumTrapdoorHalf.TOP);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | a(iblockdata.get(BlockTrapdoor.FACING));

        if (iblockdata.get(BlockTrapdoor.OPEN)) {
            i |= 4;
        }

        if (iblockdata.get(BlockTrapdoor.HALF) == EnumTrapdoorHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockTrapdoor.FACING, BlockTrapdoor.OPEN, BlockTrapdoor.HALF);
    }

    public enum EnumTrapdoorHalf implements INamable {

        TOP("top"), BOTTOM("bottom");

        private final String c;

        EnumTrapdoorHalf(String s) {
            this.c = s;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }

        }
    }
}