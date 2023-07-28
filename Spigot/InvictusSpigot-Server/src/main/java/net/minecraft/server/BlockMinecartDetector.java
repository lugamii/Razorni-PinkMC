package net.minecraft.server;

import com.google.common.base.Predicate;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.List;
import java.util.Random;

public class BlockMinecartDetector extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<EnumTrackPosition> SHAPE = BlockStateEnum.a("shape",
            EnumTrackPosition.class, new Predicate() {
                public boolean a(
                        EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
                    return blockminecarttrackabstract_enumtrackposition != EnumTrackPosition.NORTH_EAST
                            && blockminecarttrackabstract_enumtrackposition != EnumTrackPosition.NORTH_WEST
                            && blockminecarttrackabstract_enumtrackposition != EnumTrackPosition.SOUTH_EAST
                            && blockminecarttrackabstract_enumtrackposition != EnumTrackPosition.SOUTH_WEST;
                }

                public boolean apply(Object object) {
                    return this.a((EnumTrackPosition) object);
                }
            });
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");

    public BlockMinecartDetector() {
        super(true);
        this.j(this.blockStateList.getBlockData().set(BlockMinecartDetector.POWERED, Boolean.valueOf(false))
                .set(BlockMinecartDetector.SHAPE, EnumTrackPosition.NORTH_SOUTH));
        this.a(true);
    }

    public int a(World world) {
        return 20;
    }

    public boolean isPowerSource() {
        return true;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        if (!iblockdata.get(BlockMinecartDetector.POWERED)) {
            this.e(world, blockposition, iblockdata);
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (iblockdata.get(BlockMinecartDetector.POWERED)) {
            this.e(world, blockposition, iblockdata);
        }
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
                 EnumDirection enumdirection) {
        return iblockdata.get(BlockMinecartDetector.POWERED) ? 15 : 0;
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
                 EnumDirection enumdirection) {
        return !iblockdata.get(BlockMinecartDetector.POWERED) ? 0
                : (enumdirection == EnumDirection.UP ? 15 : 0);
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = ((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue();
        boolean flag1 = false;
        List<EntityMinecartAbstract> list = this.a(world, blockposition, EntityMinecartAbstract.class, new Predicate[0]);

        if (!list.isEmpty()) {
            flag1 = true;
        }

        // CraftBukkit start
        final int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();
        if (flag != flag1) {
            org.bukkit.block.Block block = world.getWorld().getBlockAt(x, y, z);

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, flag ? 15 : 0, flag1 ? 15 : 0);
            world.getServer().getPluginManager().callEvent(eventRedstone);

            flag1 = eventRedstone.getNewCurrent() > 0;
        }
        // CraftBukkit end

        if (flag1 && !flag) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockMinecartDetector.POWERED, true), 3);
            world.applyPhysics(x, y, z, this);
            world.applyPhysics(x, y - 1, z, this);
            world.b(x, y, z, x, y, z);
        }

        if (!flag1 && flag) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockMinecartDetector.POWERED, false), 3);
            world.applyPhysics(x, y, z, this);
            world.applyPhysics(x, y - 1, z, this);
            world.b(x, y, z, x, y, z);
        }

        if (flag1) {
            world.a(blockposition, this, this.a(world));
        }

        world.updateAdjacentComparators(new BlockPosition(x, y, z), this);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.onPlace(world, blockposition, iblockdata);
        this.e(world, blockposition, iblockdata);
    }

    public IBlockState<EnumTrackPosition> n() {
        return BlockMinecartDetector.SHAPE;
    }

    public boolean isComplexRedstone() {
        return true;
    }

    public int l(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).get(BlockMinecartDetector.POWERED)) {
            List<EntityMinecartCommandBlock> list = this.a(world, blockposition, EntityMinecartCommandBlock.class, new Predicate[0]);

            if (!list.isEmpty()) {
                return ((EntityMinecartCommandBlock) list.get(0)).getCommandBlock().j();
            }

            List<EntityMinecartAbstract> list1 = this.a(world, blockposition, EntityMinecartAbstract.class,
                    new Predicate[]{IEntitySelector.c});

            if (!list1.isEmpty()) {
                return Container.b((IInventory) list1.get(0));
            }
        }

        return 0;
    }

    protected <T extends EntityMinecartAbstract> List<T> a(World world, BlockPosition blockposition, Class<T> oclass,
                                                           Predicate<Entity>... apredicate) {
        AxisAlignedBB axisalignedbb = this.a(blockposition);

        return apredicate.length != 1 ? world.a(oclass, axisalignedbb) : world.a(oclass, axisalignedbb, apredicate[0]);
    }

    private AxisAlignedBB a(BlockPosition blockposition) {

        return new AxisAlignedBB(blockposition.getX() + 0.2F, blockposition.getY(),
                blockposition.getZ() + 0.2F, (blockposition.getX() + 1) - 0.2F,
                (blockposition.getY() + 1) - 0.2F,
                (blockposition.getZ() + 1) - 0.2F);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData()
                .set(BlockMinecartDetector.SHAPE, EnumTrackPosition.a(i & 7))
                .set(BlockMinecartDetector.POWERED, (i & 8) > 0);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | iblockdata.get(BlockMinecartDetector.SHAPE).a();

        if (iblockdata.get(BlockMinecartDetector.POWERED)) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this,
                new IBlockState[]{BlockMinecartDetector.SHAPE, BlockMinecartDetector.POWERED});
    }
}
