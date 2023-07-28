package net.minecraft.server;

import eu.vortexdev.invictusspigot.InvictusSpigot;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import eu.vortexdev.invictusspigot.util.java.LinkedArraySet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.TrackingRange;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class EntityTracker {
    private static final Logger a = LogManager.getLogger();

    private final Set<EntityTrackerEntry> c = new LinkedArraySet<>();

    public IntHashMap<EntityTrackerEntry> trackedEntities = new IntHashMap<>();

    private final int e;
    private int noTrackDistance = 0;

    public EntityTracker(WorldServer worldserver) {
        this.e = worldserver.getMinecraftServer().getPlayerList().d();
    }

    public void track(Entity entity) {
        if (entity instanceof EntityPlayer) {
            addEntity(entity, 512, 2);
        } else if (entity instanceof EntityFishingHook) {
            addEntity(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            addEntity(entity, 64, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntityFireball) {
            addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntitySnowball) {
            addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderPearl) {
            addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderSignal) {
            addEntity(entity, 64, 4, true);
        } else if (entity instanceof EntityEgg) {
            addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityPotion) {
            addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityThrownExpBottle) {
            addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityFireworks) {
            addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            addEntity(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecartAbstract) {
            addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityBoat) {
            addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntitySquid) {
            addEntity(entity, 64, 3, true);
        } else if (entity instanceof EntityWither) {
            addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityBat) {
            addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityEnderDragon) {
            addEntity(entity, 160, 3, true);
        } else if (entity instanceof IAnimal) {
            addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            addEntity(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityHanging) {
            addEntity(entity, 160, 2147483647, false);
        } else if (entity instanceof EntityArmorStand) {
            addEntity(entity, 160, 3, true);
        } else if (entity instanceof EntityExperienceOrb) {
            addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityEnderCrystal) {
            addEntity(entity, 256, 2147483647, false);
        }
    }

    public void addEntity(Entity entity, int i, int j) {
        addEntity(entity, i, j, false);
    }

    public void addEntity(Entity entity, int i, int j, boolean flag) {
        if (InvictusConfig.hideSand && entity instanceof EntityFallingBlock || InvictusConfig.hideTnt && entity instanceof EntityTNTPrimed) {
            return;
        }

        AsyncCatcher.catchOp("entity track");
        i = TrackingRange.getEntityTrackingRange(entity, i);
        if (i > this.e)
            i = this.e;
        try {
            if (this.trackedEntities.b(entity.getId()))
                throw new IllegalStateException("Entity is already tracked!");
            EntityTrackerEntry entitytrackerentry = entity.getEntry(this, i, j, flag);
            this.c.add(entitytrackerentry);
            this.trackedEntities.a(entity.getId(), entitytrackerentry);
            entitytrackerentry.addPlayers();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Adding entity to track");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity To Track");
            crashreportsystemdetails.a("Tracking range", i + " blocks");
            final int finalI = i;
            crashreportsystemdetails.a("Update interval", () -> {
                String s = "Once per " + finalI + " ticks";
                if (finalI == Integer.MAX_VALUE)
                    s = "Maximum (" + s + ")";
                return s;
            });
            entity.appendEntityCrashDetails(crashreportsystemdetails);
            CrashReportSystemDetails crashreportsystemdetails1 = crashreport.a("Entity That Is Already Tracked");
            this.trackedEntities.get(entity.getId()).tracker.appendEntityCrashDetails(crashreportsystemdetails1);
            try {
                throw new ReportedException(crashreport);
            } catch (ReportedException reportedexception) {
                a.error("\"Silently\" catching entity tracking error.", reportedexception);
            }
        }
    }

    public void untrackEntity(Entity entity) {
        AsyncCatcher.catchOp("entity untrack");
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            for (EntityTrackerEntry entitytrackerentry : this.c) {
                entitytrackerentry.clear(entityplayer);
            }
        }
        EntityTrackerEntry entitytrackerentry1 = this.trackedEntities.d(entity.getId());
        if (entitytrackerentry1 != null) {
            this.c.remove(entitytrackerentry1);
            entitytrackerentry1.a();
        }
    }

    public void updatePlayers() {
        int offset = 0;
        LinkedArraySet<EntityTrackerEntry> c = (LinkedArraySet<EntityTrackerEntry>) this.c;
        CountDownLatch latch = new CountDownLatch(4);
        ExecutorService pool = InvictusSpigot.INSTANCE.getThreadingManager().getTrackerThreadPool();
        for (int i = 1; i <= 4; i++) {
            final int localOffset = offset++;
            Runnable runnable = () -> {
                for (int i1 = localOffset; i1 < c.size(); i1 += 4) {
                    c.get(i1).update();
                }
                latch.countDown();
            };
            if (i < 4)
                pool.execute(runnable); else runnable.run();
        }
        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void a(EntityPlayer entityplayer) { // void B() EntityPlayer
    }

    public void a(EntityPlayer entityplayer, Chunk chunk) {
        for (EntityTrackerEntry entitytrackerentry : this.c) {
            if (entitytrackerentry.tracker != entityplayer && entitytrackerentry.tracker.ae == chunk.locX && entitytrackerentry.tracker.ag == chunk.locZ)
                entitytrackerentry.updatePlayer(entityplayer);
        }
    }

    public void a(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
        if (entitytrackerentry != null)
            entitytrackerentry.broadcast(packet);
    }

    public void sendPacketToEntity(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
        if (entitytrackerentry != null)
            entitytrackerentry.broadcastIncludingSelf(packet);
    }

    public void untrackPlayer(EntityPlayer entityplayer) {
        for (EntityTrackerEntry entitytrackerentry : this.c) {
            entitytrackerentry.clear(entityplayer);
        }
    }

    public int getNoTrackDistance() {
        return noTrackDistance;
    }

    public void setNoTrackDistance(int noTrackDistance) {
        this.noTrackDistance = noTrackDistance;
    }

}
