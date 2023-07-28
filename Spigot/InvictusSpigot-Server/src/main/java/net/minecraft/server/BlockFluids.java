package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;

import java.util.Random;

public abstract class BlockFluids extends Block {

    public static final BlockStateInteger LEVEL = BlockStateInteger.of("level", 0, 15);

    protected BlockFluids(Material material) {
        super(material);
        this.j(this.blockStateList.getBlockData().set(BlockFluids.LEVEL, 0));
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.a(true);
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.material != Material.LAVA;
    }

    public static float b(int i) {
        if (i >= 8) {
            i = 0;
        }

        return (float) (i + 1) / 9.0F;
    }

    protected int e(IBlockAccess iblockaccess, int x, int y, int z) {
        IBlockData b = iblockaccess.getType(x, y, z);
        return b.getBlock().getMaterial() == this.material ? b.get(BlockFluids.LEVEL) : -1;
    }

    protected int f(IBlockAccess iblockaccess, int x, int y, int z) {
        int i = this.e(iblockaccess, x, y, z);

        return i >= 8 ? 0 : i;
    }

    protected int e(IBlockData b) {
        return b.getBlock().getMaterial() == this.material ? b.get(BlockFluids.LEVEL) : -1;
    }

    protected int f(IBlockData b) {
        int i = this.e(b);

        return i >= 8 ? 0 : i;
    }

    public boolean d() {
        return false;
    }

    public boolean c() {
        return false;
    }

    public boolean a(IBlockData iblockdata, boolean flag) {
        return flag && iblockdata.get(BlockFluids.LEVEL) == 0;
    }

    public boolean bA(IBlockAccess iblockaccess, int x, int y, int z, EnumDirection enumdirection) {
        Material material = iblockaccess.getType(x, y, z).getBlock().getMaterial();

        return material != this.material && (enumdirection == EnumDirection.UP || (material != Material.ICE && material.isBuildable()));
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public int b() {
        return 1;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return null;
    }

    public int a(Random random) {
        return 0;
    }

    protected Vec3D h(IBlockAccess iblockaccess, BlockPosition blockposition) {
        Vec3D vec3d = new Vec3D(0.0D, 0.0D, 0.0D);
        int bX = blockposition.getX(), bY = blockposition.getY(), bZ = blockposition.getZ();
        int i = this.f(iblockaccess, bX, bY, bZ);

        int x, y, z;

        for(EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            x = bX + enumdirection.getAdjacentX();
            y = bY + enumdirection.getAdjacentY();
            z = bZ + enumdirection.getAdjacentZ();

            int j = this.f(iblockaccess, x, y, z);
            int k;

            if (j < 0) {
                if (!iblockaccess.getType(x, y, z).getBlock().getMaterial().isSolid()) {
                    j = this.f(iblockaccess, x, y - 1, z);
                    if (j >= 0) {
                        k = j - (i - 8);
                        vec3d = vec3d.add(((x - bX) * k), ((y - bY) * k), ((z - bZ) * k));
                    }
                }
            } else {
                k = j - i;
                vec3d = vec3d.add(((x - bX) * k), ((y - bY) * k), ((z - bZ) * k));
            }
        }

        if (iblockaccess.getType(bX, bY, bZ).get(BlockFluids.LEVEL) >= 8) {
            for(EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                x = bX + enumdirection.getAdjacentX();
                y = bY + enumdirection.getAdjacentY();
                z = bZ + enumdirection.getAdjacentZ();

                if (this.bA(iblockaccess, x, y, z, enumdirection)
                        || this.bA(iblockaccess, x, y + 1, z, enumdirection)) {
                    vec3d = vec3d.a().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3d.a();
    }

    public Vec3D a(World world, BlockPosition blockposition, Entity entity, Vec3D vec3d) {
        return vec3d.e(this.h(world, blockposition));
    }

    public int a(World world) {
        return this.material == Material.WATER ? 5 : (this.material == Material.LAVA ? (world.worldProvider.o() ? 10 : 30) : 0);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        this.e(world, blockposition, iblockdata);
    }

    public boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.material == Material.LAVA) {
            boolean flag = false;

            int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();
            for (EnumDirection enumdirection : EnumDirection.values()) {
                if (enumdirection != EnumDirection.DOWN && world.getType(x + enumdirection.getAdjacentX(), y + enumdirection.getAdjacentY(), z + enumdirection.getAdjacentZ()).getBlock().getMaterial() == Material.WATER) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                int integer = iblockdata.get(BlockFluids.LEVEL);
                if (integer >= 0) {
                    if (integer == 0 && InvictusConfig.generateObsidian) {
                        world.setTypeUpdate(blockposition, Blocks.OBSIDIAN.getBlockData());
                    } else if(InvictusConfig.generateCobble) {
                        world.setTypeUpdate(blockposition, Blocks.COBBLESTONE.getBlockData());
                    }
                    fizz(world, blockposition);
                    return true;
                }
            }
        }

        return false;
    }

    protected void fizz(World world, BlockPosition blockposition) {
        fizz(world, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    protected void fizz(World world, int d0, int d1, int d2) {
        if(InvictusConfig.fluidFizzSounds) {
            world.makeSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
            for (int i = 0; i < 8; i++)
                world.addParticle(EnumParticle.SMOKE_LARGE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D, EnumParticle.EMPTY_ARRAY);
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockFluids.LEVEL, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockFluids.LEVEL);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockFluids.LEVEL);
    }

    public static BlockFlowing a(Material material) {
        if (material == Material.WATER) {
            return Blocks.FLOWING_WATER;
        } else if (material == Material.LAVA) {
            return Blocks.FLOWING_LAVA;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public static BlockStationary b(Material material) {
        if (material == Material.WATER) {
            return Blocks.WATER;
        } else if (material == Material.LAVA) {
            return Blocks.LAVA;
        } else {
            throw new IllegalArgumentException("Invalid material");
        }
    }
}
