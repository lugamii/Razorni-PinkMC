package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import org.bukkit.craftbukkit.event.CraftEventFactory;

import java.util.Iterator;
import java.util.List;

public class EntityFallingBlock extends Entity {

    public int ticksLived;
    public boolean dropItem = true;
    public boolean hurtEntities; // PAIL: private -> public
    public NBTTagCompound tileEntityData;
    public org.bukkit.Location sourceLoc; // PaperSpigot
    private IBlockData block;
    private boolean e;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;

    // PaperSpigot start - Add FallingBlock source location API
    public EntityFallingBlock(World world) {
        this(null, world);
    }

    public EntityFallingBlock(org.bukkit.Location loc, World world) {
        super(world);
        sourceLoc = loc;
        loadChunks = world.paperSpigotConfig.loadUnloadedFallingBlocks; // PaperSpigot
    }

    public EntityFallingBlock(World world, double d0, double d1, double d2, IBlockData iblockdata) {
        this(null, world, d0, d1, d2, iblockdata);
    }

    public EntityFallingBlock(org.bukkit.Location loc, World world, double d0, double d1, double d2, IBlockData iblockdata) {
        super(world);
        sourceLoc = loc;
        this.block = iblockdata;
        this.k = true;
        this.setSize(0.98F, 0.98F);
        this.setPosition(d0, d1, d2);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
        this.loadChunks = world.paperSpigotConfig.loadUnloadedFallingBlocks; // PaperSpigot
    }

    protected boolean s_() {
        return false;
    }

    protected void h() {
    }

    public boolean ad() {
        return !dead;
    }

    public void t_() {
        Block block = this.block.getBlock();
        if (block.getMaterial() == Material.AIR) {
            die();
        } else {
            lastX = locX;
            lastY = locY;
            lastZ = locZ;

            if (ticksLived++ == 0) {
                int x = MathHelper.floor(locX), y = MathHelper.floor(locY), z = MathHelper.floor(locZ);
                if (world.getType(x, y, z).getBlock() == block && !CraftEventFactory.callEntityChangeBlockEvent(this, x, y, z, Blocks.AIR, 0).isCancelled()) {
                    world.setTypeAndData(new BlockPosition(x, y, z), Blocks.AIR.getBlockData(), 3, false, true);
                } else {
                    die();
                    return;
                }
            }

            motY -= 0.03999999910593033D;
            move(motX, motY, motZ);

            if (world.paperSpigotConfig.fallingBlockHeightNerf != 0 && locY > world.paperSpigotConfig.fallingBlockHeightNerf ||
                    inUnloadedChunk && world.paperSpigotConfig.removeUnloadedFallingBlocks) {
                die();
                if (dropItem && world.getGameRules().getBoolean("doEntityDrops"))
                    a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
                return;
            }

            motX *= 0.9800000190734863D;
            motY *= 0.9800000190734863D;
            motZ *= 0.9800000190734863D;

            BlockPosition blockposition = new BlockPosition(this);
            if (onGround) {
                motX *= 0.699999988079071D;
                motZ *= 0.699999988079071D;
                motY *= -0.5D;

                IBlockData currentBlock = world.getType(blockposition);
                if (currentBlock.getBlock() != Blocks.PISTON_EXTENSION) {
                    die();
                    if (!e) {
                        if (world.a(block, blockposition, true, EnumDirection.UP, null, null) && !BlockFalling.canFall(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()) && blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000 && blockposition.getY() >= 0 && blockposition.getY() < 256 && currentBlock != this.block) {
                            if (InvictusConfig.sandEntityChangeBlockEvent && CraftEventFactory.callEntityChangeBlockEvent(this, blockposition, this.block.getBlock(), this.block.getBlock().toLegacyData(this.block)).isCancelled())
                                return;

                            world.setTypeAndData(blockposition, this.block, 3, false, InvictusConfig.optimizeSandMovement);
                            respawn();

                            if (block instanceof BlockFalling)
                                ((BlockFalling) block).a_(this.world, blockposition);

                            if (this.tileEntityData != null && block instanceof IContainer) {
                                TileEntity tileentity = this.world.getTileEntity(blockposition);
                                if (tileentity != null) {
                                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                                    tileentity.b(nbttagcompound);
                                    for (String s : this.tileEntityData.c()) {
                                        NBTBase nbtbase = this.tileEntityData.get(s);
                                        if (!s.equals("x") && !s.equals("y") && !s.equals("z"))
                                            nbttagcompound.set(s, nbtbase.clone());
                                    }
                                    tileentity.a(nbttagcompound);
                                    tileentity.update();
                                }
                            }
                        } else if (dropItem && world.getGameRules().getBoolean("doEntityDrops")) {
                            a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
                        }
                    }
                }

            } else if ((ticksLived > 100 && (blockposition.getY() < 1 || blockposition.getY() > 256)) || ticksLived > 600) {
                if (dropItem && world.getGameRules().getBoolean("doEntityDrops"))
                    a(new ItemStack(block, 1, block.getDropData(this.block)), 0.0F);
                die();
            }
        }

    }

    public void e(float f, float f1) {
        if (this.hurtEntities) {
            int i = MathHelper.f(f - 1.0F);

            if (i > 0) {
                boolean flag = this.block.getBlock() == Blocks.ANVIL;
                DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
                for (Entity en : world.getEntities(this, boundingBox)) {
                    CraftEventFactory.entityDamage = this; // CraftBukkit
                    en.damageEntity(damagesource, Math.min(MathHelper.d(i * this.fallHurtAmount), this.fallHurtMax));
                    CraftEventFactory.entityDamage = null; // CraftBukkit
                }

                if (flag && this.random.nextFloat() < 0.05000000074505806D + i * 0.05D) {
                    int j = this.block.get(BlockAnvil.DAMAGE);
                    ++j;
                    if (j > 2) {
                        this.e = true;
                    } else {
                        this.block = this.block.set(BlockAnvil.DAMAGE, j);
                    }
                }
            }
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        Block block = this.block != null ? this.block.getBlock() : Blocks.AIR;
        MinecraftKey minecraftkey = Block.REGISTRY.c(block);

        nbttagcompound.setString("Block", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setByte("Data", (byte) block.toLegacyData(this.block));
        nbttagcompound.setByte("Time", (byte) this.ticksLived);
        nbttagcompound.setBoolean("DropItem", this.dropItem);
        nbttagcompound.setBoolean("HurtEntities", this.hurtEntities);
        nbttagcompound.setFloat("FallHurtAmount", this.fallHurtAmount);
        nbttagcompound.setInt("FallHurtMax", this.fallHurtMax);
        if (this.tileEntityData != null) {
            nbttagcompound.set("TileEntityData", this.tileEntityData);
        }
        // PaperSpigot start - Add FallingBlock source location API
        if (sourceLoc != null) {
            nbttagcompound.setInt("SourceLoc_x", sourceLoc.getBlockX());
            nbttagcompound.setInt("SourceLoc_y", sourceLoc.getBlockY());
            nbttagcompound.setInt("SourceLoc_z", sourceLoc.getBlockZ());
        }
        // PaperSpigot end
    }

    protected void a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getByte("Data") & 255;

        if (nbttagcompound.hasKeyOfType("Block", 8)) {
            this.block = Block.getByName(nbttagcompound.getString("Block")).fromLegacyData(i);
        } else if (nbttagcompound.hasKeyOfType("TileID", 99)) {
            this.block = Block.getById(nbttagcompound.getInt("TileID")).fromLegacyData(i);
        } else {
            this.block = Block.getById(nbttagcompound.getByte("Tile") & 255).fromLegacyData(i);
        }

        this.ticksLived = nbttagcompound.getByte("Time") & 255;
        Block block = this.block.getBlock();

        if (nbttagcompound.hasKeyOfType("HurtEntities", 99)) {
            this.hurtEntities = nbttagcompound.getBoolean("HurtEntities");
            this.fallHurtAmount = nbttagcompound.getFloat("FallHurtAmount");
            this.fallHurtMax = nbttagcompound.getInt("FallHurtMax");
        } else if (block == Blocks.ANVIL) {
            this.hurtEntities = true;
        }

        if (nbttagcompound.hasKeyOfType("DropItem", 99)) {
            this.dropItem = nbttagcompound.getBoolean("DropItem");
        }

        if (nbttagcompound.hasKeyOfType("TileEntityData", 10)) {
            this.tileEntityData = nbttagcompound.getCompound("TileEntityData");
        }

        if (block == null || block.getMaterial() == Material.AIR) {
            this.block = Blocks.SAND.getBlockData();
        }
        // PaperSpigot start - Add FallingBlock source location API
        if (nbttagcompound.hasKey("SourceLoc_x")) {
            int srcX = nbttagcompound.getInt("SourceLoc_x");
            int srcY = nbttagcompound.getInt("SourceLoc_y");
            int srcZ = nbttagcompound.getInt("SourceLoc_z");
            sourceLoc = new org.bukkit.Location(world.getWorld(), srcX, srcY, srcZ);
        }
        // PaperSpigot end
    }

    public void a(boolean flag) {
        this.hurtEntities = flag;
    }

    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        super.appendEntityCrashDetails(crashreportsystemdetails);
        if (this.block != null) {
            Block block = this.block.getBlock();

            crashreportsystemdetails.a("Immitating block ID", Block.getId(block));
            crashreportsystemdetails.a("Immitating block data", block.toLegacyData(this.block));
        }

    }

    public IBlockData getBlock() {
        return this.block;
    }

    public double f(double d0, double d1, double d2) {
        if (!world.paperSpigotConfig.fixCannons)
            return super.f(d0, d1, d2);
        double d3 = locX - d0;
        double d4 = locY + getHeadHeight() - d1;
        double d5 = locZ - d2;
        return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
    }

    public float getHeadHeight() {
        return world.paperSpigotConfig.fixCannons ? (length / 2.0F) : super.getHeadHeight();
    }

    @Override
    public void move(double d0, double d1, double d2) {
        if (loadChunks)
            loadChunks();
        if (noclip) {
            a(getBoundingBox().c(d0, d1, d2));
            recalcPosition();
        } else {
            lastMotX = motX;
            lastMotY = motY;
            lastMotZ = motZ;

            try {
                checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");
                appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            if (d0 == 0.0D && d1 == 0.0D && d2 == 0.0D && vehicle == null && passenger == null)
                return;

            if (H) {
                H = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                motX = 0.0D;
                motY = 0.0D;
                motZ = 0.0D;
            }

            double d6 = d0;
            double d7 = d1;
            double d8 = d2;

            AxisAlignedBB totalArea = getBoundingBox().a(d0, d1, d2);
            double xLength = totalArea.d - totalArea.a;
            double yLength = totalArea.e - totalArea.b;
            double zLength = totalArea.f - totalArea.c;
            boolean axisScan = InvictusConfig.optimizeTntMovement && (xLength * yLength * zLength > 10.0D);
            List<AxisAlignedBB> list = world.getCubes(this, axisScan ? getBoundingBox().a(0.0D, d1, 0.0D) : totalArea);

            AxisAlignedBB axisalignedbb1;
            for (Iterator<AxisAlignedBB> iterator = list.iterator(); iterator.hasNext(); d1 = axisalignedbb1.b(getBoundingBox(), d1))
                axisalignedbb1 = iterator.next();
            a(getBoundingBox().c(0.0D, d1, 0.0D));

            AxisAlignedBB axisalignedbb2;
            if (InvictusConfig.fixEastWest && Math.abs(d0) > Math.abs(d2)) {
                if (axisScan)
                    list = world.getCubes(this, getBoundingBox().a(0.0D, 0.0D, d2));

                Iterator<AxisAlignedBB> iterator1;
                for (iterator1 = list.iterator(); iterator1.hasNext(); d2 = axisalignedbb2.c(getBoundingBox(), d2))
                    axisalignedbb2 = iterator1.next();
                a(getBoundingBox().c(0.0D, 0.0D, d2));

                if (axisScan)
                    list = world.getCubes(this, getBoundingBox().a(d0, 0.0D, 0.0D));

                for (iterator1 = list.iterator(); iterator1.hasNext(); d0 = axisalignedbb2.a(getBoundingBox(), d0))
                    axisalignedbb2 = iterator1.next();
                a(getBoundingBox().c(d0, 0.0D, 0.0D));
            } else {
                if (axisScan)
                    list = world.getCubes(this, getBoundingBox().a(d0, 0.0D, 0.0D));

                Iterator<AxisAlignedBB> iterator1;
                for (iterator1 = list.iterator(); iterator1.hasNext(); d0 = axisalignedbb2.a(getBoundingBox(), d0))
                    axisalignedbb2 = iterator1.next();
                a(getBoundingBox().c(d0, 0.0D, 0.0D));

                if (axisScan)
                    list = world.getCubes(this, getBoundingBox().a(0.0D, 0.0D, d2));

                for (iterator1 = list.iterator(); iterator1.hasNext(); d2 = axisalignedbb2.c(getBoundingBox(), d2))
                    axisalignedbb2 = iterator1.next();
                a(getBoundingBox().c(0.0D, 0.0D, d2));
            }

            recalcPosition();
            positionChanged = !(d6 == d0 && d8 == d2);
            E = (d7 != d1);
            onGround = (E && d7 < 0.0D);
            F = !(!positionChanged && !E);

            BlockPosition blockposition = new BlockPosition(MathHelper.floor(locX), MathHelper.floor(locY - 0.20000000298023224D), MathHelper.floor(locZ));
            Block block = world.getType(blockposition).getBlock();
            if (block.getMaterial() == Material.AIR) {
                Block block1 = world.getType(blockposition.getX(), blockposition.getY() - 1, blockposition.getZ()).getBlock();
                if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
                    block = block1;
                    blockposition.setY(blockposition.getY() - 1);
                }
            }

            a(d1, onGround, block, blockposition);
            if (d6 != d0)
                motX = 0.0D;
            if (d8 != d2)
                motZ = 0.0D;
            if (d7 != d1)
                block.a(world, this);

        }
    }

}