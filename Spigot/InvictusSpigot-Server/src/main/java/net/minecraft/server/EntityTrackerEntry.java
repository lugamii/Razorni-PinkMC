package net.minecraft.server;

import com.google.common.collect.Lists;
import eu.vortexdev.invictusspigot.config.InvictusConfig;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EntityTrackerEntry {

    public final Entity tracker;
    public final Set<EntityPlayer> trackedPlayers = new LinkedHashSet<>(); // Vortex
    protected final boolean u;
    public int b;
    public int c;
    public int xLoc;
    public int yLoc;
    public int zLoc;
    public int yRot;
    public int xRot;
    public int i;
    public double j;
    public double k;
    public double l;
    public int m;
    public boolean n;
    protected double q;
    protected double r;
    protected double s;
    protected boolean isMoving;
    protected int v;
    protected Entity w;
    protected boolean x;
    protected boolean y;

    public EntityTrackerEntry(Entity entity, int b, int c, boolean flag) {
        tracker = entity;
        this.b = b;
        this.c = c;
        this.u = flag;
        this.xLoc = MathHelper.floor(entity.locX * 32.0D);
        this.yLoc = MathHelper.floor(entity.locY * 32.0D);
        this.zLoc = MathHelper.floor(entity.locZ * 32.0D);
        this.yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
        this.xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
        this.i = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
    }

    public void track(List<EntityHuman> list) {
        if (this.w != tracker.vehicle || tracker.vehicle != null && this.m % 60 == 0) {
            this.w = tracker.vehicle;
            this.broadcast(new PacketPlayOutAttachEntity(0, tracker, tracker.vehicle));
        }

        if (tracker instanceof EntityItemFrame && this.m % 10 == 0) { // CraftBukkit - Moved below, should always enter this block
            ItemStack itemstack = ((EntityItemFrame) tracker).getItem();
            if (itemstack != null && itemstack.getItem() instanceof ItemWorldMap) { // CraftBukkit - Moved this.m % 10 logic here so item frames do not enter the other blocks
                WorldMap worldmap = Items.FILLED_MAP.getSavedMap(itemstack, tracker.world);

                // CraftBukkit
                for (EntityPlayer trackedPlayer : trackedPlayers) {

                    worldmap.a(trackedPlayer, itemstack);
                    Packet<?> packet = Items.FILLED_MAP.c(itemstack, tracker.world, trackedPlayer);

                    if (packet != null) {
                        trackedPlayer.playerConnection.sendPacket(packet);
                    }
                }
            }

            this.b();
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
                boolean flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4, arrow = tracker instanceof EntityArrow;

                if (this.m > 0 || arrow) { // PaperSpigot - Moved up
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
                        if (!flag || !flag1 && !arrow) {
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

            if (tracker instanceof EntityLiving) {
                i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F);
                if (Math.abs(i - this.i) >= 4) {
                    this.broadcast(new PacketPlayOutEntityHeadRotation(tracker, (byte) i));
                    this.i = i;
                }
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
        if (tracker.ticksLived % tracker.getAddRemoveDelay() == 0) {
            removePlayers();
            if (b > 0)
                addPlayers();
        }
        track(null);
    }

    public void removePlayers() {
        List<EntityPlayer> remove = Lists.newArrayList();

        for (EntityPlayer entityPlayer : trackedPlayers) {
            final double x = entityPlayer.locX - tracker.locX, z = entityPlayer.locZ - tracker.locZ;
            if (!(x >= -b && x <= b && z >= -b && z <= b)) {
                entityPlayer.d(tracker);
                remove.add(entityPlayer);
            }
        }

        remove.forEach(trackedPlayers::remove);
    }

    public void addPlayers() {
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

        if (datawatcher.a()) {
            broadcastIncludingSelf(new PacketPlayOutEntityMetadata(tracker.getId(), datawatcher, false));
        }

        if (tracker instanceof EntityLiving) {
            Set<AttributeInstance> set = ((AttributeMapServer) ((EntityLiving) tracker).getAttributeMap()).getAttributes();

            if (!set.isEmpty()) {
                // CraftBukkit start - Send scaled max health
                if (tracker instanceof EntityPlayer) {
                    ((EntityPlayer) tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
                }
                // CraftBukkit end
                this.broadcastIncludingSelf(new PacketPlayOutUpdateAttributes(tracker.getId(), set));
            }

            set.clear();
        }
    }

    public void broadcast(Packet<?> packet) {
        for (EntityPlayer p : trackedPlayers) {
            p.playerConnection.sendPacket(packet);
        }
    }

    public void broadcastIncludingSelf(Packet<?> packet) {
        this.broadcast(packet);
        if (tracker instanceof EntityPlayer) {
            ((EntityPlayer) tracker).playerConnection.sendPacket(packet);
        }
    }

    public void a() {
        for (EntityPlayer p : trackedPlayers) {
            p.d(tracker);
        }
    }

    public void updatePlayer(EntityPlayer entityplayer) {
        if (c(entityplayer)) {
            if (!trackedPlayers.contains(entityplayer) && (e(entityplayer) || tracker.attachedToPlayer)) {
                if (!entityplayer.getBukkitEntity().canSeeEntity(tracker.getBukkitEntity()))
                    return;

                PlayerConnection playerConnection = entityplayer.playerConnection;
                trackedPlayers.add(entityplayer);

                Packet<?> packet = c();

                if (packet == null)  // Vortex - Don't send packet if the entity is died
                    return;

                playerConnection.sendPacket(packet);

                if (tracker instanceof EntityPlayer) {
                    EntityHuman entityhuman = (EntityHuman) tracker;
                    if (entityhuman.isSleeping()) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutBed(entityhuman, new BlockPosition(tracker)));
                    }
                }

                int id = tracker.getId();

                if (!tracker.getDataWatcher().d()) {
                    PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(id, tracker.getDataWatcher(), true);
                    if (doHealthObfuscation()) {
                        metadataPacket.obfuscateHealth();
                    }
                    playerConnection.sendPacket(metadataPacket);
                }

                NBTTagCompound nbttagcompound = tracker.getNBTTag();

                if (nbttagcompound != null)
                    playerConnection.sendPacket(new PacketPlayOutUpdateEntityNBT(id, nbttagcompound));

                this.j = tracker.motX;
                this.k = tracker.motY;
                this.l = tracker.motZ;

                if (this.u && !(packet instanceof PacketPlayOutSpawnEntityLiving))
                    playerConnection.sendPacket(new PacketPlayOutEntityVelocity(id, tracker.motX, tracker.motY, tracker.motZ));

                if (tracker instanceof EntityLiving) {
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

                    if (tracker instanceof EntityInsentient && ((EntityInsentient) tracker).getLeashHolder() != null)
                        playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, tracker, ((EntityInsentient) tracker).getLeashHolder()));

                    broadcast(new PacketPlayOutEntityHeadRotation(tracker, (byte) (this.i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F))));
                }

                if (tracker.vehicle != null)
                    playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, tracker, tracker.vehicle));
            }
        } else if (trackedPlayers.contains(entityplayer)) {
            trackedPlayers.remove(entityplayer);
            entityplayer.d(tracker);
        }
    }

    protected boolean e(EntityPlayer entityplayer) {
        return entityplayer.u().getPlayerChunkMap().a(entityplayer, tracker.ae, tracker.ag);
    }

    protected Packet<PacketListenerPlayOut> c() {
        if (tracker.dead) {
            return null;
        }
        if (tracker instanceof EntityItem) {
            return new PacketPlayOutSpawnEntity(tracker, 2, 1);
        } else if (tracker instanceof EntityFallingBlock) {
            EntityFallingBlock entityfallingblock = (EntityFallingBlock) tracker;
            return new PacketPlayOutSpawnEntity(tracker, 70, Block.getCombinedId(entityfallingblock.getBlock()));
        } else if (tracker instanceof EntityPlayer) {
            return new PacketPlayOutNamedEntitySpawn((EntityHuman) tracker);
        } else if (tracker instanceof EntityTNTPrimed) {
            return new PacketPlayOutSpawnEntity(tracker, 50);
        } else if (tracker instanceof EntityPotion) {
            return new PacketPlayOutSpawnEntity(tracker, 73, ((EntityPotion) tracker).getPotionValue());
        } else if (tracker instanceof EntityArrow) {
            Entity entity = ((EntityArrow) tracker).shooter;
            return new PacketPlayOutSpawnEntity(tracker, 60, entity != null ? entity.getId() : tracker.getId());
        } else if (tracker instanceof EntityFishingHook) {
            EntityHuman entityhuman = ((EntityFishingHook) tracker).owner;
            return new PacketPlayOutSpawnEntity(tracker, 90, entityhuman != null ? entityhuman.getId() : tracker.getId());
        } else if (tracker instanceof EntityEnderPearl) {
            return new PacketPlayOutSpawnEntity(tracker, 65);
        } else if (tracker instanceof EntityMinecartAbstract) {
            EntityMinecartAbstract entityminecartabstract = (EntityMinecartAbstract) tracker;
            return new PacketPlayOutSpawnEntity(tracker, 10, entityminecartabstract.s().a());
        } else if (tracker instanceof EntityBoat) {
            return new PacketPlayOutSpawnEntity(tracker, 1);
        } else if (tracker instanceof IAnimal) {
            this.i = MathHelper.d(tracker.getHeadRotation() * 256.0F / 360.0F);
            return new PacketPlayOutSpawnEntityLiving((EntityLiving) tracker);
        } else if (tracker instanceof EntitySnowball) {
            return new PacketPlayOutSpawnEntity(tracker, 61);
        } else if (tracker instanceof EntityThrownExpBottle) {
            return new PacketPlayOutSpawnEntity(tracker, 75);
        } else if (tracker instanceof EntityEnderSignal) {
            return new PacketPlayOutSpawnEntity(tracker, 72);
        } else if (tracker instanceof EntityFireworks) {
            return new PacketPlayOutSpawnEntity(tracker, 76);
        } else {
            PacketPlayOutSpawnEntity packetplayoutspawnentity;

            if (tracker instanceof EntityFireball) {
                EntityFireball entityfireball = (EntityFireball) tracker;

                byte b0 = 63;

                if (tracker instanceof EntitySmallFireball) {
                    b0 = 64;
                } else if (tracker instanceof EntityWitherSkull) {
                    b0 = 66;
                }

                if (entityfireball.shooter != null) {
                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, b0, ((EntityFireball) tracker).shooter.getId());
                } else {
                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, b0, 0);
                }

                packetplayoutspawnentity.d((int) (entityfireball.dirX * 8000.0D));
                packetplayoutspawnentity.e((int) (entityfireball.dirY * 8000.0D));
                packetplayoutspawnentity.f((int) (entityfireball.dirZ * 8000.0D));
                return packetplayoutspawnentity;
            } else if (tracker instanceof EntityEgg) {
                return new PacketPlayOutSpawnEntity(tracker, 62);
            } else if (tracker instanceof EntityEnderCrystal) {
                return new PacketPlayOutSpawnEntity(tracker, 51);
            } else if (tracker instanceof EntityArmorStand) {
                return new PacketPlayOutSpawnEntity(tracker, 78);
            } else if (tracker instanceof EntityPainting) {
                return new PacketPlayOutSpawnEntityPainting((EntityPainting) tracker);
            } else {
                BlockPosition blockposition;

                if (tracker instanceof EntityItemFrame) {
                    EntityItemFrame entityitemframe = (EntityItemFrame) tracker;

                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, 71, entityitemframe.direction.b());
                    blockposition = entityitemframe.getBlockPosition();
                    packetplayoutspawnentity.a(MathHelper.d((float) (blockposition.getX() * 32)));
                    packetplayoutspawnentity.b(MathHelper.d((float) (blockposition.getY() * 32)));
                    packetplayoutspawnentity.c(MathHelper.d((float) (blockposition.getZ() * 32)));
                    return packetplayoutspawnentity;
                } else if (tracker instanceof EntityLeash) {
                    EntityLeash entityleash = (EntityLeash) tracker;

                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(tracker, 77);
                    blockposition = entityleash.getBlockPosition();
                    packetplayoutspawnentity.a(MathHelper.d((float) (blockposition.getX() * 32)));
                    packetplayoutspawnentity.b(MathHelper.d((float) (blockposition.getY() * 32)));
                    packetplayoutspawnentity.c(MathHelper.d((float) (blockposition.getZ() * 32)));
                    return packetplayoutspawnentity;
                } else if (tracker instanceof EntityExperienceOrb) {
                    return new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb) tracker);
                } else {
                    throw new IllegalArgumentException("Don't know how to add " + tracker.getClass() + "!");
                }
            }
        }
    }

    public boolean c(EntityPlayer entityplayer) {
        double d0 = entityplayer.locX - tracker.locX;
        double d1 = entityplayer.locZ - tracker.locZ;
        return d0 >= -b && d0 <= b && d1 >= -b && d1 <= b && tracker.a(entityplayer);
    }

    public void clear(EntityPlayer entityplayer) {
        if (trackedPlayers.remove(entityplayer)) {
            entityplayer.d(tracker);
        }
    }

    public boolean doHealthObfuscation() {
        return tracker.isAlive() && tracker instanceof EntityPlayer;
    }
}
