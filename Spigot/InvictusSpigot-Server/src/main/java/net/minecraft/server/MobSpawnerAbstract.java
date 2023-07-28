package net.minecraft.server;

import com.google.common.collect.Lists;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.util.List;

public abstract class MobSpawnerAbstract {

    private final List<MobSpawnerAbstract.a> mobs = Lists.newArrayList();
    public int spawnDelay = 20;
    private String mobName = "Pig";
    private MobSpawnerAbstract.a spawnData;

    private int tickDelay = 0; // PaperSpigot

    public MobSpawnerAbstract() {
    }

    public String getMobName() {
        if (i() == null) {
            if (mobName == null)
                mobName = "Pig";
            else if (mobName.equals("Minecart"))
                mobName = "MinecartRideable";
            return mobName;
        }
        return i().d;
    }

    public void setMobName(String s) {
        this.mobName = s;
    }

    private boolean g() {
        BlockPosition blockposition = b();
        return a().isPlayerNearbyWhoAffectsSpawning(blockposition.getX() + 0.5D, blockposition.getY() + 0.5D, blockposition.getZ() + 0.5D, InvictusConfig.requiredPlayersNearbyRange);
    }

    public void c() {
        // PaperSpigot start - Configurable mob spawner tick rate
        if (spawnDelay > 0 && --tickDelay > 0)
            return;
        World world = a();

        tickDelay = world.paperSpigotConfig.mobSpawnerTickRate;
        // PaperSpigot end

        BlockPosition blockposition = b();
        if (!InvictusConfig.requiredPlayersNearby || g()) {
            if (spawnDelay < -tickDelay) { // PaperSpigot
                h();
            }

            if (spawnDelay > 0) {
                spawnDelay -= tickDelay; // PaperSpigot
                return;
            }

            boolean spawned = false;

            for (int i = 0; i < InvictusConfig.spawnCount; ++i) {

                if (InvictusConfig.chunkMobLimit > 0) {
                    // Mob Limit Check
                    Chunk chunk = new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ()).getChunk();
                    int amount = 0;
                    for (org.bukkit.entity.Entity e : chunk.getEntities()) {
                        if (e instanceof LivingEntity && !e.isDead()) {
                            amount++;
                        }
                    }
                    if (amount > InvictusConfig.chunkMobLimit) {
                        return;
                    }
                }

                Entity entity = EntityTypes.createEntityByName(getMobName(), world);

                if (entity == null) {
                    return;
                }

                if (!InvictusConfig.disableMaxNearbyEntities) {
                    double range = InvictusConfig.spawnRange;
                    int entityCount = world.a(entity.getClass(), new AxisAlignedBB(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition.getX() + 1, blockposition.getY() + 1, blockposition.getZ() + 1).grow(range, range, range)).size();
                    if (entityCount >= InvictusConfig.maxNearbyEntities) {
                        h();
                        return;
                    }
                }

                double locX;
                double locY;
                double locZ;

                if (InvictusConfig.disableRandomSpawnLocations) {
                    locX = blockposition.getX() + (world.random.nextInt(2) * 1.0D - 0.5D) * InvictusConfig.spawnRange + 0.5D;
                    locY = (blockposition.getY() + world.random.nextInt(3) - 1);
                    locZ = blockposition.getZ() + (world.random.nextInt(2) * 1.0D - 0.5D) * InvictusConfig.spawnRange + 0.5D;
                } else {
                    locX = blockposition.getX() + (world.random.nextDouble() - world.random.nextDouble()) * InvictusConfig.spawnRange + 0.5D;
                    locY = (blockposition.getY() + world.random.nextInt(3) - 1);
                    locZ = blockposition.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * InvictusConfig.spawnRange + 0.5D;
                }

                EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;

                entity.fromMobSpawner = true;
                entity.setPosition(locX, locY, locZ);

                if (entityinsentient == null || (InvictusConfig.disableLightLevelsCheck || entityinsentient.bR())) {
                    if (entityinsentient != null && !entityinsentient.canSpawn()) {
                        entityinsentient.setPosition(entityinsentient.locX, entityinsentient.locY + 1.0D, entityinsentient.locZ);
                        if (!entityinsentient.canSpawn())
                            continue;
                    }
                    a(entity, true);
                    if (InvictusConfig.spawnerParticles) {
                        world.triggerEffect(2004, blockposition, 0);
                        if (entityinsentient != null)
                            entityinsentient.y();
                    }
                    spawned = true;
                }
            }

            if (spawned) {
                h();
            }
        }
    }

    private Entity a(Entity entity, boolean flag) {
        MobSpawnerAbstract.a spawnData = i();
        if (spawnData != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.d(nbttagcompound);

            for (String s : spawnData.c.c()) {
                nbttagcompound.set(s, spawnData.c.get(s).clone());
            }

            entity.f(nbttagcompound);

            if (entity.world != null) {
                SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, b().getX(), b().getY(), b().getZ());
                if (!event.isCancelled()) {
                    entity.fromMobSpawner = true;
                    entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER);
                }
            }

            NBTTagCompound nbttagcompound1;

            for (Entity entity1 = entity; nbttagcompound.hasKeyOfType("Riding", 10); nbttagcompound = nbttagcompound1) {
                nbttagcompound1 = nbttagcompound.getCompound("Riding");
                Entity entity2 = EntityTypes.createEntityByName(nbttagcompound1.getString("id"), entity.world);

                if (entity2 != null) {
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                    entity2.d(nbttagcompound2);

                    for (String s1 : nbttagcompound1.c()) {
                        NBTBase nbtbase1 = nbttagcompound1.get(s1);

                        nbttagcompound2.set(s1, nbtbase1.clone());
                    }

                    entity2.f(nbttagcompound2);
                    entity2.setPositionRotation(entity1.locX, entity1.locY, entity1.locZ, entity1.yaw, entity1.pitch);
                    // CraftBukkit start - call SpawnerSpawnEvent, skip if cancelled
                    SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity2, b().getX(), b().getY(), b().getZ());
                    if (event.isCancelled()) {
                        continue;
                    }
                    if (entity.world != null) {
                        entity.world.addEntity(entity2, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
                    }

                    entity1.mount(entity2);
                }

                entity1 = entity2;
            }
        } else if (entity instanceof EntityLiving && entity.world != null) {
            if (entity instanceof EntityInsentient) {
                ((EntityInsentient) entity).prepare(entity.world.E(MathHelper.floor(entity.locX), MathHelper.floor(entity.locY), MathHelper.floor(entity.locZ)), null);
            }
            SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, b().getX(), b().getY(), b().getZ());
            if (!event.isCancelled()) {
                entity.fromMobSpawner = true;
                entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER);
            }
        }

        return entity;
    }

    private void h() {
        if (InvictusConfig.maxSpawnDelay <= InvictusConfig.minSpawnDelay) {
            spawnDelay = InvictusConfig.minSpawnDelay;
        } else {
            spawnDelay = InvictusConfig.minSpawnDelay + a().random.nextInt(InvictusConfig.maxSpawnDelay - InvictusConfig.minSpawnDelay);
        }
        if (mobs.size() > 0)
            a(WeightedRandom.a(a().random, mobs));
        a(1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        mobName = nbttagcompound.getString("EntityId");
        spawnDelay = nbttagcompound.getShort("Delay");

        mobs.clear();
        if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);
            for (int i = 0; i < nbttaglist.size(); ++i) {
                mobs.add(new MobSpawnerAbstract.a(nbttaglist.get(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("SpawnData", 10)) {
            a(new MobSpawnerAbstract.a(nbttagcompound.getCompound("SpawnData"), this.mobName));
        } else {
            a((MobSpawnerAbstract.a) null);
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        String s = getMobName();

        if (!UtilColor.b(s)) {
            nbttagcompound.setString("EntityId", s);
            nbttagcompound.setShort("Delay", (short) spawnDelay);
            nbttagcompound.setShort("MinSpawnDelay", (short) InvictusConfig.minSpawnDelay);
            nbttagcompound.setShort("MaxSpawnDelay", (short) InvictusConfig.maxSpawnDelay);
            nbttagcompound.setShort("SpawnCount", (short) InvictusConfig.spawnCount);
            nbttagcompound.setShort("MaxNearbyEntities", (short) InvictusConfig.maxNearbyEntities);
            nbttagcompound.setShort("RequiredPlayerRange", (short) InvictusConfig.requiredPlayersNearbyRange);
            nbttagcompound.setShort("SpawnRange", (short) InvictusConfig.spawnRange);
            if (i() != null) {
                nbttagcompound.set("SpawnData", i().c.clone());
            }

            if (i() != null || mobs.size() > 0) {
                NBTTagList nbttaglist = new NBTTagList();

                if (mobs.size() > 0) {
                    for (a mob : mobs) {
                        nbttaglist.add(mob.a());
                    }
                } else {
                    nbttaglist.add(i().a());
                }

                nbttagcompound.set("SpawnPotentials", nbttaglist);
            }

        }
    }

    public boolean b(int i) {
        return false;
    }

    private MobSpawnerAbstract.a i() {
        return spawnData;
    }

    public void a(MobSpawnerAbstract.a spawnData) {
        this.spawnData = spawnData;
    }

    public abstract void a(int i);

    public abstract World a();

    public abstract BlockPosition b();

    public class a extends WeightedRandom.WeightedRandomChoice {
        private final NBTTagCompound c;
        private final String d;

        public a(NBTTagCompound nbttagcompound) {
            this(nbttagcompound.getCompound("Properties"), nbttagcompound.getString("Type"), nbttagcompound.getInt("Weight"));
        }

        public a(NBTTagCompound nbttagcompound, String s) {
            this(nbttagcompound, s, 1);
        }

        private a(NBTTagCompound nbttagcompound, String s, int i) {
            super(i);
            if (s.equals("Minecart")) {
                if (nbttagcompound != null) {
                    s = EntityMinecartAbstract.EnumMinecartType.a(nbttagcompound.getInt("Type")).b();
                } else {
                    s = "MinecartRideable";
                }
            }

            this.c = nbttagcompound;
            this.d = s;
        }

        public NBTTagCompound a() {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.set("Properties", this.c);
            nbttagcompound.setString("Type", this.d);
            nbttagcompound.setInt("Weight", this.a);
            return nbttagcompound;
        }
    }
}
