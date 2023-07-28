package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.*;

public class BlockRedstoneWire extends Block {

    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> NORTH = BlockStateEnum.of("north",
            BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> EAST = BlockStateEnum.of("east",
            BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> SOUTH = BlockStateEnum.of("south",
            BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> WEST = BlockStateEnum.of("west",
            BlockRedstoneWire.EnumRedstoneWireConnection.class);
    public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
    private final Set<BlockPosition> R = Sets.newHashSet();
    private boolean Q = true;

    public BlockRedstoneWire() {
        super(Material.ORIENTABLE);
        this.j(this.blockStateList.getBlockData()
                .set(BlockRedstoneWire.NORTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
                .set(BlockRedstoneWire.EAST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
                .set(BlockRedstoneWire.SOUTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
                .set(BlockRedstoneWire.WEST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
                .set(BlockRedstoneWire.POWER, 0));
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    protected static boolean e(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return d(iblockaccess.getType(blockposition));
    }

    protected static boolean d(IBlockData iblockdata) {
        return a(iblockdata, null);
    }

    protected static boolean a(IBlockData iblockdata, EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.REDSTONE_WIRE) {
            return true;
        } else if (Blocks.UNPOWERED_REPEATER.e(block)) {
            EnumDirection enumdirection1 = iblockdata.get(BlockRepeater.FACING);

            return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
        } else {
            return block.isPowerSource() && enumdirection != null;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = iblockdata.set(BlockRedstoneWire.WEST, this.c(iblockaccess, blockposition, EnumDirection.WEST));
        iblockdata = iblockdata.set(BlockRedstoneWire.EAST, this.c(iblockaccess, blockposition, EnumDirection.EAST));
        iblockdata = iblockdata.set(BlockRedstoneWire.NORTH, this.c(iblockaccess, blockposition, EnumDirection.NORTH));
        iblockdata = iblockdata.set(BlockRedstoneWire.SOUTH, this.c(iblockaccess, blockposition, EnumDirection.SOUTH));
        return iblockdata;
    }

    private BlockRedstoneWire.EnumRedstoneWireConnection c(IBlockAccess iblockaccess, BlockPosition blockposition,
                                                           EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        Block block = iblockaccess.getType(blockposition.shift(enumdirection)).getBlock();

        if (!a(iblockaccess.getType(blockposition1), enumdirection)
                && (block.u() || !d(iblockaccess.getType(blockposition1.getX(), blockposition1.getY() - 1, blockposition1.getZ())))) {
            Block block1 = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock();

            return !block1.u() && block.u() && d(iblockaccess.getType(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()))
                    ? BlockRedstoneWire.EnumRedstoneWireConnection.UP
                    : BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
        } else {
            return BlockRedstoneWire.EnumRedstoneWireConnection.SIDE;
        }
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        IBlockData data = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
        return World.a(data) || data.getBlock() == Blocks.GLOWSTONE;
    }

    private IBlockData e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata = this.a(world, blockposition, blockposition, iblockdata);
        ArrayList<BlockPosition> arraylist = Lists.newArrayList(this.R);

        this.R.clear();

        for (BlockPosition blockposition1 : arraylist) {
            world.applyPhysics(blockposition1, this);
        }

        return iblockdata;
    }

    private IBlockData a(World world, BlockPosition blockposition, BlockPosition blockposition1, IBlockData iblockdata) {
        IBlockData iblockdata1 = iblockdata;
        int i = iblockdata.get(BlockRedstoneWire.POWER);
        byte b0 = 0;
        int j = this.getPower(world, blockposition1, b0);

        this.Q = false;
        int k = world.A(blockposition);

        this.Q = true;
        if (k > 0 && k > j - 1) {
            j = k;
        }

        int l = 0;

        for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            BlockPosition blockposition2 = blockposition.shift(enumdirection);
            boolean flag = blockposition2.getX() != blockposition1.getX()
                    || blockposition2.getZ() != blockposition1.getZ();

            if (flag) {
                l = this.getPower(world, blockposition2, l);
            }

            if (world.getType(blockposition2).getBlock().isOccluding()
                    && !world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().isOccluding()) {
                if (flag && blockposition.getY() >= blockposition1.getY()) {
                    l = this.getPower(world, blockposition2.up(), l);
                }
            } else if (!world.getType(blockposition2).getBlock().isOccluding() && flag
                    && blockposition.getY() <= blockposition1.getY()) {
                l = this.getPower(world, blockposition2.down(), l);
            }
        }

        if (l > j) {
            j = l - 1;
        } else if (j > 0) {
            --j;
        } else {
            j = 0;
        }

        if (k > j - 1) {
            j = k;
        }

        // CraftBukkit start
        if (i != j) {
            BlockRedstoneEvent event = new BlockRedstoneEvent(
                    world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), i,
                    j);
            world.getServer().getPluginManager().callEvent(event);

            j = event.getNewCurrent();
        }
        // CraftBukkit end

        if (i != j) {
            iblockdata = iblockdata.set(BlockRedstoneWire.POWER, j);
            if (world.getType(blockposition) == iblockdata1) {
                world.setTypeAndData(blockposition, iblockdata, 2);
            }

            this.R.add(blockposition);

            for (EnumDirection enumdirection1 : EnumDirection.values()) {
                this.R.add(blockposition.shift(enumdirection1));
            }

        }

        return iblockdata;
    }

    private void e(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).getBlock() == this) {
            world.redstonePhysics(blockposition.getX(), blockposition.getY(), blockposition.getZ(), this);
            for(EnumDirection direction : EnumDirection.values()) {
                world.redstonePhysics(blockposition.getX() + direction.getAdjacentX(), blockposition.getY() + direction.getAdjacentY(), blockposition.getZ() + direction.getAdjacentZ(), this);
            }
        }
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);
        Iterator<EnumDirection> iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

        EnumDirection enumdirection;

        while (iterator.hasNext()) {
            enumdirection = iterator.next();
            world.applyPhysics(blockposition.shift(enumdirection), this);
        }

        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            enumdirection = iterator.next();
            this.e(world, blockposition.shift(enumdirection));
        }

        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            enumdirection = iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (world.getType(blockposition1).getBlock().isOccluding()) {
                this.e(world, blockposition1.up());
            } else {
                this.e(world, blockposition1.down());
            }
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        for (EnumDirection enumdirection : EnumDirection.values()) {
            world.applyPhysics(blockposition.shift(enumdirection), this);
        }

        this.e(world, blockposition, iblockdata);
        Iterator<EnumDirection> iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        EnumDirection enumdirection1;

        while (iterator.hasNext()) {
            enumdirection1 = iterator.next();
            this.e(world, blockposition.shift(enumdirection1));
        }

        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            enumdirection1 = iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection1);

            if (world.getType(blockposition1).getBlock().isOccluding()) {
                this.e(world, blockposition1.up());
            } else {
                this.e(world, blockposition1.down());
            }
        }

    }

    public int getPower(World world, BlockPosition blockposition, int i) {
        IBlockData data = world.getType(blockposition);
        if (data.getBlock() != this) {
            return i;
        } else {
            return Math.max(data.get(BlockRedstoneWire.POWER), i);
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (this.canPlace(world, blockposition)) {
            this.e(world, blockposition, iblockdata);
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REDSTONE;
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return !this.Q ? 0 : this.a(iblockaccess, blockposition, iblockdata, enumdirection);
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        if (!this.Q) {
            return 0;
        } else {
            int i = iblockdata.get(BlockRedstoneWire.POWER);

            if (i == 0) {
                return 0;
            } else if (enumdirection == EnumDirection.UP) {
                return i;
            } else {
                EnumSet<EnumDirection> enumset = EnumSet.noneOf(EnumDirection.class);

                for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                    if (this.d(iblockaccess, blockposition, enumdirection1)) {
                        enumset.add(enumdirection1);
                    }
                }

                if (enumdirection.k().c() && enumset.isEmpty()) {
                    return i;
                } else if (enumset.contains(enumdirection) && !enumset.contains(enumdirection.f())
                        && !enumset.contains(enumdirection.e())) {
                    return i;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition1);
        Block block = iblockdata.getBlock();
        boolean flag = block.isOccluding();
        boolean flag1 = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().isOccluding();

        return !flag1 && flag && e(iblockaccess, blockposition1.up()) || (a(iblockdata, enumdirection) || (block == Blocks.POWERED_REPEATER
                && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection || !flag && e(iblockaccess, blockposition1.down())));
    }

    public boolean isPowerSource() {
        return this.Q;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockRedstoneWire.POWER, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockRedstoneWire.POWER);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST,
                BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER);
    }

    static enum EnumRedstoneWireConnection implements INamable {

        UP("up"), SIDE("side"), NONE("none");

        private final String d;

        private EnumRedstoneWireConnection(String s) {
            this.d = s;
        }

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this.d;
        }
    }
}
