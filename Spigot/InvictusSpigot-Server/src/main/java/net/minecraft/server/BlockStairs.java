package net.minecraft.server;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockStairs extends Block {
    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", EnumDirection.EnumDirectionLimit.HORIZONTAL);

    public static final BlockStateEnum<EnumHalf> HALF = BlockStateEnum.of("half", EnumHalf.class);

    public static final BlockStateEnum<EnumStairShape> SHAPE = BlockStateEnum.of("shape", EnumStairShape.class);

    private static final int[][] O = new int[][]{{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};

    private final Block P;

    private final IBlockData Q;

    private boolean R;

    private int S;

    protected BlockStairs(IBlockData paramIBlockData) {
        super((paramIBlockData.getBlock()).material);
        j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(HALF, EnumHalf.BOTTOM).set(SHAPE, EnumStairShape.STRAIGHT));
        this.P = paramIBlockData.getBlock();
        this.Q = paramIBlockData;
        c(this.P.strength);
        b(this.P.durability / 3.0F);
        a(this.P.stepSound);
        e(255);
        a(CreativeModeTab.b);
    }

    public static boolean c(Block paramBlock) {
        return paramBlock instanceof BlockStairs;
    }

    public static boolean a(IBlockAccess iblockaccess, int x, int y, int z, IBlockData paramIBlockData) {
        IBlockData iBlockData = iblockaccess.getType(x, y, z);
        Block block = iBlockData.getBlock();
        return c(block) && iBlockData.get(HALF) == paramIBlockData.get(HALF) && iBlockData.get(FACING) == paramIBlockData.get(FACING);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (this.R) {
            a(0.5F * (this.S % 2), 0.5F * (this.S / 4 % 2), 0.5F * (this.S / 2 % 2), 0.5F + 0.5F * (this.S % 2), 0.5F + 0.5F * (this.S / 4 % 2), 0.5F + 0.5F * (this.S / 2 % 2));
        } else {
            a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public void e(IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (iblockaccess.getType(blockposition).get(HALF) == EnumHalf.TOP) {
            a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    public int f(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iBlockData = iblockaccess.getType(blockposition);
        EnumDirection enumDirection = iBlockData.get(FACING);
        EnumHalf enumHalf = iBlockData.get(HALF);
        boolean bool = enumHalf == EnumHalf.TOP;
        if (enumDirection == EnumDirection.EAST) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData))
                    return bool ? 1 : 2;
                if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData))
                    return bool ? 2 : 1;
            }
        } else if (enumDirection == EnumDirection.WEST) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData))
                    return bool ? 2 : 1;
                if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData))
                    return bool ? 1 : 2;
            }
        } else if (enumDirection == EnumDirection.SOUTH) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 2 : 1;
                if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 1 : 2;
            }
        } else if (enumDirection == EnumDirection.NORTH) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 1 : 2;
                if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 2 : 1;
            }
        }
        return 0;
    }

    public int g(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iBlockData = iblockaccess.getType(blockposition);
        EnumDirection enumDirection = iBlockData.get(FACING);
        EnumHalf enumHalf = iBlockData.get(HALF);
        boolean bool = enumHalf == EnumHalf.TOP;
        if (enumDirection == EnumDirection.EAST) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData))
                    return bool ? 1 : 2;
                if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData))
                    return bool ? 2 : 1;
            }
        } else if (enumDirection == EnumDirection.WEST) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData))
                    return bool ? 2 : 1;
                if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData))
                    return bool ? 1 : 2;
            }
        } else if (enumDirection == EnumDirection.SOUTH) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 2 : 1;
                if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 1 : 2;
            }
        } else if (enumDirection == EnumDirection.NORTH) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 1 : 2;
                if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData))
                    return bool ? 2 : 1;
            }
        }
        return 0;
    }

    public boolean h(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iBlockData = iblockaccess.getType(blockposition);
        EnumDirection enumDirection = iBlockData.get(FACING);
        EnumHalf enumHalf = iBlockData.get(HALF);
        boolean bool1 = enumHalf == EnumHalf.TOP;
        float f1 = 0.5F;
        float f2 = 1.0F;
        if (bool1) {
            f1 = 0.0F;
            f2 = 0.5F;
        }
        float f3 = 0.0F;
        float f4 = 1.0F;
        float f5 = 0.0F;
        float f6 = 0.5F;
        boolean bool2 = true;
        if (enumDirection == EnumDirection.EAST) {
            f3 = 0.5F;
            f6 = 1.0F;
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData)) {
                    f6 = 0.5F;
                    bool2 = false;
                } else if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData)) {
                    f5 = 0.5F;
                    bool2 = false;
                }
            }
        } else if (enumDirection == EnumDirection.WEST) {
            f4 = 0.5F;
            f6 = 1.0F;
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData)) {
                    f6 = 0.5F;
                    bool2 = false;
                } else if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData)) {
                    f5 = 0.5F;
                    bool2 = false;
                }
            }
        } else if (enumDirection == EnumDirection.SOUTH) {
            f5 = 0.5F;
            f6 = 1.0F;
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    f4 = 0.5F;
                    bool2 = false;
                } else if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    f3 = 0.5F;
                    bool2 = false;
                }
            }
        } else if (enumDirection == EnumDirection.NORTH) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    f4 = 0.5F;
                    bool2 = false;
                } else if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    f3 = 0.5F;
                    bool2 = false;
                }
            }
        }
        a(f3, f1, f5, f4, f2, f6);
        return bool2;
    }

    public boolean i(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iBlockData = iblockaccess.getType(blockposition);
        EnumDirection enumDirection = iBlockData.get(FACING);
        EnumHalf enumHalf = iBlockData.get(HALF);
        boolean bool1 = enumHalf == EnumHalf.TOP;
        float f1 = 0.5F;
        float f2 = 1.0F;
        if (bool1) {
            f1 = 0.0F;
            f2 = 0.5F;
        }
        float f3 = 0.0F;
        float f4 = 0.5F;
        float f5 = 0.5F;
        float f6 = 1.0F;
        boolean bool2 = false;
        if (enumDirection == EnumDirection.EAST) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData)) {
                    f5 = 0.0F;
                    f6 = 0.5F;
                    bool2 = true;
                } else if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData)) {
                    bool2 = true;
                }
            }
        } else if (enumDirection == EnumDirection.WEST) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ());
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                f3 = 0.5F;
                f4 = 1.0F;
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.NORTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1, iBlockData)) {
                    f5 = 0.0F;
                    f6 = 0.5F;
                    bool2 = true;
                } else if (enumDirection1 == EnumDirection.SOUTH && !a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1, iBlockData)) {
                    bool2 = true;
                }
            }
        } else if (enumDirection == EnumDirection.SOUTH) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                f5 = 0.0F;
                f6 = 0.5F;
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    bool2 = true;
                } else if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    f3 = 0.5F;
                    f4 = 1.0F;
                    bool2 = true;
                }
            }
        } else if (enumDirection == EnumDirection.NORTH) {
            IBlockData iBlockData1 = iblockaccess.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1);
            Block block = iBlockData1.getBlock();
            if (c(block) && enumHalf == iBlockData1.get(HALF)) {
                EnumDirection enumDirection1 = iBlockData1.get(FACING);
                if (enumDirection1 == EnumDirection.WEST && !a(iblockaccess, blockposition.getX() - 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    bool2 = true;
                } else if (enumDirection1 == EnumDirection.EAST && !a(iblockaccess, blockposition.getX() + 1, blockposition.getY(), blockposition.getZ(), iBlockData)) {
                    f3 = 0.5F;
                    f4 = 1.0F;
                    bool2 = true;
                }
            }
        }
        if (bool2)
            a(f3, f1, f5, f4, f2, f6);
        return bool2;
    }

    public void a(World paramWorld, BlockPosition blockposition, IBlockData paramIBlockData, AxisAlignedBB paramAxisAlignedBB, List<AxisAlignedBB> paramList, Entity paramEntity) {
        e(paramWorld, blockposition);
        super.a(paramWorld, blockposition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
        boolean bool = h(paramWorld, blockposition);
        super.a(paramWorld, blockposition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
        if (bool &&
                i(paramWorld, blockposition))
            super.a(paramWorld, blockposition, paramIBlockData, paramAxisAlignedBB, paramList, paramEntity);
        a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void attack(World paramWorld, BlockPosition blockposition, EntityHuman paramEntityHuman) {
        this.P.attack(paramWorld, blockposition, paramEntityHuman);
    }

    public void postBreak(World paramWorld, BlockPosition blockposition, IBlockData paramIBlockData) {
        this.P.postBreak(paramWorld, blockposition, paramIBlockData);
    }

    public float a(Entity paramEntity) {
        return this.P.a(paramEntity);
    }

    public int a(World paramWorld) {
        return this.P.a(paramWorld);
    }

    public Vec3D a(World paramWorld, BlockPosition blockposition, Entity paramEntity, Vec3D paramVec3D) {
        return this.P.a(paramWorld, blockposition, paramEntity, paramVec3D);
    }

    public boolean A() {
        return this.P.A();
    }

    public boolean a(IBlockData paramIBlockData, boolean paramBoolean) {
        return this.P.a(paramIBlockData, paramBoolean);
    }

    public boolean canPlace(World paramWorld, BlockPosition blockposition) {
        return this.P.canPlace(paramWorld, blockposition);
    }

    public void onPlace(World paramWorld, BlockPosition blockposition, IBlockData paramIBlockData) {
        doPhysics(paramWorld, blockposition, this.Q, Blocks.AIR);
        this.P.onPlace(paramWorld, blockposition, this.Q);
    }

    public void remove(World paramWorld, BlockPosition blockposition, IBlockData paramIBlockData) {
        this.P.remove(paramWorld, blockposition, this.Q);
    }

    public void a(World paramWorld, BlockPosition blockposition, Entity paramEntity) {
        this.P.a(paramWorld, blockposition, paramEntity);
    }

    public void b(World paramWorld, BlockPosition blockposition, IBlockData paramIBlockData, Random paramRandom) {
        this.P.b(paramWorld, blockposition, paramIBlockData, paramRandom);
    }

    public boolean interact(World paramWorld, BlockPosition blockposition, IBlockData paramIBlockData, EntityHuman paramEntityHuman, EnumDirection paramEnumDirection, float paramFloat1, float paramFloat2, float paramFloat3) {
        return this.P.interact(paramWorld, blockposition, this.Q, paramEntityHuman, EnumDirection.DOWN, 0.0F, 0.0F, 0.0F);
    }

    public void wasExploded(World paramWorld, BlockPosition blockposition, Explosion paramExplosion) {
        this.P.wasExploded(paramWorld, blockposition, paramExplosion);
    }

    public MaterialMapColor g(IBlockData paramIBlockData) {
        return this.P.g(this.Q);
    }

    public IBlockData getPlacedState(World paramWorld, BlockPosition blockposition, EnumDirection paramEnumDirection, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, EntityLiving paramEntityLiving) {
        IBlockData iBlockData = super.getPlacedState(paramWorld, blockposition, paramEnumDirection, paramFloat1, paramFloat2, paramFloat3, paramInt, paramEntityLiving);
        iBlockData = iBlockData.set(FACING, paramEntityLiving.getDirection()).set(SHAPE, EnumStairShape.STRAIGHT);
        if (paramEnumDirection == EnumDirection.DOWN || (paramEnumDirection != EnumDirection.UP && paramFloat2 > 0.5D))
            return iBlockData.set(HALF, EnumHalf.TOP);
        return iBlockData.set(HALF, EnumHalf.BOTTOM);
    }

    public MovingObjectPosition a(World paramWorld, BlockPosition blockposition, Vec3D paramVec3D1, Vec3D paramVec3D2) {
        MovingObjectPosition[] arrayOfMovingObjectPosition = new MovingObjectPosition[8];
        IBlockData iBlockData = paramWorld.getType(blockposition);
        int i = iBlockData.get(FACING).b();
        boolean bool = iBlockData.get(HALF) == EnumHalf.TOP;
        int[] arrayOfInt = O[i + (bool ? 4 : 0)];
        this.R = true;
        for (byte b = 0; b < 8; b++) {
            this.S = b;
            if (Arrays.binarySearch(arrayOfInt, b) < 0)
                arrayOfMovingObjectPosition[b] = super.a(paramWorld, blockposition, paramVec3D1, paramVec3D2);
        }
        for (int j : arrayOfInt)
            arrayOfMovingObjectPosition[j] = null;
        MovingObjectPosition movingObjectPosition = null;
        double d = 0.0D;
        for (MovingObjectPosition movingObjectPosition1 : arrayOfMovingObjectPosition) {
            if (movingObjectPosition1 != null) {
                double d1 = movingObjectPosition1.pos.distanceSquared(paramVec3D2);
                if (d1 > d) {
                    movingObjectPosition = movingObjectPosition1;
                    d = d1;
                }
            }
        }
        return movingObjectPosition;
    }

    public IBlockData fromLegacyData(int paramInt) {
        IBlockData iBlockData = getBlockData().set(HALF, ((paramInt & 0x4) > 0) ? EnumHalf.TOP : EnumHalf.BOTTOM);
        iBlockData = iBlockData.set(FACING, EnumDirection.fromType1(5 - (paramInt & 0x3)));
        return iBlockData;
    }

    public int toLegacyData(IBlockData paramIBlockData) {
        int i = 0;
        if (paramIBlockData.get(HALF) == EnumHalf.TOP)
            i |= 0x4;
        i |= 5 - paramIBlockData.get(FACING).a();
        return i;
    }

    public IBlockData updateState(IBlockData paramIBlockData, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (h(iblockaccess, blockposition)) {
            switch (g(iblockaccess, blockposition)) {
                case 0:
                    paramIBlockData = paramIBlockData.set(SHAPE, EnumStairShape.STRAIGHT);
                    break;
                case 1:
                    paramIBlockData = paramIBlockData.set(SHAPE, EnumStairShape.INNER_RIGHT);
                    break;
                case 2:
                    paramIBlockData = paramIBlockData.set(SHAPE, EnumStairShape.INNER_LEFT);
                    break;
            }
        } else {
            switch (f(iblockaccess, blockposition)) {
                case 0:
                    paramIBlockData = paramIBlockData.set(SHAPE, EnumStairShape.STRAIGHT);
                    break;
                case 1:
                    paramIBlockData = paramIBlockData.set(SHAPE, EnumStairShape.OUTER_RIGHT);
                    break;
                case 2:
                    paramIBlockData = paramIBlockData.set(SHAPE, EnumStairShape.OUTER_LEFT);
                    break;
            }
        }
        return paramIBlockData;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, FACING, HALF, SHAPE);
    }

    public enum EnumHalf implements INamable {
        TOP("top"),
        BOTTOM("bottom");

        private final String c;

        EnumHalf(String param1String1) {
            this.c = param1String1;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }

    public enum EnumStairShape implements INamable {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String f;

        EnumStairShape(String param1String1) {
            this.f = param1String1;
        }

        public String toString() {
            return this.f;
        }

        public String getName() {
            return this.f;
        }
    }
}
