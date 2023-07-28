package net.minecraft.server;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Explosive;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.Iterator;
import java.util.List;

public class EntityTNTPrimed extends Entity {
    public int fuseTicks;
    public float yield = 4.0F;
    public boolean isIncendiary = false;
    public Location sourceLoc;
    private EntityLiving source;

    public EntityTNTPrimed(World world) {
        this(null, world);
    }

    public EntityTNTPrimed(Location loc, World world) {
        super(world);
        sourceLoc = loc;
        k = true;
        setSize(0.98F, 0.98F);
        loadChunks = world.paperSpigotConfig.loadUnloadedTNTEntities;
    }

    public EntityTNTPrimed(Location loc, World world, double d0, double d1, double d2, EntityLiving entityliving) {
        this(loc, world);
        setPosition(d0, d1, d2);
        if (world.paperSpigotConfig.fixCannons) {
            motX = motZ = 0.0D;
        } else {
            float f = (float) (Math.random() * 3.1415927410125732D * 2.0D);
            motX = (-((float) FastMath.sin(f)) * 0.02F);
            motZ = (-((float) FastMath.cos(f)) * 0.02F);
        }
        motY = 0.20000000298023224D;
        fuseTicks = 80;
        lastX = d0;
        lastY = d1;
        lastZ = d2;
        source = entityliving;
    }

    protected void h() {
    }

    protected boolean s_() {
        return false;
    }

    public boolean ad() {
        return !dead;
    }

    public void t_() {
        lastX = locX;
        lastY = locY;
        lastZ = locZ;

        motY -= 0.03999999910593033D;

        move(motX, motY, motZ);

        if (world.paperSpigotConfig.tntEntityHeightNerf != 0 && locY > world.paperSpigotConfig.tntEntityHeightNerf) {
            die();
        }

        if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedTNTEntities) {
            die();
            fuseTicks = 2;
        }

        motX *= 0.9800000190734863D;
        motY *= 0.9800000190734863D;
        motZ *= 0.9800000190734863D;

        if (onGround) {
            motX *= 0.699999988079071D;
            motZ *= 0.699999988079071D;
            motY *= -0.5D;
        }

        if (fuseTicks-- <= 0) {
            respawn();
            explode();
            die();
        } else {
            W();
            if(InvictusConfig.tntParticles)
                world.addParticle(EnumParticle.SMOKE_NORMAL, locX, locY + 0.5D, locZ, 0.0D, 0.0D, 0.0D, EnumParticle.EMPTY_ARRAY);
        }
    }

    private void explode() {
        ChunkProviderServer chunkProviderServer = (ChunkProviderServer) world.chunkProvider;

        boolean forceChunkLoad = chunkProviderServer.forceChunkLoad;
        if (world.paperSpigotConfig.loadUnloadedTNTEntities) {
            chunkProviderServer.forceChunkLoad = true;
        }

        CraftServer server = world.getServer();
        ExplosionPrimeEvent event = new ExplosionPrimeEvent((Explosive) CraftEntity.getEntity(server, this));
        server.getPluginManager().callEvent(event);

        if (!event.isCancelled())
            world.createExplosion(this, locX, locY + (length / 2.0F), locZ, event.getRadius(), event.getFire(), true);

        if (world.paperSpigotConfig.loadUnloadedTNTEntities)
            chunkProviderServer.forceChunkLoad = forceChunkLoad;
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Fuse", (byte) fuseTicks);
        if (sourceLoc != null) {
            nbttagcompound.setInt("SourceLoc_x", sourceLoc.getBlockX());
            nbttagcompound.setInt("SourceLoc_y", sourceLoc.getBlockY());
            nbttagcompound.setInt("SourceLoc_z", sourceLoc.getBlockZ());
        }
    }

    protected void a(NBTTagCompound nbttagcompound) {
        fuseTicks = nbttagcompound.getByte("Fuse");
        if (nbttagcompound.hasKey("SourceLoc_x")) {
            sourceLoc = new Location(world.getWorld(), nbttagcompound.getInt("SourceLoc_x"), nbttagcompound.getInt("SourceLoc_y"), nbttagcompound.getInt("SourceLoc_z"));
        }
    }

    public EntityLiving getSource() {
        return source;
    }

    @Override
    public double f(double d0, double d1, double d2) {
        double d3 = locX - d0;
        double d4 = locY + getHeadHeight() - d1;
        double d5 = locZ - d2;
        return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
    }

    @Override
    public boolean aL() {
        return !world.paperSpigotConfig.fixCannons && super.aL();
    }

    @Override
    public float getHeadHeight() {
        return world.paperSpigotConfig.fixCannons ? (length / 2.0F) : 0.0F;
    }

    @Override
    public boolean W() {
        return world.paperSpigotConfig.fixCannons ? inWater : super.W();
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
