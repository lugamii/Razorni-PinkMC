package net.minecraft.server;

import com.google.common.collect.Lists;
import eu.vortexdev.invictusspigot.config.InvictusConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PlayerTrackerEntry extends EntityTrackerEntry {

    private final EntityTracker entityTracker;
    private boolean withinNoTrack = false;

    public PlayerTrackerEntry(EntityTracker entityTracker, Entity entity, int b, int c, boolean flag) {
        super(entity, b, c, flag);
        this.entityTracker = entityTracker;
    }

    public void track(List<EntityHuman> list) {
        if (this.w != tracker.vehicle || tracker.vehicle != null && this.m % 60 == 0) {
            this.w = tracker.vehicle;
            this.broadcast(new PacketPlayOutAttachEntity(0, tracker, tracker.vehicle));
        }

        if (this.m % this.c == 0 || tracker.ai || tracker.getDataWatcher().a()) {
            int i;
            int j;

            if (tracker.vehicle == null) {
                ++this.v;
                i = MathHelper.floor(tracker.locX * 32.0D);
                j = MathHelper.floor(tracker.locY * 32.0D);
                int k = MathHelper.floor(tracker.locZ * 32.0D);
                int l = MathHelper.d(tracker.yaw * 256.0F / 360.0F);
                int i1 = MathHelper.d(tracker.pitch * 256.0F / 360.0F);
                int j1 = i - this.xLoc;
                int k1 = j - this.yLoc;
                int l1 = k - this.zLoc;
                Packet<PacketListenerPlayOut> object = null;
                boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.m % 60 == 0;
                boolean flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4;

                if (this.m > 0) { // PaperSpigot - Moved up
                    // CraftBukkit start - Code moved from below
                    if (flag) {
                        this.xLoc = i;
                        this.yLoc = j;
                        this.zLoc = k;
                    }

                    if (flag1) {
                        this.yRot = l;
                        this.xRot = i1;
                    }
                    // CraftBukkit end

                    if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128 && this.v <= 150 && !this.x && this.y == tracker.onGround) {
                        if (!flag || !flag1) {
                            if (flag) {
                                object = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(tracker.getId(), (byte) j1, (byte) k1, (byte) l1, tracker.onGround);
                            } else if (flag1) {
                                object = new PacketPlayOutEntity.PacketPlayOutEntityLook(tracker.getId(), (byte) l, (byte) i1, tracker.onGround);
                            }
                        } else {
                            object = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(tracker.getId(), (byte) j1, (byte) k1, (byte) l1, (byte) l, (byte) i1, tracker.onGround);
                        }
                    } else {
                        this.y = tracker.onGround;
                        this.v = 0;
                        object = new PacketPlayOutEntityTeleport(tracker.getId(), i, j, k, (byte) l, (byte) i1, tracker.onGround);
                    }
                }

                if (this.u) {
                    double d0 = tracker.motX - this.j;
                    double d1 = tracker.motY - this.k;
                    double d2 = tracker.motZ - this.l;
                    double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                    if (d4 > 0.0004 || d4 > 0.0D && tracker.motX == 0.0D && tracker.motY == 0.0D && tracker.motZ == 0.0D) {
                        this.j = tracker.motX;
                        this.k = tracker.motY;
                        this.l = tracker.motZ;
                        this.broadcast(new PacketPlayOutEntityVelocity(tracker.getId(), this.j, this.k, this.l));
                    }
                }

                if (object != null) {
                    this.broadcast(object);
                }

                this.b();

                this.x = false;
            } else {
                i = MathHelper.d(tracker.yaw * 256.0F / 360.0F);
                j = MathHelper.d(tracker.pitch * 256.0F / 360.0F);
                if (Math.abs(i - this.yRot) >= 4 || Math.abs(j - this.xRot) >= 4) {
                    this.broadcast(new PacketPlayOutEntity.PacketPlayOutEntityLook(tracker.getId(), (byte) i, (byte) j, tracker.onGround));
                    this.yRot = i;
                    this.xRot = j;
                }

                this.xLoc = MathHelper.floor(tracker.locX * 32.0D);
                this.yLoc = MathHelper.floor(tracker.locY * 32.0D);
                this.zLoc = MathHelper.floor(tracker.locZ * 32.0D);
                this.b();
                this.x = true;
            }

            i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F);
            if (Math.abs(i - this.i) >= 4) {
                this.broadcast(new PacketPlayOutEntityHeadRotation(tracker, (byte) i));
                this.i = i;
            }

            tracker.ai = false;
        }

        ++this.m;
        if (tracker.velocityChanged) {
            broadcastIncludingSelf(new PacketPlayOutEntityVelocity(tracker));
            tracker.velocityChanged = false;
        }
    }

    public void update() {
        withinNoTrack = withinNoTrack();
        if (tracker.ticksLived % tracker.getAddRemoveDelay() == 0) {
            removePlayers();
            if (b > 0)
                addPlayers();
        }
        track(null);
    }

    public void removePlayers() {
        List<EntityPlayer> remove = Lists.newArrayList();

        if (!withinNoTrack) {
            for (EntityPlayer entityPlayer : trackedPlayers) {
                final double x = entityPlayer.locX - tracker.locX, z = entityPlayer.locZ - tracker.locZ;
                if (!(x >= -b && x <= b && z >= -b && z <= b)) {
                    remove.add(entityPlayer);
                }
            }
        } else {
            remove.addAll(trackedPlayers);
        }

        remove.forEach(player -> {
            player.d(tracker);
            trackedPlayers.remove(player);
        });
    }

    public void addPlayers() {
        if(withinNoTrack)
            return;
        final int minX = MathHelper.floor(tracker.locX - b) >> 4, maxX = MathHelper.floor(tracker.locX + b) >> 4, minZ = MathHelper.floor(tracker.locZ - b) >> 4, maxZ = MathHelper.floor(tracker.locZ + b) >> 4, minY = MathHelper.clamp(MathHelper.floor(tracker.locY - b) >> 4, 0, 15), maxY = MathHelper.clamp(MathHelper.floor(tracker.locY + b) >> 4, 0, 15);
        for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
                Chunk chunk = tracker.world.getChunkIfLoaded(chunkX, chunkZ);
                if (chunk != null) {
                    for (int chunkY = minY; chunkY <= maxY; chunkY++) {
                        List<Entity> slices = chunk.entitySlices[chunkY];
                        for (Entity entity : slices) {
                            if (entity instanceof EntityPlayer && entity != tracker) {
                                updatePlayer((EntityPlayer) entity);
                            }
                        }
                    }
                }
            }
        }
    }

    private void b() {
        DataWatcher datawatcher = tracker.getDataWatcher();

        // mSpigot patch for nametag health visibility
        if (datawatcher.a()) {
            List<DataWatcher.WatchableObject> changedMetadata = datawatcher.b();
            if (tracker.isAlive()) {
                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(tracker.getId(), new ArrayList<>(changedMetadata), false).obfuscateHealth();
                if (!metadataPacket.didFindHealth() || 1 < metadataPacket.getData().size())
                    broadcast(metadataPacket);
                if(((EntityPlayer)tracker).playerConnection != null)
                    ((EntityPlayer) tracker).playerConnection.sendPacket(new PacketPlayOutEntityMetadata(tracker.getId(), changedMetadata, false));
            } else {
                broadcastIncludingSelf(new PacketPlayOutEntityMetadata(tracker.getId(), changedMetadata, false));
            }
        }
        Set<AttributeInstance> set = ((AttributeMapServer) ((EntityLiving) tracker).getAttributeMap()).getAttributes();

        if (!set.isEmpty()) {
            // CraftBukkit start - Send scaled max health
            ((EntityPlayer) tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
            // CraftBukkit end
            this.broadcastIncludingSelf(new PacketPlayOutUpdateAttributes(tracker.getId(), set));
        }

        set.clear();
    }

    public void updatePlayer(EntityPlayer entityplayer) {
        boolean isTracked = trackedPlayers.contains(entityplayer);  // Vortex - Only one "contains"
        double d0 = entityplayer.locX - tracker.locX, d1 = entityplayer.locZ - tracker.locZ;
        if (d0 >= -b && d0 <= b && d1 >= -b && d1 <= b) {
            if (!isTracked && e(entityplayer)) {
                EntityPlayer player = (EntityPlayer) tracker;

                if (withinNoTrack() || !entityplayer.getBukkitEntity().canSee(player.getBukkitEntity()))
                    return;

                PlayerConnection playerConnection = entityplayer.playerConnection;
                trackedPlayers.add(entityplayer);

                Packet<?> packet = !tracker.dead ? new PacketPlayOutNamedEntitySpawn(player) : null;

                if (packet == null)  // Vortex - Don't send packet if the entity is died
                    return;

                playerConnection.sendPacket(packet);

                if (player.isSleeping()) {
                    entityplayer.playerConnection.sendPacket(new PacketPlayOutBed(player, new BlockPosition(tracker)));
                }

                int id = tracker.getId();

                if (!tracker.getDataWatcher().d()) {
                    playerConnection.sendPacket(new PacketPlayOutEntityMetadata(id, tracker.getDataWatcher(), true).obfuscateHealth());
                }

                NBTTagCompound nbttagcompound = tracker.getNBTTag();

                if (nbttagcompound != null)
                    playerConnection.sendPacket(new PacketPlayOutUpdateEntityNBT(id, nbttagcompound));

                j = tracker.motX;
                k = tracker.motY;
                l = tracker.motZ;

                if (u)
                    playerConnection.sendPacket(new PacketPlayOutEntityVelocity(id, tracker.motX, tracker.motY, tracker.motZ));

                EntityLiving entityliving = (EntityLiving) tracker;
                Collection<AttributeInstance> collection = ((AttributeMapServer) entityliving.getAttributeMap()).c();

                if (id == entityplayer.getId())
                    ((EntityPlayer) tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);

                if (!collection.isEmpty())
                    playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(id, collection));

                for (int i = 0; i < 5; i++) {
                    ItemStack itemstack = entityliving.getEquipment(i);
                    if (itemstack != null)
                        playerConnection.sendPacket(new PacketPlayOutEntityEquipment(id, i, itemstack, InvictusConfig.patchArmorNametags));
                }

                for (MobEffect effect : entityliving.getEffects())
                    playerConnection.sendPacket(new PacketPlayOutEntityEffect(id, effect));

                broadcast(new PacketPlayOutEntityHeadRotation(tracker, (byte) (this.i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F))));

                if (tracker.vehicle != null)
                    playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, tracker, tracker.vehicle));
            }
        } else if (isTracked) {
            this.trackedPlayers.remove(entityplayer);
            entityplayer.d(tracker);
        }
    }

    private boolean withinNoTrack() {
        double xDistSqrd = tracker.locX * tracker.locX;
        double zDistSqrd = tracker.locZ * tracker.locZ;
        int noTrackDistanceSqrd = entityTracker.getNoTrackDistance() * entityTracker.getNoTrackDistance();
        return noTrackDistanceSqrd != 0 && xDistSqrd <= noTrackDistanceSqrd && zDistSqrd <= noTrackDistanceSqrd;
    }

    public void broadcastIncludingSelf(Packet<?> packet) {
        this.broadcast(packet);
        ((EntityPlayer) tracker).playerConnection.sendPacket(packet);
    }
}
