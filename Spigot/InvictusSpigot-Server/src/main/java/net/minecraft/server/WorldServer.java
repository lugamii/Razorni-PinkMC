package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import gnu.trove.iterator.TLongShortIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.WeatherType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.util.HashTreeSet;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

import java.util.*;
import java.util.logging.Level;
// CraftBukkit end

public class WorldServer extends World implements IAsyncTaskHandler {

    private static final Logger a = LogManager.getLogger();
    private static final List<StructurePieceTreasure> U = Lists.newArrayList(new StructurePieceTreasure(Items.STICK, 0, 1, 3, 10), new StructurePieceTreasure(Item.getItemOf(Blocks.PLANKS), 0, 1, 3, 10), new StructurePieceTreasure(Item.getItemOf(Blocks.LOG), 0, 1, 3, 10), new StructurePieceTreasure(Items.STONE_AXE, 0, 1, 1, 3), new StructurePieceTreasure(Items.WOODEN_AXE, 0, 1, 1, 5), new StructurePieceTreasure(Items.STONE_PICKAXE, 0, 1, 1, 3), new StructurePieceTreasure(Items.WOODEN_PICKAXE, 0, 1, 1, 5), new StructurePieceTreasure(Items.APPLE, 0, 2, 3, 5), new StructurePieceTreasure(Items.BREAD, 0, 2, 3, 3), new StructurePieceTreasure(Item.getItemOf(Blocks.LOG2), 0, 1, 3, 10));
    // CraftBukkit start
    public final int dimension;
    protected final VillageSiege siegeManager = new VillageSiege(this);
    private final MinecraftServer server;
    private final PlayerChunkMap manager;
    private final HashTreeSet<NextTickListEntry> M = new HashTreeSet<>(); // CraftBukkit - HashTreeSet // PAIL: Rename nextTickList
    private final Map<UUID, Entity> entitiesByUUID = Maps.newHashMap();
    private final PortalTravelAgent Q;
    private final SpawnerCreature R = new SpawnerCreature();
    private final WorldServer.BlockActionDataList[] S = new WorldServer.BlockActionDataList[]{new WorldServer.BlockActionDataList(null), new WorldServer.BlockActionDataList(null)};
    private final List<NextTickListEntry> V = Lists.newArrayList();
    public EntityTracker tracker;
    public ChunkProviderServer chunkProviderServer;
    public boolean savingDisabled;
    private boolean O;
    private int T;
    public boolean ticking;

    // Add env and gen to constructor
    public WorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, WorldData worlddata, int i, MethodProfiler methodprofiler, org.bukkit.World.Environment env, org.bukkit.generator.ChunkGenerator gen) {
        super(idatamanager, worlddata, WorldProvider.byDimension(env.getId()), methodprofiler, false, gen, env);
        this.dimension = i;
        this.pvpMode = minecraftserver.getPVP();
        worlddata.world = this;
        this.server = minecraftserver;
        this.tracker = new EntityTracker(this);
        this.manager = new PlayerChunkMap(this, spigotConfig.viewDistance); // Spigot
        this.worldProvider.a(this);
        this.chunkProvider = this.k();
        this.Q = ((org.bukkit.craftbukkit.CraftTravelAgent) new org.bukkit.craftbukkit.CraftTravelAgent(this).setSearchRadius(paperSpigotConfig.portalSearchRadius)); // CraftBukkit // Paper - configurable search radius
        this.B();
        this.C();
        this.getWorldBorder().a(minecraftserver.aI());
    }

    public boolean checkTicking() {
        if (ticking && players.isEmpty()) {
            ticking = false;
            keepSpawnInMemory = false;
        } else if (!players.isEmpty() && !ticking) {
            ticking = true;
            keepSpawnInMemory = paperSpigotConfig.keepSpawnInMemory;
        }
        return ticking;
    }

    public World b() {
        this.worldMaps = new PersistentCollection(this.dataManager);
        String s = PersistentVillage.a(this.worldProvider);
        PersistentVillage persistentvillage = (PersistentVillage) this.worldMaps.get(PersistentVillage.class, s);

        if (persistentvillage == null) {
            this.villages = new PersistentVillage(this);
            this.worldMaps.a(s, this.villages);
        } else {
            this.villages = persistentvillage;
            this.villages.a(this);
        }

        if (getServer().getScoreboardManager() == null) { // CraftBukkit
            this.scoreboard = new ScoreboardServer(this.server);
            PersistentScoreboard persistentscoreboard = (PersistentScoreboard) this.worldMaps.get(PersistentScoreboard.class, "scoreboard");

            if (persistentscoreboard == null) {
                persistentscoreboard = new PersistentScoreboard();
                this.worldMaps.a("scoreboard", persistentscoreboard);
            }

            persistentscoreboard.a(this.scoreboard);
            ((ScoreboardServer) this.scoreboard).a(persistentscoreboard);
            // CraftBukkit start
        } else {
            this.scoreboard = getServer().getScoreboardManager().getMainScoreboard().getHandle();
        }
        // CraftBukkit end
        this.getWorldBorder().setCenter(this.worldData.C(), this.worldData.D());
        this.getWorldBorder().setDamageAmount(this.worldData.I());
        this.getWorldBorder().setDamageBuffer(this.worldData.H());
        this.getWorldBorder().setWarningDistance(this.worldData.J());
        this.getWorldBorder().setWarningTime(this.worldData.K());
        if (this.worldData.F() > 0L) {
            this.getWorldBorder().transitionSizeBetween(this.worldData.E(), this.worldData.G(), this.worldData.F());
        } else {
            this.getWorldBorder().setSize(this.worldData.E());
        }

        // CraftBukkit start
        if (generator != null) {
            getWorld().getPopulators().addAll(generator.getDefaultPopulators(getWorld()));
        }
        // CraftBukkit end

        return this;
    }

    // CraftBukkit start
    @Override
    public TileEntity getTileEntity(BlockPosition pos) {
        TileEntity result = super.getTileEntity(pos);
        Block type = getType(pos).getBlock();

        if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST) { // Spigot
            if (!(result instanceof TileEntityChest)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.FURNACE) {
            if (!(result instanceof TileEntityFurnace)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.DROPPER) {
            if (!(result instanceof TileEntityDropper)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.DISPENSER) {
            if (!(result instanceof TileEntityDispenser)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.JUKEBOX) {
            if (!(result instanceof BlockJukeBox.TileEntityRecordPlayer)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.NOTEBLOCK) {
            if (!(result instanceof TileEntityNote)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.MOB_SPAWNER) {
            if (!(result instanceof TileEntityMobSpawner)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if ((type == Blocks.STANDING_SIGN) || (type == Blocks.WALL_SIGN)) {
            if (!(result instanceof TileEntitySign)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.ENDER_CHEST) {
            if (!(result instanceof TileEntityEnderChest)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.BREWING_STAND) {
            if (!(result instanceof TileEntityBrewingStand)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.BEACON) {
            if (!(result instanceof TileEntityBeacon)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.HOPPER) {
            if (!(result instanceof TileEntityHopper)) {
                result = fixTileEntity(pos, type, result);
            }
        }

        return result;
    }

    public TileEntity getTileEntity(Chunk ch, BlockPosition pos, Block type) { // Lags with getCubes for pistons having to push
        // sand
        TileEntity result = super.getTileEntity(ch, pos);
        if (result instanceof TileEntityPiston) {
            return result;
        }
        if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST) { // Spigot
            if (!(result instanceof TileEntityChest)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.HOPPER) { // Moved Hoppers from bottom to top b/c they're so common
            if (!(result instanceof TileEntityHopper)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.FURNACE) {
            if (!(result instanceof TileEntityFurnace)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.DROPPER) {
            if (!(result instanceof TileEntityDropper)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.DISPENSER) {
            if (!(result instanceof TileEntityDispenser)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.JUKEBOX) {
            if (!(result instanceof BlockJukeBox.TileEntityRecordPlayer)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.NOTEBLOCK) {
            if (!(result instanceof TileEntityNote)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.MOB_SPAWNER) {
            if (!(result instanceof TileEntityMobSpawner)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if ((type == Blocks.STANDING_SIGN) || (type == Blocks.WALL_SIGN)) {
            if (!(result instanceof TileEntitySign)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.ENDER_CHEST) {
            if (!(result instanceof TileEntityEnderChest)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.BREWING_STAND) {
            if (!(result instanceof TileEntityBrewingStand)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.BEACON) {
            if (!(result instanceof TileEntityBeacon)) {
                result = fixTileEntity(pos, type, result);
            }
        }

        return result;
    }

    private TileEntity fixTileEntity(BlockPosition pos, Block type, TileEntity found) {
        this.getServer().getLogger().log(Level.SEVERE, "Block at {0},{1},{2} is {3} but has {4}" + ". "
                + "Bukkit will attempt to fix this, but there may be additional damage that we cannot recover.", new Object[]{pos.getX(), pos.getY(), pos.getZ(), Objects.requireNonNull(org.bukkit.Material.getMaterial(Block.getId(type))).toString(), found});

        if (type instanceof IContainer) {
            TileEntity replacement = ((IContainer) type).a(this, type.toLegacyData(type.getBlockData()));
            replacement.world = this;
            this.setTileEntity(pos, replacement);
            return replacement;
        } else {
            this.getServer().getLogger().severe("Don't know how to fix for this type... Can't do anything! :(");
            return found;
        }
    }

    private boolean canSpawn(int x, int z) {
        if (generator != null) {
            return generator.canSpawn(getWorld(), x, z);
        } else {
            return worldProvider.canSpawn(x, z);
        }
    }
    // CraftBukkit end

    public void doTick() {
        if (InvictusConfig.tickWeather)
            super.doTick();

        worldProvider.m().b();

        boolean dayLightCycle = getGameRules().getBoolean("doDaylightCycle");
        if (InvictusConfig.sleepCheck && everyoneDeeplySleeping()) {
            if (dayLightCycle) {
                long i = worldData.getDayTime() + 24000L;
                worldData.setDayTime(i - i % 24000L);
            }
            e();
        }

        long time = worldData.getTime();
        if (InvictusConfig.mobSpawn && (allowMonsters || allowAnimals) && players.size() > 0) {
            R.a(this, allowMonsters && (ticksPerMonsterSpawns != 0 && time % ticksPerMonsterSpawns == 0L), allowAnimals && (ticksPerAnimalSpawns != 0 && time % ticksPerAnimalSpawns == 0L), worldData.getTime() % 400L == 0L);
        }

        if (InvictusConfig.unloadChunks) {
            chunkProvider.unloadChunks();
        }

        int j = a(1.0F);
        if (j != ab()) {
            c(j);
        }

        worldData.setTime(time + 1L);
        if (dayLightCycle)
            worldData.setDayTime(worldData.getDayTime() + 1L);

        a(false);

        h();

        spigotConfig.antiXrayInstance.flushUpdates(this); // PaperSpigot

        manager.flush();

        if (InvictusConfig.tickVillages) {
            villages.tick();
            siegeManager.a();
        }

        Q.a(getTime());

        ak();

        getWorld().processChunkGC(); // CraftBukkit
    }

    public BiomeBase.BiomeMeta a(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        List<BiomeBase.BiomeMeta> list = this.N().getMobsFor(enumcreaturetype, blockposition);

        return list != null && !list.isEmpty() ? WeightedRandom.a(this.random, list) : null;
    }

    public boolean a(EnumCreatureType enumcreaturetype, BiomeBase.BiomeMeta biomebase_biomemeta, BlockPosition blockposition) {
        List<BiomeBase.BiomeMeta> list = this.N().getMobsFor(enumcreaturetype, blockposition);

        return list != null && !list.isEmpty() && list.contains(biomebase_biomemeta);
    }

    public void everyoneSleeping() {
        if (!InvictusConfig.sleepCheck)
            return;
        O = false;
        if (!players.isEmpty()) {
            int i = 0;
            int j = 0;

            for (EntityHuman entityhuman : players) {
                if (entityhuman.isSpectator()) {
                    ++i;
                } else if (entityhuman.isSleeping() || entityhuman.fauxSleeping) {
                    ++j;
                }
            }

            O = j > 0 && j >= players.size() - i;
        }

    }

    protected void e() {
        O = false;

        for (EntityHuman entityhuman : this.players) {
            if (entityhuman.isSleeping()) {
                entityhuman.a(false, false, true);
            }
        }

        ag();
    }

    private void ag() {
        worldData.setStorm(false);
        // CraftBukkit start
        // If we stop due to everyone sleeping we should reset the weather duration to some other random value.
        // Not that everyone ever manages to get the whole server to sleep at the same time....
        if (!worldData.hasStorm()) {
            worldData.setWeatherDuration(0);
        }
        // CraftBukkit end
        worldData.setThundering(false);
        // CraftBukkit start
        // If we stop due to everyone sleeping we should reset the weather duration to some other random value.
        // Not that everyone ever manages to get the whole server to sleep at the same time....
        if (!worldData.isThundering()) {
            worldData.setThunderDuration(0);
        }
        // CraftBukkit end
    }

    public boolean everyoneDeeplySleeping() {
        if (O) {
            Iterator<EntityHuman> iterator = players.iterator();

            // CraftBukkit - This allows us to assume that some people are in bed but not really, allowing time to pass in spite of AFKers
            boolean foundActualSleepers = false;

            EntityHuman entityhuman;

            do {
                if (!iterator.hasNext()) {
                    return foundActualSleepers;
                }

                entityhuman = iterator.next();

                // CraftBukkit start
                if (entityhuman.isDeeplySleeping()) {
                    foundActualSleepers = true;
                }
            } while (!entityhuman.isSpectator() || entityhuman.isDeeplySleeping() || entityhuman.fauxSleeping);
            // CraftBukkit end

        }
        return false;
    }

    protected void h() {
        super.h();
        int randomTickSpeed = InvictusConfig.randomTickSpeed;
        for (TLongShortIterator iter = chunkTickList.iterator(); iter.hasNext(); ) {
            iter.advance();

            long chunkCoord = iter.key();
            int chunkX = World.keyToX(chunkCoord), chunkZ = World.keyToZ(chunkCoord);
            long hash = LongHash.toLong(chunkX, chunkZ);
            if (!chunkProvider.isChunkLoaded(hash) || chunkProviderServer.unloadQueue.contains(hash)) {
                iter.remove();
                continue;
            }

            int k = chunkX * 16, l = chunkZ * 16;
            Chunk chunk = chunkProvider.getOrCreateChunk(hash, chunkX, chunkZ);

            a(k, l, chunk);
            chunk.b(false);

            if (!paperSpigotConfig.disableThunder && random.nextInt(100000) == 0 && S() && R()) {
                m = m * 3 + 1013904223;

                int i = m >> 2;
                BlockPosition blockposition = a(k + (i & 0xF), l + (i >> 8 & 0xF));
                if (isRainingAt(blockposition))
                    strikeLightning(new EntityLightning(this, blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            }

            if (!paperSpigotConfig.disableIceAndSnow && random.nextInt(16) == 0) {
                m = m * 3 + 1013904223;

                int i = m >> 2;
                BlockPosition blockposition = q(k + (i & 0xF), l + (i >> 8 & 0xF));
                BlockPosition blockposition1 = blockposition.down();

                if (w(blockposition1)) {
                    BlockState blockState = getWorld().getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ()).getState();
                    blockState.setTypeId(Block.getId(Blocks.ICE));
                    BlockFormEvent iceBlockForm = new BlockFormEvent(blockState.getBlock(), blockState);
                    getServer().getPluginManager().callEvent(iceBlockForm);
                    if (!iceBlockForm.isCancelled())
                        blockState.update(true);
                }

                if (S() && f(blockposition, true)) {
                    BlockState blockState = getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()).getState();
                    blockState.setTypeId(Block.getId(Blocks.SNOW_LAYER));
                    BlockFormEvent snow = new BlockFormEvent(blockState.getBlock(), blockState);
                    getServer().getPluginManager().callEvent(snow);
                    if (!snow.isCancelled())
                        blockState.update(true);
                }

                if (S() && getBiome(blockposition1).e())
                    getType(blockposition1).getBlock().k(this, blockposition1);
            }

            if (randomTickSpeed > 0) {
                for (ChunkSection chunksection : chunk.getSections()) {
                    if (chunksection != null && chunksection.shouldTick())
                        for (int l1 = 0; l1 < randomTickSpeed; l1++) {
                            this.m = m * 3 + 1013904223;
                            int i2 = m >> 2;
                            int j2 = i2 & 0xF;
                            int k2 = i2 >> 8 & 0xF;
                            int l2 = i2 >> 16 & 0xF;
                            IBlockData iblockdata = chunksection.getType(j2, l2, k2);
                            Block block = iblockdata.getBlock();
                            if (block.isTicking())
                                block.a(this, new BlockPosition(j2 + k, l2 + chunksection.getYPosition(), k2 + l), iblockdata, this.random);
                        }
                }
            }
        }

        if (spigotConfig.clearChunksOnTick)
            chunkTickList.clear();
    }

    protected BlockPosition a(BlockPosition blockposition) {
        return a(blockposition.getX(), blockposition.getZ());
    }

    protected BlockPosition a(int x, int z) {
        BlockPosition blockposition1 = q(x, z);
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), blockposition1.getX(), getHeight(), blockposition1.getZ()).grow(3.0D, 3.0D, 3.0D);
        List<EntityLiving> list = a(EntityLiving.class, axisalignedbb, (Predicate<EntityLiving>) entityliving -> entityliving != null && entityliving.isAlive() && WorldServer.this.i(entityliving.getChunkCoordinates()));

        return !list.isEmpty() ? list.get(random.nextInt(list.size())).getChunkCoordinates() : blockposition1;
    }

    public boolean a(BlockPosition blockposition, Block block) {
        return V.contains(new NextTickListEntry(blockposition, block));
    }

    public void a(BlockPosition blockposition, Block block, int i) {
        a(blockposition, block, i, 0);
    }

    public void a(BlockPosition blockposition, Block block, int i, int j) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);

        if (e && block.getMaterial() != Material.AIR) {
            if (block.N()) {
                BlockPosition position = nextticklistentry.a;
                if (areChunksLoadedBetween(position.a(-8, -8, -8), position.a(8, 8, 8))) {
                    Chunk chunk = getChunkAtWorldCoords(position);
                    IBlockData iblockdata = getType(chunk, position.getX(), position.getY(), position.getZ(), true);
                    Block iblock = iblockdata.getBlock();
                    if (iblock.getMaterial() != Material.AIR && iblock == nextticklistentry.a()) {
                        iblock.b(this, chunk, position, iblockdata, this.random);
                    }
                }
                return;
            }
            i = 1;
        }

        if (isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4, true)) {
            if (block.getMaterial() != Material.AIR) {
                nextticklistentry.a(i + this.worldData.getTime());
                nextticklistentry.a(j);
            }
            if (!M.contains(nextticklistentry))
                M.add(nextticklistentry);
        }
    }

    public void a(BlockPosition blockposition, Chunk chunk, Block block, int i, int j) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);

        if (e && block.getMaterial() != Material.AIR) {
            if (block.N()) {
                if (areChunksLoadedBetween(blockposition.a(-8, -8, -8), blockposition.a(8, 8, 8))) {
                    IBlockData iblockdata = getType(chunk, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);
                    Block iblock = iblockdata.getBlock();
                    if (iblock.getMaterial() != Material.AIR && iblock == nextticklistentry.a()) {
                        iblock.b(this, chunk, blockposition, iblockdata, this.random);
                    }
                }
                return;
            }
            i = 1;
        }

        if (block.getMaterial() != Material.AIR) {
            nextticklistentry.a(i + this.worldData.getTime());
            nextticklistentry.a(j);
        }
        if (!M.contains(nextticklistentry))
            M.add(nextticklistentry);
    }

    public void b(BlockPosition blockposition, Block block, int i, int j) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);
        nextticklistentry.a(j);
        if (block.getMaterial() != Material.AIR)
            nextticklistentry.a(i + this.worldData.getTime());
        if (!M.contains(nextticklistentry))
            M.add(nextticklistentry);
    }

    public boolean a(boolean flag) {
        int i = M.size();
        if (i > paperSpigotConfig.tickNextTickCap)
            i = paperSpigotConfig.tickNextTickCap;

        for (int j = 0; j < i; j++) {
            NextTickListEntry nextticklistentry = M.first();
            if (!flag && nextticklistentry.b > worldData.getTime())
                break;
            M.remove(nextticklistentry);
            V.add(nextticklistentry);
        }

        if (paperSpigotConfig.tickNextTickListCapIgnoresRedstone) {
            Iterator<NextTickListEntry> iterator1 = M.iterator();
            while (iterator1.hasNext()) {
                NextTickListEntry next = iterator1.next();
                if (!flag && next.b > worldData.getTime())
                    break;
                Block block = next.a();
                if (block.isPowerSource() || block instanceof IContainer || block instanceof BlockFalling) {
                    iterator1.remove();
                    V.add(next);
                }
            }
        }

        Iterator<NextTickListEntry> iterator = V.iterator();
        while (iterator.hasNext()) {
            NextTickListEntry nextticklistentry = iterator.next();
            iterator.remove();
            BlockPosition position = nextticklistentry.a;
            Chunk chunk = getChunkIfLoaded(position.getX() >> 4, position.getZ() >> 4);
            if (chunk != null) {
                IBlockData iblockdata = getType(chunk, position.getX(), position.getY(), position.getZ(), true);
                Block block = iblockdata.getBlock();
                if (block.getMaterial() != Material.AIR && Block.a(block, nextticklistentry.a())) {
                    try {
                        block.b(this, chunk, position, iblockdata, this.random);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Exception while ticking a block");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being ticked");
                        CrashReportSystemDetails.a(crashreportsystemdetails, position, iblockdata);
                        throw new ReportedException(crashreport);
                    }
                }
                continue;
            }
            a(position, nextticklistentry.a(), 0);
        }
        V.clear();
        return !M.isEmpty();
    }

    public List<NextTickListEntry> a(Chunk chunk, boolean flag) {
        ChunkCoordIntPair chunkcoordintpair = chunk.j();
        int i = (chunkcoordintpair.x << 4) - 2;
        int k = (chunkcoordintpair.z << 4) - 2;
        return a(new StructureBoundingBox(i, 0, k, i + 16 + 2, 256, k + 16 + 2), flag);
    }

    public List<NextTickListEntry> a(StructureBoundingBox structureboundingbox, boolean flag) {
        ArrayList<NextTickListEntry> arraylist = null;
        for (int i = 0; i < 2; i++) {
            Iterator<NextTickListEntry> iterator;
            if (i == 0) {
                iterator = this.M.iterator();
            } else {
                iterator = this.V.iterator();
            }
            while (iterator.hasNext()) {
                NextTickListEntry nextticklistentry = iterator.next();
                BlockPosition blockposition = nextticklistentry.a;
                if (blockposition.getX() >= structureboundingbox.a && blockposition.getX() < structureboundingbox.d && blockposition.getZ() >= structureboundingbox.c && blockposition.getZ() < structureboundingbox.f) {
                    if (flag)
                        iterator.remove();
                    if (arraylist == null)
                        arraylist = Lists.newArrayList();
                    arraylist.add(nextticklistentry);
                }
            }
        }
        return arraylist;
    }

    protected IChunkProvider k() {
        IChunkLoader ichunkloader = this.dataManager.createChunkLoader(this.worldProvider);

        // CraftBukkit start
        org.bukkit.craftbukkit.generator.InternalChunkGenerator gen;

        if (this.generator != null) {
            gen = new org.bukkit.craftbukkit.generator.CustomChunkGenerator(this, this.getSeed(), this.generator);
        } else if (this.worldProvider instanceof WorldProviderHell) {
            gen = new org.bukkit.craftbukkit.generator.NetherChunkGenerator(this, this.getSeed());
        } else if (this.worldProvider instanceof WorldProviderTheEnd) {
            gen = new org.bukkit.craftbukkit.generator.SkyLandsChunkGenerator(this, this.getSeed());
        } else {
            gen = new org.bukkit.craftbukkit.generator.NormalChunkGenerator(this, this.getSeed());
        }

        this.chunkProviderServer = new ChunkProviderServer(this, ichunkloader, gen);
        // CraftBukkit end
        return this.chunkProviderServer;
    }

    public List<TileEntity> getTileEntities(int i, int j, int k, int l, int i1, int j1) {
        List<TileEntity> arraylist = Lists.newArrayList();

        // CraftBukkit start - Get tile entities from chunks instead of world
        for (int chunkX = (i >> 4); chunkX <= ((l - 1) >> 4); chunkX++) {
            for (int chunkZ = (k >> 4); chunkZ <= ((j1 - 1) >> 4); chunkZ++) {
                Chunk chunk = getChunkAt(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                for (TileEntity tileentity : chunk.tileEntities.values()) {
                    if ((tileentity.position.getX() >= i) && (tileentity.position.getY() >= j) && (tileentity.position.getZ() >= k) && (tileentity.position.getX() < l) && (tileentity.position.getY() < i1) && (tileentity.position.getZ() < j1)) {
                        arraylist.add(tileentity);
                    }
                }
            }
        }

        return arraylist;
    }

    public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
        return !this.server.a(this, blockposition, entityhuman) && this.getWorldBorder().a(blockposition);
    }

    public void a(WorldSettings worldsettings) {
        if (!this.worldData.w()) {
            try {
                this.b(worldsettings);

                super.a(worldsettings);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception initializing level");

                try {
                    this.a(crashreport);
                } catch (Throwable ignored) {
                }

                throw new ReportedException(crashreport);
            }

            this.worldData.d(true);
        }

    }

    private void aj() {
        this.worldData.f(false);
        this.worldData.c(true);
        this.worldData.setStorm(false);
        this.worldData.setThundering(false);
        this.worldData.i(1000000000);
        this.worldData.setDayTime(6000L);
        this.worldData.setGameType(WorldSettings.EnumGamemode.SPECTATOR);
        this.worldData.g(false);
        this.worldData.setDifficulty(EnumDifficulty.PEACEFUL);
        this.worldData.e(true);
        this.getGameRules().set("doDaylightCycle", "false");
    }

    private void b(WorldSettings worldsettings) {
        if (!this.worldProvider.e()) {
            this.worldData.setSpawn(BlockPosition.ZERO.up(this.worldProvider.getSeaLevel()));
        } else {
            this.isLoading = true;
            WorldChunkManager worldchunkmanager = this.worldProvider.m();
            Random random = new Random(this.getSeed());
            BlockPosition blockposition = worldchunkmanager.a(0, 0, 256, worldchunkmanager.a(), random);
            int i = 0;
            int j = this.worldProvider.getSeaLevel();
            int k = 0;

            // CraftBukkit start
            if (this.generator != null) {
                Random rand = new Random(this.getSeed());
                org.bukkit.Location spawn = this.generator.getFixedSpawnLocation(getWorld(), rand);

                if (spawn != null) {
                    if (spawn.getWorld() != getWorld()) {
                        throw new IllegalStateException("Cannot set spawn point for " + this.worldData.getName() + " to be in another world (" + spawn.getWorld().getName() + ")");
                    } else {
                        this.worldData.setSpawn(new BlockPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()));
                        this.isLoading = false;
                        return;
                    }
                }
            }
            // CraftBukkit end

            if (blockposition != null) {
                i = blockposition.getX();
                k = blockposition.getZ();
            } else {
                WorldServer.a.warn("Unable to find spawn biome");
            }

            int l = 0;

            while (!this.canSpawn(i, k)) { // CraftBukkit - use our own canSpawn
                i += random.nextInt(64) - random.nextInt(64);
                k += random.nextInt(64) - random.nextInt(64);
                ++l;
                if (l == 1000) {
                    break;
                }
            }

            this.worldData.setSpawn(new BlockPosition(i, j, k));
            this.isLoading = false;
            if (worldsettings.c()) {
                this.l();
            }

        }
    }

    protected void l() {
        WorldGenBonusChest worldgenbonuschest = new WorldGenBonusChest(WorldServer.U, 10);

        for (int i = 0; i < 10; ++i) {
            int j = this.worldData.c() + this.random.nextInt(6) - this.random.nextInt(6);
            int k = this.worldData.e() + this.random.nextInt(6) - this.random.nextInt(6);
            BlockPosition blockposition = this.r(new BlockPosition(j, 0, k)).up();

            if (worldgenbonuschest.generate(this, this.random, blockposition)) {
                break;
            }
        }

    }

    public BlockPosition getDimensionSpawn() {
        return this.worldProvider.h();
    }

    public void save(boolean flag, IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
        if (this.chunkProvider.canSave()) {
            org.bukkit.Bukkit.getPluginManager().callEvent(new org.bukkit.event.world.WorldSaveEvent(getWorld())); // CraftBukkit
            if (iprogressupdate != null) {
                iprogressupdate.a("Saving level");
            }

            this.a();
            if (iprogressupdate != null) {
                iprogressupdate.c("Saving chunks");
            }

            this.chunkProvider.saveChunks(flag, iprogressupdate);
            // CraftBukkit - ArrayList -> Collection

            for (Chunk chunk : chunkProviderServer.a()) {
                if (chunk != null && !this.manager.a(chunk.locX, chunk.locZ)) {
                    this.chunkProviderServer.queueUnload(chunk.locX, chunk.locZ);
                }
            }

        }
    }

    public void flushSave() {
        if (this.chunkProvider.canSave()) {
            this.chunkProvider.c();
        }
    }

    protected void a() throws ExceptionWorldConflict {
        this.checkSession();
        this.worldData.a(this.getWorldBorder().getSize());
        this.worldData.d(this.getWorldBorder().getCenterX());
        this.worldData.c(this.getWorldBorder().getCenterZ());
        this.worldData.e(this.getWorldBorder().getDamageBuffer());
        this.worldData.f(this.getWorldBorder().getDamageAmount());
        this.worldData.j(this.getWorldBorder().getWarningDistance());
        this.worldData.k(this.getWorldBorder().getWarningTime());
        this.worldData.b(this.getWorldBorder().j());
        this.worldData.e(this.getWorldBorder().i());
        // CraftBukkit start - save worldMaps once, rather than once per shared world
        if (!(this instanceof SecondaryWorldServer)) {
            this.worldMaps.a();
        }
        this.dataManager.saveWorldData(this.worldData, this.server.getPlayerList().t());
        // CraftBukkit end
    }

    protected void a(Entity entity) {
        super.a(entity);
        this.entitiesById.a(entity.getId(), entity);
        if (!(entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock)) {
            this.entitiesByUUID.put(entity.getUniqueID(), entity);
            Entity[] aentity = entity.aB();

            if (aentity != null) {
                for (Entity value : aentity) {
                    this.entitiesById.a(value.getId(), value);
                }
            }
        }
    }

    protected void b(Entity entity) {
        super.b(entity);
        this.entitiesById.d(entity.getId());
        if (!(entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock)) {
            this.entitiesByUUID.remove(entity.getUniqueID());
            Entity[] aentity = entity.aB();

            if (aentity != null) {
                for (Entity value : aentity) {
                    this.entitiesById.d(value.getId());
                }
            }
        }
    }

    public boolean strikeLightning(Entity entity) {
        // CraftBukkit start
        LightningStrikeEvent lightning = new LightningStrikeEvent(this.getWorld(), (org.bukkit.entity.LightningStrike) entity.getBukkitEntity());
        this.getServer().getPluginManager().callEvent(lightning);

        if (lightning.isCancelled()) {
            return false;
        }
        if (super.strikeLightning(entity)) {
            this.server.getPlayerList().sendPacketNearby(entity.locX, entity.locY, entity.locZ, 512.0D, dimension, new PacketPlayOutSpawnEntityWeather(entity));
            // CraftBukkit end
            return true;
        } else {
            return false;
        }
    }

    public void broadcastEntityEffect(Entity entity, byte b0) {
        this.getTracker().sendPacketToEntity(entity, new PacketPlayOutEntityStatus(entity, b0));
    }

    public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        // CraftBukkit start
        Explosion explosion = super.createExplosion(entity, d0, d1, d2, f, flag, flag1);

        if (explosion.wasCanceled) {
            return explosion;
        }

        if (!flag1) {
            explosion.clearBlocks();
        }

        if (InvictusConfig.explosionParticles)
            for (EntityHuman entityhuman : players) {
                if (entityhuman.e(d0, d1, d2) < 4096.0D)
                    ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutExplosion(d0, d1, d2, f, explosion.getBlocks(), explosion.b().get(entityhuman)));
            }

        return explosion;
    }

    public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        BlockActionData blockactiondata = new BlockActionData(blockposition, block, i, j);
        Iterator<BlockActionData> iterator = this.S[this.T].iterator();

        BlockActionData blockactiondata1;

        do {
            if (!iterator.hasNext()) {
                this.S[this.T].add(blockactiondata);
                return;
            }

            blockactiondata1 = iterator.next();
        } while (!blockactiondata1.equals(blockactiondata));

    }

    private void ak() {
        while (!this.S[this.T].isEmpty()) {
            int i = this.T;

            this.T ^= 1;

            for (BlockActionData blockactiondata : this.S[i]) {
                if (this.a(blockactiondata)) {
                    // CraftBukkit - this.worldProvider.dimension -> this.dimension
                    this.server.getPlayerList().sendPacketNearby(blockactiondata.a().getX(), blockactiondata.a().getY(), blockactiondata.a().getZ(), 64.0D, dimension, new PacketPlayOutBlockAction(blockactiondata.a(), blockactiondata.d(), blockactiondata.b(), blockactiondata.c()));
                }
            }

            this.S[i].clear();
        }

    }

    private boolean a(BlockActionData blockactiondata) {
        IBlockData iblockdata = this.getType(blockactiondata.a());

        return iblockdata.getBlock() == blockactiondata.d() && iblockdata.getBlock().a(this, blockactiondata.a(), iblockdata, blockactiondata.b(), blockactiondata.c());
    }

    public void saveLevel() {
        this.dataManager.a();
    }

    protected void p() {
        boolean flag = this.S();

        super.p();

        if (flag != this.S()) {
            // Only send weather packets to those affected
            for (EntityHuman player : this.players) {
                if (player.world == this) {
                    ((EntityPlayer) player).setPlayerWeather((!flag ? WeatherType.DOWNFALL : WeatherType.CLEAR), false);
                }
            }
        }
        for (EntityHuman player : this.players) {
            if (player.world == this) {
                ((EntityPlayer) player).updateWeather(this.o, this.p, this.q, this.r);
            }
        }
        // CraftBukkit end

    }

    protected int q() {
        return this.server.getPlayerList().s();
    }

    public MinecraftServer getMinecraftServer() {
        return this.server;
    }

    public EntityTracker getTracker() {
        return this.tracker;
    }

    public PlayerChunkMap getPlayerChunkMap() {
        return this.manager;
    }

    public PortalTravelAgent getTravelAgent() {
        return this.Q;
    }

    public void a(EnumParticle enumparticle, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
        this.a(enumparticle, false, d0, d1, d2, i, d3, d4, d5, d6, aint);
    }

    public void a(EnumParticle enumparticle, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
        // CraftBukkit - visibility api support
        sendParticles(null, enumparticle, flag, d0, d1, d2, i, d3, d4, d5, d6, aint);
    }

    public void sendParticles(EntityPlayer sender, EnumParticle enumparticle, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
        // CraftBukkit end
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(enumparticle, flag, (float) d0, (float) d1, (float) d2, (float) d3, (float) d4, (float) d5, (float) d6, i, aint);

        for (EntityHuman entityHuman : this.players) {
            EntityPlayer player = (EntityPlayer) entityHuman;
            if (sender != null && !player.getBukkitEntity().canSee(sender.getBukkitEntity())) {
                continue; // CraftBukkit
            }
            BlockPosition blockposition = player.getChunkCoordinates();
            double d7 = blockposition.c(d0, d1, d2);

            if (d7 <= 256.0D || flag && d7 <= 65536.0D) {
                player.playerConnection.sendPacket(packetplayoutworldparticles);
            }
        }

    }

    public Entity getEntity(UUID uuid) {
        return this.entitiesByUUID.get(uuid);
    }

    public ListenableFuture<Object> postToMainThread(Runnable runnable) {
        return this.server.postToMainThread(runnable);
    }

    public boolean isMainThread() {
        return this.server.isMainThread();
    }

    static class BlockActionDataList extends ArrayList<BlockActionData> {

        private BlockActionDataList() {
        }

        BlockActionDataList(Object object) {
            this();
        }
    }
}
