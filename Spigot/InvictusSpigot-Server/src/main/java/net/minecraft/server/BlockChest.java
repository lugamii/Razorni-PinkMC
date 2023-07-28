package net.minecraft.server;

import java.util.Iterator;

public class BlockChest extends BlockContainer {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);
    public final int b;

    protected BlockChest(int i) {
        super(Material.WOOD);
        this.j(this.blockStateList.getBlockData().set(BlockChest.FACING, EnumDirection.NORTH));
        this.b = i;
        this.a(CreativeModeTab.c);
        this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public int b() {
        return 2;
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        final int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();
        if (iblockaccess.getType(x, y, z - 1).getBlock() == this) {
            this.a(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        } else if (iblockaccess.getType(x, y, z + 1).getBlock() == this) {
            this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        } else if (iblockaccess.getType(x - 1, y, z).getBlock() == this) {
            this.a(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        } else if (iblockaccess.getType(x + 1, y, z).getBlock() == this) {
            this.a(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        } else {
            this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);

        for (EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            BlockPosition blockposition1 = blockposition.shift(enumDirection);
            IBlockData iblockdata1 = world.getType(blockposition1);

            if (iblockdata1.getBlock() == this) {
                this.e(world, blockposition1, iblockdata1);
            }
        }

    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockChest.FACING, entityliving.getDirection());
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        EnumDirection enumdirection = EnumDirection.fromType2(MathHelper.floor((entityliving.yaw * 4.0F / 360.0F) + 0.5D) & 3).opposite();

        iblockdata = iblockdata.set(BlockChest.FACING, enumdirection);
        final int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();
        boolean flag = this == world.getType(x, y, z - 1).getBlock();
        boolean flag1 = this == world.getType(x, y, z + 1).getBlock();
        boolean flag2 = this == world.getType(x - 1, y, z).getBlock();
        boolean flag3 = this == world.getType(x + 1, y, z).getBlock();

        if (!flag && !flag1 && !flag2 && !flag3) {
            world.setTypeAndData(blockposition, iblockdata, 3, false, false);
        } else if (enumdirection.k() == EnumDirection.EnumAxis.X && (flag || flag1)) {
            if (flag) {
                world.setTypeAndData(blockposition.north(), iblockdata, 3, false, false);
            } else {
                world.setTypeAndData(blockposition.south(), iblockdata, 3, false, false);
            }

            world.setTypeAndData(blockposition, iblockdata, 3);
        } else if (enumdirection.k() == EnumDirection.EnumAxis.Z && (flag2 || flag3)) {
            if (flag2) {
                world.setTypeAndData(blockposition.west(), iblockdata, 3, false, false);
            } else {
                world.setTypeAndData(blockposition.east(), iblockdata, 3, false, false);
            }

            world.setTypeAndData(blockposition, iblockdata, 3, false, false);
        }

        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).a(itemstack.getName());
            }
        }

    }

    public IBlockData e(World world, BlockPosition blockposition, IBlockData iblockdata) {

        final int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();
        IBlockData iblockdata1 = world.getType(x, y, z - 1);
        IBlockData iblockdata2 = world.getType(x, y, z + 1);
        IBlockData iblockdata3 = world.getType(x - 1, y, z);
        IBlockData iblockdata4 = world.getType(x + 1, y, z);
        EnumDirection enumdirection = iblockdata.get(BlockChest.FACING);
        Block block = iblockdata1.getBlock();
        Block block1 = iblockdata2.getBlock();
        Block block2 = iblockdata3.getBlock();
        Block block3 = iblockdata4.getBlock();

        if (block != this && block1 != this) {
            boolean flag = block.o();
            boolean flag1 = block1.o();

            if (block2 == this || block3 == this) {
                int x1 = block2 == this ? x - 1 : x + 1;
                enumdirection = EnumDirection.SOUTH;
                EnumDirection enumdirection1;

                if (block2 == this) {
                    enumdirection1 = iblockdata3.get(BlockChest.FACING);
                } else {
                    enumdirection1 = iblockdata4.get(BlockChest.FACING);
                }

                if (enumdirection1 == EnumDirection.NORTH) {
                    enumdirection = EnumDirection.NORTH;
                }

                Block block4 = world.getType(x1, y, z - 1).getBlock();
                Block block5 = world.getType(x1, y, z + 1).getBlock();

                if ((flag || block4.o()) && !flag1 && !block5.o()) {
                    enumdirection = EnumDirection.SOUTH;
                }

                if ((flag1 || block5.o()) && !flag && !block4.o()) {
                    enumdirection = EnumDirection.NORTH;
                }
            }
        } else {
            int x1 = block2 == this ? x - 1 : x + 1;
            enumdirection = EnumDirection.EAST;
            EnumDirection enumdirection2;

            if (block == this) {
                enumdirection2 = iblockdata1.get(BlockChest.FACING);
            } else {
                enumdirection2 = iblockdata2.get(BlockChest.FACING);
            }

            if (enumdirection2 == EnumDirection.WEST) {
                enumdirection = EnumDirection.WEST;
            }

            Block block6 = world.getType(x1, y, z - 1).getBlock();
            Block block7 = world.getType(x1, y, z + 1).getBlock();

            if ((block2.o() || block6.o()) && !block3.o() && !block7.o()) {
                enumdirection = EnumDirection.EAST;
            }

            if ((block3.o() || block7.o()) && !block2.o() && !block6.o()) {
                enumdirection = EnumDirection.WEST;
            }
        }

        iblockdata = iblockdata.set(BlockChest.FACING, enumdirection);
        world.setTypeAndData(blockposition, iblockdata, 3);
        return iblockdata;
    }

    public IBlockData f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return f(world, blockposition, iblockdata, false);
    }

    public IBlockData f(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        EnumDirection enumdirection = null;
        final int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();

        for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            IBlockData iblockdata1 = world.getType(x + enumdirection1.getAdjacentX(), y + enumdirection1.getAdjacentY(), z + enumdirection1.getAdjacentZ());

            if (iblockdata1.getBlock() == this) {
                return iblockdata;
            }

            if (iblockdata1.getBlock().o()) {
                if (enumdirection != null) {
                    enumdirection = null;
                    break;
                }

                enumdirection = enumdirection1;
            }
        }

        if (enumdirection != null) {
            return iblockdata.set(BlockChest.FACING, enumdirection.opposite());
        } else {
            EnumDirection enumdirection2 = iblockdata.get(BlockChest.FACING);

            if (world.getType(x + enumdirection2.getAdjacentX(), y + enumdirection2.getAdjacentY(), z + enumdirection2.getAdjacentZ()).getBlock().o()) {
                enumdirection2 = enumdirection2.opposite();
            }

            if (world.getType(x + enumdirection2.getAdjacentX(), y + enumdirection2.getAdjacentY(), z + enumdirection2.getAdjacentZ()).getBlock().o()) {
                enumdirection2 = enumdirection2.e();
            }

            if (world.getType(x + enumdirection2.getAdjacentX(), y + enumdirection2.getAdjacentY(), z + enumdirection2.getAdjacentZ()).getBlock().o()) {
                enumdirection2 = enumdirection2.opposite();
            }

            return iblockdata.set(BlockChest.FACING, enumdirection2);
        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        int i = 0;
        final int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();
        if (world.getType(x - 1, y, z).getBlock() == this) {
            if (this.m(world, x - 1, y, z)) {
                return false;
            }

            ++i;
        }

        if (world.getType(x + 1, y, z).getBlock() == this) {
            if (this.m(world, x + 1, y, z)) {
                return false;
            }

            ++i;
        }

        if (world.getType(x, y, z - 1).getBlock() == this) {
            if (this.m(world, x, y, z - 1)) {
                return false;
            }

            ++i;
        }

        if (world.getType(x, y, z + 1).getBlock() == this) {
            if (this.m(world, x, y, z + 1)) {
                return false;
            }

            ++i;
        }

        return i <= 1;
    }

    private boolean m(World world, int x, int y, int z) {
        Iterator<EnumDirection> iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();
        EnumDirection enumdirection;
        do {
            if (!iterator.hasNext())
                return false;
            enumdirection = iterator.next();
        } while (world.getType(x + enumdirection.getAdjacentX(), y + enumdirection.getAdjacentY(), z + enumdirection.getAdjacentZ()).getBlock() != this);
        return true;
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        super.doPhysics(world, blockposition, iblockdata, block);
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityChest) {
            tileentity.E();
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof IInventory) {
            InventoryUtils.dropInventory(world, blockposition, (IInventory) tileentity);
            world.updateAdjacentComparators(blockposition, this);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        ITileInventory itileinventory = getInventory(world, blockposition);

        if (itileinventory != null) {
            entityhuman.openContainer(itileinventory);
        }

        return true;
    }

    //Paper - OBF HELPER
    public ITileInventory getInventory(World world, BlockPosition blockposition) {
        return f(world, blockposition, false);
    }

    public ITileInventory f(World world, BlockPosition blockposition) {
        return f(world, blockposition, true);
    }

    public ITileInventory f(World world, BlockPosition blockposition, boolean flag) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (!(tileentity instanceof TileEntityChest))
            return null;

        Object object = tileentity;

        if (flag && this.n(world, blockposition))
            return null;

        for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            IBlockData typeIfLoaded = world.getTypeIfLoaded(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());

            if (typeIfLoaded == null)
                continue;
            Block block = typeIfLoaded.getBlock();

            if (block == this) {
                if (this.n(world, blockposition1)) {
                    return null;
                }

                TileEntity tileentity1 = world.getTileEntity(blockposition1);

                if (tileentity1 instanceof TileEntityChest) {
                    if (enumdirection != EnumDirection.WEST && enumdirection != EnumDirection.NORTH) {
                        object = new InventoryLargeChest("container.chestDouble", (ITileInventory) object, (TileEntityChest) tileentity1);
                    } else {
                        object = new InventoryLargeChest("container.chestDouble", (TileEntityChest) tileentity1, (ITileInventory) object);
                    }
                }
            }
        }

        return (ITileInventory) object;


    }

    public TileEntity a(World world, int i) {
        return new TileEntityChest();
    }

    public boolean isPowerSource() {
        return this.b == 1;
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        if (!this.isPowerSource()) {
            return 0;
        } else {
            int i = 0;
            TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                i = ((TileEntityChest) tileentity).l;
            }

            return MathHelper.clamp(i, 0, 15);
        }
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? this.a(iblockaccess, blockposition, iblockdata, enumdirection) : 0;
    }

    private boolean n(World world, BlockPosition blockposition) {
        return this.o(world, blockposition) || this.p(world, blockposition);
    }

    private boolean o(World world, BlockPosition blockposition) {
        return world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().isOccluding();
    }

    private boolean p(World world, BlockPosition blockposition) {
        return false;
    }

    public boolean isComplexRedstone() {
        return true;
    }

    public int l(World world, BlockPosition blockposition) {
        return Container.b(this.getInventory(world, blockposition));
    }

    public IBlockData fromLegacyData(int i) {
        EnumDirection enumdirection = EnumDirection.fromType1(i);

        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            enumdirection = EnumDirection.NORTH;
        }

        return this.getBlockData().set(BlockChest.FACING, enumdirection);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockChest.FACING).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockChest.FACING);
    }
}