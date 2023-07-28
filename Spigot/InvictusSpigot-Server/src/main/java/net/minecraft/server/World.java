package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.vortexdev.invictusspigot.config.GeneratorConfig;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import eu.vortexdev.invictusspigot.util.java.LinkedArraySet;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.generator.ChunkGenerator;
import org.github.paperspigot.event.ServerExceptionEvent;
import org.github.paperspigot.exception.ServerInternalException;
import org.spigotmc.ActivationRange;
import org.spigotmc.AsyncCatcher;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class World implements IBlockAccess {
    public static boolean haveWeSilencedAPhysicsCrash;
    public static String blockLocation;
    public List<TileEntity> tileEntityList = Lists.newArrayList();
    public List<EntityHuman> players = Lists.newArrayList();
    public List<Entity> k = Lists.newArrayList();
    public MethodProfiler methodProfiler;
    public Random random = new Random();
    public boolean isClientSide;

    public final Map<Explosion.CacheKey, Float> explosionDensityCache = new HashMap<>();

    public org.spigotmc.SpigotWorldConfig spigotConfig; // Spigot
    public org.github.paperspigot.PaperSpigotWorldConfig paperSpigotConfig; // PaperSpigot
    public GeneratorConfig generatorConfig;
    // Spigot end
    protected Set<Entity> g = new LinkedArraySet<>(); // Paper
    protected IntHashMap<Entity> entitiesById = new IntHashMap<>();
    protected int n = 1013904223;
    protected IDataManager dataManager;
    protected gnu.trove.map.hash.TLongShortHashMap chunkTickList;
    private final List<TileEntity> b = Lists.newArrayList();
    private final Set<TileEntity> c = new LinkedArraySet<>(); // Paper
    private final Calendar K = Calendar.getInstance();
    private final WorldBorder N;
    // CraftBukkit start Added the following
    private final CraftWorld world;
    private final List<Runnable> interceptedSounds = new ArrayList<>();
    public byte chunkTickRadius;
    public WorldProvider worldProvider; // CraftBukkit - remove final
    public WorldData worldData; // CraftBukkit - public
    public PersistentCollection worldMaps; // CraftBukkit - public
    public Scoreboard scoreboard = new Scoreboard(); // CraftBukkit - public
    public boolean allowMonsters; // CraftBukkit - public
    public boolean allowAnimals; // CraftBukkit - public
    public boolean pvpMode;
    public boolean keepSpawnInMemory;
    public ChunkGenerator generator;
    public boolean captureBlockStates = false;
    public boolean captureTreeGeneration = false;
    public ArrayList<BlockState> capturedBlockStates = new ArrayList<BlockState>() {
        @Override
        public boolean add(BlockState blockState) {
            for (BlockState blockState1 : this) {
                if (blockState1.getLocation().equals(blockState.getLocation())) {
                    return false;
                }
            }
            return super.add(blockState);
        }
    };
    public long ticksPerAnimalSpawns;
    public long ticksPerMonsterSpawns;
    public boolean populating;
    public ExecutorService lightingExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Paper - Lighting Thread").build()); // PaperSpigot - Asynchronous lighting updates
    public Map<BlockPosition, TileEntity> capturedTileEntities = Maps.newHashMap();
    protected boolean e;
    protected int m = random.nextInt();
    protected float o;
    protected float p;
    protected float q;
    protected float r;
    protected List<IWorldAccess> u = Lists.newArrayList();
    protected IChunkProvider chunkProvider;
    protected boolean isLoading;
    protected PersistentVillage villages;
    protected float growthOdds = 100;
    protected float modifiedOdds = 100;
    int[] H;
    private int a = 63;
    private int I;
    private boolean M;
    private int tickPosition;
    private boolean guardEntityList;
    public List<Entity> entityList = new java.util.ArrayList<Entity>() {
        @Override
        public Entity remove(int index) {
            guard();
            return super.remove(index);
        }

        @Override
        public boolean remove(Object o) {
            guard();
            return super.remove(o);
        }

        private void guard() {
            if (guardEntityList) {
                throw new java.util.ConcurrentModificationException();
            }
        }
    };
    private int tileTickPosition;
    private boolean interceptSounds;

    protected World(IDataManager idatamanager, WorldData worlddata, WorldProvider worldprovider, MethodProfiler methodprofiler, boolean flag, ChunkGenerator gen, org.bukkit.World.Environment env) {
        this.generatorConfig = new GeneratorConfig(worlddata.getName());
        this.spigotConfig = new org.spigotmc.SpigotWorldConfig(worlddata.getName()); // Spigot
        this.paperSpigotConfig = new org.github.paperspigot.PaperSpigotWorldConfig(worlddata.getName()); // PaperSpigot
        this.generator = gen;
        this.world = new CraftWorld((WorldServer) this, gen, env);
        this.ticksPerAnimalSpawns = this.getServer().getTicksPerAnimalSpawns(); // CraftBukkit
        this.ticksPerMonsterSpawns = this.getServer().getTicksPerMonsterSpawns(); // CraftBukkit
        // CraftBukkit end
        // Spigot start
        this.chunkTickRadius = (byte) ((this.getServer().getViewDistance() < 7) ? this.getServer().getViewDistance() : 7);
        this.chunkTickList = new gnu.trove.map.hash.TLongShortHashMap(spigotConfig.chunksPerTick * 5, 0.7f, Long.MIN_VALUE, Short.MIN_VALUE);
        this.chunkTickList.setAutoCompactionFactor(0);
        // Spigot end

        this.methodProfiler = methodprofiler;

        this.allowMonsters = true;
        this.allowAnimals = true;
        this.H = new int['\u8000'];
        this.dataManager = idatamanager;
        this.worldData = worlddata;
        this.worldProvider = worldprovider;
        this.isClientSide = false;
        this.N = worldprovider.getWorldBorder();
        // CraftBukkit start
        // Moved from PlayerList
        this.N.a(new IWorldBorderListener() {
            public void a(WorldBorder worldborder, double d0) {
                getServer().getHandle().sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE), World.this);
            }

            public void a(WorldBorder worldborder, double d0, double d1, long i) {
                getServer().getHandle().sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE), World.this);
            }

            public void a(WorldBorder worldborder, double d0, double d1) {
                getServer().getHandle().sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER), World.this);
            }

            public void a(WorldBorder worldborder, int i) {
                getServer().getHandle().sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_TIME), World.this);
            }

            public void b(WorldBorder worldborder, int i) {
                getServer().getHandle().sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS), World.this);
            }

            public void b(WorldBorder worldborder, double d0) {
            }

            public void c(WorldBorder worldborder, double d0) {
            }
        });
        this.getServer().addWorld(this.world);
        // CraftBukkit end
        this.keepSpawnInMemory = this.paperSpigotConfig.keepSpawnInMemory; // PaperSpigot
    }

    public static long chunkToKey(int x, int z) {
        long k = ((((long) x) & 0xFFFF0000L) << 16) | ((((long) x) & 0x0000FFFFL));
        k |= ((((long) z) & 0xFFFF0000L) << 32) | ((((long) z) & 0x0000FFFFL) << 16);
        return k;
    }

    public static int keyToX(long k) {
        return (int) (((k >> 16) & 0xFFFF0000) | (k & 0x0000FFFF));
    }

    public static int keyToZ(long k) {
        return (int) (((k >> 32) & 0xFFFF0000L) | ((k >> 16) & 0x0000FFFF));
    }

    public static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return a(iblockaccess, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public static boolean a(IBlockAccess iblockaccess, int x, int y, int z) {
        IBlockData iblockdata = iblockaccess.getType(x, y, z);
        Block block = iblockdata.getBlock();
        return block.getMaterial().k() &&
                block.d() || (block instanceof BlockStairs ? iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP
                : (block instanceof BlockStepAbstract
                ? iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP
                : (block instanceof BlockHopper || (block instanceof BlockSnow && iblockdata.get(BlockSnow.LAYERS) == 7))));
    }

    public static boolean a(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();
        return block.getMaterial().k() &&
                block.d() || (block instanceof BlockStairs ? iblockdata.get(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP
                : (block instanceof BlockStepAbstract
                ? iblockdata.get(BlockStepAbstract.HALF) == BlockStepAbstract.EnumSlabHalf.TOP
                : (block instanceof BlockHopper || (block instanceof BlockSnow && iblockdata.get(BlockSnow.LAYERS) == 7))));
    }

    public CraftWorld getWorld() {
        return this.world;
    }

    public CraftServer getServer() {
        return (CraftServer) Bukkit.getServer();
    }

    public Chunk getChunkIfLoaded(int x, int z) {
        return ((ChunkProviderServer) this.chunkProvider).getChunkIfLoaded(x, z);
    }

    public Chunk getChunkIfLoaded(long hash) {
        return ((ChunkProviderServer) this.chunkProvider).getChunkIfLoaded(hash);
    }

    public World b() {
        return this;
    }

    public BiomeBase getBiome(BlockPosition blockposition) {
        return getBiome(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.worldProvider.m();
    }

    //Vortex - Start
    public boolean isValidLocation(int x, int y, int z) {
        return x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256;
    }

    private IBlockData getCapturedBlockType(int x, int y, int z) {
        for (BlockState previous : this.capturedBlockStates) {
            if (previous.getX() == x && previous.getY() == y && previous.getZ() == z)
                return CraftMagicNumbers.getBlock(previous.getTypeId()).fromLegacyData(previous.getRawData());
        }
        return null;
    }

    public DifficultyDamageScaler E(int x, int y, int z) {
        long i = 0L;
        float f = 0.0F;

        if (this.isLoaded(x, y, z)) {
            f = this.y();
            i = this.getChunkAtWorldCoords(x, z).w();
        }

        return new DifficultyDamageScaler(this.getDifficulty(), this.getDayTime(), i, f);
    }

    public BlockPosition r(int x, int z) {
        Chunk chunk = this.getChunkAtWorldCoords(x, z);

        int x1;
        int y1;
        int z1;

        int x2;
        int y2;
        int z2;

        for (x1 = x, y1 = chunk.g() + 16, z1 = z; y1 >= 0; x1 = x2, y1 = y2, z1 = z2) {
            x2 = x1;
            y2 = y1 - 1;
            z2 = z1;
            Material material = chunk.getTypeAbs(x2, y2, z2).getMaterial();

            if (material.isSolid() && material != Material.LEAVES) {
                break;
            }
        }

        return new BlockPosition(x1, y1, z1);
    }

    public IBlockData getTypeIfLoaded(int x, int y, int z) {
        if (this.captureTreeGeneration) {
            IBlockData previous = getCapturedBlockType(x, y, z);
            if (previous != null)
                return previous;
        }
        Chunk chunk = ((ChunkProviderServer) this.chunkProvider).getChunkIfLoaded(x >> 4, z >> 4);
        if (chunk != null)
            return chunk.getBlockData(x, y, z);
        return null;
    }

    public boolean areChunksLoadedBetween(int x, int y, int z, int x1, int y1, int z1) {
        return isAreaLoaded(x, y, z, x1, y1, z1, true);
    }

    public boolean isLoaded(int x, int y, int z) {
        return isValidLocation(x, y, z) && isChunkLoaded(x >> 4, z >> 4, true);
    }

    public boolean i(int x,int y, int z) {
        return getChunkAtWorldCoords(x, z).d(x, y, z);
    }

    public int getLightLevel(BlockPosition blockposition) {
        if (InvictusConfig.disableLighting)
            return 15;
        return c(blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);
    }

    public int getLightLevel(int x, int y, int z) {
        if (InvictusConfig.disableLighting)
            return 15;
        return c(x, y, z, true);
    }

    public int c(int x, int y, int z, boolean flag) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (flag && this.getType(x, y, z).getBlock().s()) {

                int i = this.c(x, y + 1, z, false);
                int j = this.c(x + 1, y, z, false);
                int k = this.c(x - 1, y, z, false);
                int l = this.c(x, y, z + 1, false);
                int i1 = this.c(x, y, z - 1, false);

                if (j > i) {
                    i = j;
                }

                if (k > i) {
                    i = k;
                }

                if (l > i) {
                    i = l;
                }

                if (i1 > i) {
                    i = i1;
                }

                return i;
            } else if (y < 0) {
                return 0;
            } else {
                if (y >= 256) {
                    y = 255;
                }

                return getChunkAtWorldCoords(x, z).a(x, y, z, this.I);
            }
        } else {
            return 15;
        }
    }

    public BiomeBase getBiome(int x, int y, int z) {
        if (isValidLocation(x, y, z)) {
            Chunk ch = getChunkIfLoaded(x, z);
            if (ch != null) {
                try {
                    return ch.getBiome(x, z, this.worldProvider.m());
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.a(throwable, "Getting biome");
                    CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Coordinates of biome request");

                    crashreportsystemdetails.a("Location", () -> CrashReportSystemDetails.a(new BlockPosition(x, y, z)));
                    throw new ReportedException(crashreport);
                }
            } else {
                return this.worldProvider.m().getBiome(x, z, BiomeBase.PLAINS);
            }
        } else {
            return this.worldProvider.m().getBiome(x, z, BiomeBase.PLAINS);
        }
    }

    public boolean isValidLocation(int x, int z) { // Vortex - Optimized player ticking
        return x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000;
    }

    public IBlockData getType(int x, int y, int z) {
        return getType(x, y, z, true);
    }

    public Chunk getChunkAtWorldCoords(int x, int z) {
        return this.getChunkAt(x >> 4, z >> 4);
    }

    public IBlockData getType(int x, int y, int z, boolean useCaptured) {
        if (captureTreeGeneration && useCaptured) {
            for (BlockState previous : capturedBlockStates) {
                if (previous.getX() == x && previous.getY() == y && previous.getZ() == z) {
                    return CraftMagicNumbers.getBlock(previous.getTypeId()).fromLegacyData(previous.getRawData());
                }
            }
        }
        if (!this.isValidLocation(x, y, z)) {
            return Blocks.AIR.getBlockData();
        } else {
            return getChunkAtWorldCoords(x, z).getBlockData(x, y, z);
        }
    }

    public void a(EnumSkyBlock enumskyblock, int x, int y, int z, int i) {
        if (isLoaded(x, y, z)) {
            getChunkAtWorldCoords(x, z).a(enumskyblock, x, y, z, i);
            this.n(new BlockPosition(x, y, z));
        }
    }

    private int a(int x, int y, int z, EnumSkyBlock enumskyblock) {
        if (enumskyblock == EnumSkyBlock.SKY && this.i(x, y, z)) {
            return 15;
        } else {
            Block block = this.getType(x, y, z).getBlock();
            int i = enumskyblock == EnumSkyBlock.SKY ? 0 : block.r();
            int j = block.p();

            if (j >= 15 && block.r() > 0) {
                j = 1;
            }

            if (j < 1) {
                j = 1;
            }

            if (j >= 15) {
                return 0;
            } else if (i >= 14) {
                return i;
            } else {
                for (EnumDirection enumdirection : EnumDirection.values()) {
                    int i1 = this.b(enumskyblock, x + enumdirection.getAdjacentX(), y + enumdirection.getAdjacentY(), z + enumdirection.getAdjacentZ()) - j;

                    if (i1 > i) {
                        i = i1;
                    }

                    if (i >= 14) {
                        return i;
                    }
                }

                return i;
            }
        }
    }

    public void applyPhysics(int x, int y, int z, Block block) {
        d(x - 1, y, z, block);
        d(x + 1, y, z, block);
        d(x, y - 1, z, block);
        d(x, y + 1, z, block);
        d(x, y, z - 1, block);
        d(x, y, z + 1, block);
    }

    public void applyPhysics(BlockPosition blockposition, Block block) {
        applyPhysics(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block);
    }

    public IBlockData getType(Chunk chunk, int x, int y, int z, boolean useCaptured) { // Vortex - Optimized block breaking
        if (captureTreeGeneration && useCaptured) {
            for (BlockState previous : capturedBlockStates) {
                if (previous.getX() == x && previous.getY() == y && previous.getZ() == z) {
                    return CraftMagicNumbers.getBlock(previous.getTypeId()).fromLegacyData(previous.getRawData());
                }
            }
        }
        if (!this.isValidLocation(x, y, z)) {
            return Blocks.AIR.getBlockData();
        } else {
            return chunk.getBlockData(x, y, z);
        }
    }

    protected abstract IChunkProvider k();

    public void a(WorldSettings worldsettings) {
        this.worldData.d(true);
    }

    public Block c(BlockPosition blockposition) {
        BlockPosition blockposition1;

        for (blockposition1 = new BlockPosition(blockposition.getX(), this.F(), blockposition.getZ()); !this.isEmpty(blockposition1.up()); blockposition1 = blockposition1.up()) {
        }

        return this.getType(blockposition1).getBlock();
    }

    public boolean isValidLocation(BlockPosition blockposition) {
        return blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000 && blockposition.getY() >= 0 && blockposition.getY() < 256;
    }

    public boolean isEmpty(BlockPosition blockposition) {
        return getType(blockposition).getBlock().getMaterial() == Material.AIR;
    }

    public boolean isEmpty(int x, int y, int z) {
        return getType(x, y, z).getBlock().getMaterial() == Material.AIR;
    }

    public boolean isLoaded(BlockPosition blockposition) {
        return a(blockposition, true);
    }

    public boolean a(BlockPosition blockposition, boolean flag) {
        return isValidLocation(blockposition) && isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4, flag);
    }

    public boolean areChunksLoaded(BlockPosition blockposition, int i) {
        return areChunksLoaded(blockposition, i, true);
    }

    public boolean areChunksLoaded(BlockPosition blockposition, int i, boolean flag) {
        return isAreaLoaded(blockposition.getX() - i, blockposition.getY() - i, blockposition.getZ() - i, blockposition.getX() + i, blockposition.getY() + i, blockposition.getZ() + i, flag);
    }

    public boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1) {
        return areChunksLoadedBetween(blockposition, blockposition1, true);
    }

    public boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1, boolean flag) {
        return isAreaLoaded(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), flag);
    }

    public boolean a(StructureBoundingBox structureboundingbox) {
        return b(structureboundingbox, true);
    }

    public boolean b(StructureBoundingBox structureboundingbox, boolean flag) {
        return isAreaLoaded(structureboundingbox.a, structureboundingbox.b, structureboundingbox.c, structureboundingbox.d, structureboundingbox.e, structureboundingbox.f, flag);
    }

    private boolean isAreaLoaded(int i, int j, int k, int l, int i1, int j1, boolean flag) {
        if (i1 >= 0 && j < 256) {
            i >>= 4;
            k >>= 4;
            l >>= 4;
            j1 >>= 4;

            for (int k1 = i; k1 <= l; ++k1) {
                for (int l1 = k; l1 <= j1; ++l1) {
                    if (!isChunkLoaded(k1, l1, flag)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
    // CraftBukkit end

    protected boolean isChunkLoaded(int i, int j, boolean flag) {
        return chunkProvider.isChunkLoaded(i, j) && (flag || !chunkProvider.getOrCreateChunk(i, j).isEmpty());
    }

    public Chunk getChunkAtWorldCoords(BlockPosition blockposition) {
        return getChunkAt(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }

    public Chunk getChunkAt(int i, int j) {
        return chunkProvider.getOrCreateChunk(i, j);
    }

    public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i) {
        return setTypeAndData(blockposition, iblockdata, i, true, false);
    }

    public boolean setTypeAndDataWithChunk(Chunk chunk, BlockPosition blockposition, IBlockData iblockdata, int i, boolean light, boolean sand) {
        if (this.captureTreeGeneration) {
            BlockState blockstate = null;
            Iterator<BlockState> it = capturedBlockStates.iterator();
            while (it.hasNext()) {
                BlockState previous = it.next();
                if (previous.getX() == blockposition.getX() && previous.getY() == blockposition.getY() && previous.getZ() == blockposition.getZ()) {
                    blockstate = previous;
                    it.remove();
                    break;
                }
            }
            if (blockstate == null) {
                blockstate = org.bukkit.craftbukkit.block.CraftBlockState.getBlockState(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
            }
            blockstate.setTypeId(CraftMagicNumbers.getId(iblockdata.getBlock()));
            blockstate.setRawData((byte) iblockdata.getBlock().toLegacyData(iblockdata));
            capturedBlockStates.add(blockstate);
            return true;
        } else if (!this.isValidLocation(blockposition)) {
            return false;
        } else {
            Block block = iblockdata.getBlock();

            // CraftBukkit start - capture blockstates
            BlockState blockstate = null;
            if (captureBlockStates) {
                blockstate = org.bukkit.craftbukkit.block.CraftBlockState.getBlockState(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
                capturedBlockStates.add(blockstate);
            }
            // CraftBukkit end

            IBlockData iblockdata1 = chunk.a(blockposition, iblockdata, light);

            if (iblockdata1 == null) {
                // CraftBukkit start - remove blockstate if failed
                if (captureBlockStates) {
                    capturedBlockStates.remove(blockstate);
                }
                // CraftBukkit end
                return false;
            } else {
                Block block1 = iblockdata1.getBlock();

                if (light && block.p() != block1.p() || block.r() != block1.r()) {
                    this.x(blockposition);
                }

                // CraftBukkit start
                if (!captureBlockStates) { // Don't notify clients or update physics while capturing blockstates
                    // Modularize client and physic updates
                    if (!sand) notifyAndUpdatePhysics(blockposition, chunk, block1, block, i);
                    else notifyAndUpdatePhysicsSand(blockposition, chunk, block1, block, i);
                }
                // CraftBukkit end

                return true;
            }
        }
    }

    public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i, boolean light, boolean sand) {
        if (this.captureTreeGeneration) {
            BlockState blockstate = null;
            Iterator<BlockState> it = capturedBlockStates.iterator();
            while (it.hasNext()) {
                BlockState previous = it.next();
                if (previous.getX() == blockposition.getX() && previous.getY() == blockposition.getY() && previous.getZ() == blockposition.getZ()) {
                    blockstate = previous;
                    it.remove();
                    break;
                }
            }
            if (blockstate == null) {
                blockstate = org.bukkit.craftbukkit.block.CraftBlockState.getBlockState(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
            }
            blockstate.setTypeId(CraftMagicNumbers.getId(iblockdata.getBlock()));
            blockstate.setRawData((byte) iblockdata.getBlock().toLegacyData(iblockdata));
            this.capturedBlockStates.add(blockstate);
            return true;
        } else if (!this.isValidLocation(blockposition)) {
            return false;
        } else {
            Chunk chunk = this.getChunkAtWorldCoords(blockposition);
            Block block = iblockdata.getBlock();

            // CraftBukkit start - capture blockstates
            BlockState blockstate = null;
            if (captureBlockStates) {
                blockstate = org.bukkit.craftbukkit.block.CraftBlockState.getBlockState(this, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
                capturedBlockStates.add(blockstate);
            }
            // CraftBukkit end

            IBlockData iblockdata1 = chunk.a(blockposition, iblockdata, light);

            if (iblockdata1 == null) {
                // CraftBukkit start - remove blockstate if failed
                if (captureBlockStates) {
                    capturedBlockStates.remove(blockstate);
                }
                // CraftBukkit end
                return false;
            } else {
                Block block1 = iblockdata1.getBlock();

                if (light && block.p() != block1.p() || block.r() != block1.r()) {
                    x(blockposition);
                }

                // CraftBukkit start
                if (!captureBlockStates) { // Don't notify clients or update physics while capturing blockstates
                    // Modularize client and physic updates
                    if (!sand) notifyAndUpdatePhysics(blockposition, chunk, block1, block, i);
                    else notifyAndUpdatePhysicsSand(blockposition, chunk, block1, block, i);
                }
                // CraftBukkit end

                return true;
            }
        }
    }

    // CraftBukkit start - Split off from original setTypeAndData(int i, int j, int k, Block block, int l, int i1) method in order to directly send client and physic updates
    public void notifyAndUpdatePhysics(BlockPosition blockposition, Chunk chunk, Block oldBlock, Block newBlock, int flag) {
        if ((flag & 2) != 0 && (chunk == null || chunk.isReady())) {  // allow chunk to be null here as chunk.isReady() is false when we send our notification during block placement
            notify(blockposition);
        }
        if ((flag & 1) != 0) {
            update(blockposition, oldBlock);
            if (newBlock.isComplexRedstone()) {
                updateAdjacentComparators(blockposition, newBlock);
            }
        }
    }

    public void notifyAndUpdatePhysicsSand(BlockPosition blockposition, Chunk chunk, Block oldBlock, Block newBlock, int flag) {
        if ((flag & 2) != 0 && (chunk == null || chunk.isReady())) {
            notify(blockposition);
        }
        if ((flag & 1) != 0) {
            d(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ(), oldBlock);
        }
    }

    public boolean setAir(BlockPosition blockposition) {
        return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
    }

    public boolean setAir(BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = this.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block.getMaterial() == Material.AIR) {
            return false;
        } else {
            this.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
            if (flag) {
                block.b(this, blockposition, iblockdata, 0);
            }

            return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
        }
    }

    public boolean setTypeUpdate(BlockPosition blockposition, IBlockData iblockdata) {
        return this.setTypeAndData(blockposition, iblockdata, 3);
    }

    public void notify(BlockPosition blockposition) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.a(blockposition);
        }
    }

    public void update(BlockPosition blockposition, Block block) {
        // CraftBukkit start
        if (populating) {
            return;
        }
        // CraftBukkit end
        applyPhysics(blockposition, block);
    }

    public void a(int i, int j, int k, int l) {
        int i1;

        if (k > l) {
            i1 = l;
            l = k;
            k = i1;
        }

        if (!worldProvider.o()) {
            for (i1 = k; i1 <= l; ++i1) {
                updateLight(EnumSkyBlock.SKY, i, i1, j); // PaperSpigot - Asynchronous lighting updates
            }
        }

        this.b(i, k, j, i, l, j);
    }

    public void b(BlockPosition blockposition, BlockPosition blockposition1) {
        this.b(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
    }

    public void b(int i, int j, int k, int l, int i1, int j1) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.a(i, j, k, l, i1, j1);
        }
    }

    public void a(int x, int y, int z, Block block, EnumDirection enumdirection) {
        if (enumdirection != EnumDirection.WEST)
            d(x - 1, y, z, block);
        if (enumdirection != EnumDirection.EAST)
            d(x + 1, y, z, block);
        if (enumdirection != EnumDirection.DOWN)
            d(x, y - 1, z, block);
        if (enumdirection != EnumDirection.UP)
            d(x, y + 1, z, block);
        if (enumdirection != EnumDirection.NORTH)
            d(x, y, z - 1, block);
        if (enumdirection != EnumDirection.SOUTH)
            d(x, y, z + 1, block);
    }

    public void redstonePhysics(int x, int y, int z, Block block, EnumDirection enumdirection) {
        if (enumdirection != EnumDirection.WEST)
            redstonePhysics(x - 1, y, z, block);
        if (enumdirection != EnumDirection.EAST)
            redstonePhysics(x + 1, y, z, block);
        if (enumdirection != EnumDirection.DOWN)
            redstonePhysics(x, y - 1, z, block);
        if (enumdirection != EnumDirection.UP)
            redstonePhysics(x, y + 1, z, block);
        if (enumdirection != EnumDirection.NORTH)
            redstonePhysics(x, y, z - 1, block);
        if (enumdirection != EnumDirection.SOUTH)
            redstonePhysics(x, y, z + 1, block);
    }

    public void a(BlockPosition blockposition, Block block, EnumDirection enumdirection) {
        a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block, enumdirection);
    }

    public void d(BlockPosition blockposition, Block block) {
        d(blockposition.getX(), blockposition.getY(), blockposition.getZ(), block);
    }

    public void redstonePhysics(int x, int y, int z, Block block) {
        Chunk chunk = getChunkAtWorldCoords(x, z);
        IBlockData iblockdata = getType(chunk, x, y, z, true);
        try {
            CraftWorld world = getWorld();
            if (InvictusConfig.blockPhysicsEvent && world != null
                    && (InvictusConfig.redstoneBlockPhysicsEvent || !(block instanceof BlockRedstoneWire || block instanceof BlockRedstoneTorch || block instanceof BlockRepeater))
                    && (InvictusConfig.sandBlockPhysicsEvent || !(block instanceof BlockFalling))) {
                BlockPhysicsEvent event = new BlockPhysicsEvent(world.getBlockAt(x, y, z), CraftMagicNumbers.getId(block));
                this.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
            }
            Block iblock = iblockdata.getBlock();
            if(iblock instanceof BlockDiodeAbstract) {
                ((BlockDiodeAbstract)iblock).doRedstonePhysics(this, chunk, new BlockPosition(x, y, z), iblockdata, block);
            } else {
                iblock.doPhysics(this, new BlockPosition(x, y, z), iblockdata, block);
            }
        } catch (StackOverflowError stackoverflowerror) { // Spigot Start
            haveWeSilencedAPhysicsCrash = true;
            blockLocation = x + ", " + y + ", " + z; // Spigot End
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Exception while updating neighbours");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being updated");

            crashreportsystemdetails.a("Source block type", new Callable() {
                public String a() {
                    try {
                        return String.format("ID #%d (%s // %s)", Block.getId(block),
                                block.a(), block.getClass().getCanonicalName());
                    } catch (Throwable throwable) {
                        return "ID #" + Block.getId(block);
                    }
                }

                public Object call() {
                    return this.a();
                }
            });
            CrashReportSystemDetails.a(crashreportsystemdetails, null, iblockdata);
            throw new ReportedException(crashreport);
        }

    }

    public void d(int x, int y, int z, Block block) {
        IBlockData iblockdata = this.getType(x, y, z);
        try {
            CraftWorld world = getWorld();
            if (InvictusConfig.blockPhysicsEvent && world != null
                    && (InvictusConfig.redstoneBlockPhysicsEvent || !(block instanceof BlockRedstoneWire || block instanceof BlockRedstoneTorch || block instanceof BlockRepeater))
                    && (InvictusConfig.sandBlockPhysicsEvent || !(block instanceof BlockFalling))) {
                BlockPhysicsEvent event = new BlockPhysicsEvent(world.getBlockAt(x, y, z), CraftMagicNumbers.getId(block));
                this.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
            }
            iblockdata.getBlock().doPhysics(this, new BlockPosition(x, y, z), iblockdata, block);
        } catch (StackOverflowError stackoverflowerror) { // Spigot Start
            haveWeSilencedAPhysicsCrash = true;
            blockLocation = x + ", " + y + ", " + z; // Spigot End
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Exception while updating neighbours");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being updated");

            crashreportsystemdetails.a("Source block type", new Callable() {
                public String a() {
                    try {
                        return String.format("ID #%d (%s // %s)", Block.getId(block),
                                block.a(), block.getClass().getCanonicalName());
                    } catch (Throwable throwable) {
                        return "ID #" + Block.getId(block);
                    }
                }

                public Object call() {
                    return this.a();
                }
            });
            CrashReportSystemDetails.a(crashreportsystemdetails, null, iblockdata);
            throw new ReportedException(crashreport);
        }

    }

    public boolean a(BlockPosition blockposition, Block block) {
        return false;
    }

    public boolean i(BlockPosition blockposition) {
        return i(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public boolean j(BlockPosition blockposition) {
        int x = blockposition.getX(), y = blockposition.getY(), z = blockposition.getZ();
        if (y >= a) {
            return this.i(x, y, z);
        } else {
            int y1 = a;

            if (!this.i(x, y1, z)) {
                return false;
            } else {
                for (y1 -= 1; y1 > y; y1 -= 1) {
                    Block block = this.getType(x, y1, z).getBlock();

                    if (block.p() > 0 && !block.getMaterial().isLiquid()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public int k(int x, int y, int z) {
        if (y < 0) {
            return 0;
        } else {
            if (y >= 256)
                y = 255;
            return this.getChunkAtWorldCoords(x, z).a(x, y, z, 0);
        }
    }

    public int k(BlockPosition blockposition) {
        return k(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public int c(BlockPosition blockposition, boolean flag) {
        return c(blockposition.getX(), blockposition.getY(), blockposition.getZ(), flag);
    }

    public BlockPosition getHighestBlockYAt(BlockPosition blockposition) {
        int i = 0;
        int x = blockposition.getX(), z = blockposition.getZ();
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            Chunk chunk = getChunkIfLoaded(x >> 4, z >> 4);
            if (chunk != null) {
                i = chunk.b(x & 15, z & 15);
            }
        } else {
            i = this.F() + 1;
        }

        return new BlockPosition(x, i, z);
    }

    public boolean areChunksLoaded(int x, int y, int z, int i) {
        return isAreaLoaded(x - i, y - i, z - i, x + i, y + i, z + i, true);
    }

    public int getHighestBlockYAt(int x, int z, boolean chunkAlreadyLoaded) { // Vortex - Optimized chunks light uodates
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (!chunkAlreadyLoaded && !chunkProvider.isChunkLoaded(x >> 4, z >> 4)) { // Poweruser
                return 0;
            } else {
                return getChunkAt(x >> 4, z >> 4).b(x & 15, z & 15);
            }
        } else {
            return F() + 1;
        }
    }

    public int b(int i, int j) {
        return b(i, j, false);
    }

    public int b(int i, int j, boolean chunkAlreadyLoaded) { // Vortex - Optimized chunks light uodates
        if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
            if (!chunkAlreadyLoaded && !chunkProvider.isChunkLoaded(i >> 4, j >> 4)) {
                return 0;
            } else {
                return getChunkAt(i >> 4, j >> 4).v();
            }
        } else {
            return this.F() + 1;
        }
    }

    public int b(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        return b(enumskyblock, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public int b(EnumSkyBlock enumskyblock, int x, int y, int z) {
        if (y < 0)
            y = 0;
        if (!this.isLoaded(x, y, z)) {
            return enumskyblock.c;
        } else {
            return getChunkAtWorldCoords(x, z).getBrightness(enumskyblock, x, y, z);
        }
    }

    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
        if (isLoaded(blockposition)) {
            getChunkAtWorldCoords(blockposition).a(enumskyblock, blockposition, i);
            this.n(blockposition);
        }
    }

    public void n(BlockPosition blockposition) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.b(blockposition);
        }
    }

    public float o(BlockPosition blockposition) {
        return o(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public float o(int x, int y, int z) {
        return worldProvider.p()[getLightLevel(x, y, z)];
    }

    // Spigot start
    public IBlockData getType(BlockPosition blockposition) {
        return getType(blockposition, true);
    }

    public IBlockData getType(BlockPosition blockposition, boolean useCaptured) {
        // CraftBukkit start - tree generation
        if (captureTreeGeneration && useCaptured) {
            // Spigot end
            for (BlockState previous : capturedBlockStates) {
                if (previous.getX() == blockposition.getX() && previous.getY() == blockposition.getY() && previous.getZ() == blockposition.getZ()) {
                    return CraftMagicNumbers.getBlock(previous.getTypeId()).fromLegacyData(previous.getRawData());
                }
            }
        }
        // CraftBukkit end
        if (!this.isValidLocation(blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            return getChunkAtWorldCoords(blockposition).getBlockData(blockposition);
        }
    }

    public boolean w() {
        return this.I < 4;
    }

    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1) {
        return this.rayTrace(vec3d, vec3d1, false, false, false);
    }

    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag) {
        return this.rayTrace(vec3d, vec3d1, flag, false, false);
    }

    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag, boolean flag1, boolean flag2) {
        if (!Double.isNaN(vec3d.a) && !Double.isNaN(vec3d.b) && !Double.isNaN(vec3d.c)) {
            if (!Double.isNaN(vec3d1.a) && !Double.isNaN(vec3d1.b) && !Double.isNaN(vec3d1.c)) {
                int i = MathHelper.floor(vec3d1.a);
                int j = MathHelper.floor(vec3d1.b);
                int k = MathHelper.floor(vec3d1.c);
                int l = MathHelper.floor(vec3d.a);
                int i1 = MathHelper.floor(vec3d.b);
                int j1 = MathHelper.floor(vec3d.c);
                BlockPosition blockposition = new BlockPosition(l, i1, j1);
                IBlockData iblockdata = getType(blockposition);
                Block block = iblockdata.getBlock();
                if ((!flag1 || block.a(this, blockposition, iblockdata) != null) && block.a(iblockdata, flag)) {
                    MovingObjectPosition movingobjectposition = block.a(this, blockposition, vec3d, vec3d1);
                    if (movingobjectposition != null)
                        return movingobjectposition;
                }
                MovingObjectPosition movingobjectposition1 = null;
                int k1 = 200;
                while (k1-- >= 0) {
                    EnumDirection enumdirection;
                    if (Double.isNaN(vec3d.a) || Double.isNaN(vec3d.b) || Double.isNaN(vec3d.c))
                        return null;
                    if (l == i && i1 == j && j1 == k)
                        return flag2 ? movingobjectposition1 : null;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    boolean flag5 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;
                    if (i > l) {
                        d0 = l + 1.0D;
                    } else if (i < l) {
                        d0 = l + 0.0D;
                    } else {
                        flag3 = false;
                    }
                    if (j > i1) {
                        d1 = i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = i1 + 0.0D;
                    } else {
                        flag4 = false;
                    }
                    if (k > j1) {
                        d2 = j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = j1 + 0.0D;
                    } else {
                        flag5 = false;
                    }
                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec3d1.a - vec3d.a;
                    double d7 = vec3d1.b - vec3d.b;
                    double d8 = vec3d1.c - vec3d.c;
                    if (flag3)
                        d3 = (d0 - vec3d.a) / d6;
                    if (flag4)
                        d4 = (d1 - vec3d.b) / d7;
                    if (flag5)
                        d5 = (d2 - vec3d.c) / d8;
                    if (d3 == -0.0D)
                        d3 = -1.0E-4D;
                    if (d4 == -0.0D)
                        d4 = -1.0E-4D;
                    if (d5 == -0.0D)
                        d5 = -1.0E-4D;
                    if (d3 < d4 && d3 < d5) {
                        enumdirection = (i > l) ? EnumDirection.WEST : EnumDirection.EAST;
                        vec3d = new Vec3D(d0, vec3d.b + d7 * d3, vec3d.c + d8 * d3);
                    } else if (d4 < d5) {
                        enumdirection = (j > i1) ? EnumDirection.DOWN : EnumDirection.UP;
                        vec3d = new Vec3D(vec3d.a + d6 * d4, d1, vec3d.c + d8 * d4);
                    } else {
                        enumdirection = (k > j1) ? EnumDirection.NORTH : EnumDirection.SOUTH;
                        vec3d = new Vec3D(vec3d.a + d6 * d5, vec3d.b + d7 * d5, d2);
                    }
                    l = MathHelper.floor(vec3d.a) - ((enumdirection == EnumDirection.EAST) ? 1 : 0);
                    i1 = MathHelper.floor(vec3d.b) - ((enumdirection == EnumDirection.UP) ? 1 : 0);
                    j1 = MathHelper.floor(vec3d.c) - ((enumdirection == EnumDirection.SOUTH) ? 1 : 0);
                    blockposition = new BlockPosition(l, i1, j1);
                    IBlockData iblockdata1 = getType(blockposition);
                    Block block1 = iblockdata1.getBlock();
                    if (!flag1 || block1.a(this, blockposition, iblockdata1) != null) {
                        if (block1.a(iblockdata1, flag)) {
                            MovingObjectPosition movingobjectposition2 = block1.a(this, blockposition, vec3d, vec3d1);
                            if (movingobjectposition2 != null)
                                return movingobjectposition2;
                            continue;
                        }
                        movingobjectposition1 = new MovingObjectPosition(MovingObjectPosition.EnumMovingObjectType.MISS, vec3d, enumdirection, blockposition);
                    }
                }
                return flag2 ? movingobjectposition1 : null;
            }
            return null;
        }
        return null;
    }

    public void interceptSounds() {
        interceptSounds = true;
    }

    public void sendInterceptedSounds() {
        for (Runnable r : interceptedSounds) {
            r.run();
        }
        interceptedSounds.clear();
        interceptSounds = false;
    }

    public void clearInterceptedSounds() {
        interceptedSounds.clear();
        interceptSounds = false;
    }

    public void makeSound(double d0, double d1, double d2, String s, float f, float f1) {
        if (interceptSounds && org.bukkit.Bukkit.isPrimaryThread()) {
            interceptedSounds.add(() -> {
                for (IWorldAccess w : u) {
                    w.a(s, d0, d1, d2, f, f1);
                }
            });
            return;
        }
        // Vortex end
        for (IWorldAccess w : u) {
            w.a(s, d0, d1, d2, f, f1);
        }
    }

    public void makeSound(Entity entity, String s, float f, float f1) {
        if (entity instanceof EntityHuman) {
            for (IWorldAccess anU : this.u)
                anU.a((EntityHuman) entity, s, entity.locX, entity.locY, entity.locZ, f, f1);
        } else {
            for (IWorldAccess anU : this.u)
                anU.a(s, entity.locX, entity.locY, entity.locZ, f, f1);
        }
    }

    public void a(EntityHuman entityhuman, String s, float f, float f1) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.a(entityhuman, s, entityhuman.locX, entityhuman.locY, entityhuman.locZ, f, f1);
        }

    }

    public void a(BlockPosition blockposition, String s) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.a(s, blockposition);
        }

    }

    public void addParticle(EnumParticle enumparticle, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {
        this.a(enumparticle.c(), enumparticle.e(), d0, d1, d2, d3, d4, d5, aint);
    }

    private void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.a(i, flag, d0, d1, d2, d3, d4, d5, aint);
        }

    }

    public boolean strikeLightning(Entity entity) {
        k.add(entity);
        return true;
    }

    public boolean addEntity(EntityPlayer entity) { // Vortex - addEntity for players to have less checks
        if (entity == null)
            return false;

        this.players.add(entity);
        this.everyoneSleeping();

        this.getChunkAt(MathHelper.floor(entity.locX / 16.0D), MathHelper.floor(entity.locZ / 16.0D)).a(entity);
        this.entityList.add(entity);
        this.a(entity);
        return true;
    }

    public boolean addEntity(EntityFallingBlock entity) { // Vortex - addEntity for sand to have less checks
        if (entity == null)
            return false;
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);
        Chunk chunk = getChunkIfLoaded(i, j);

        if (!entity.attachedToPlayer && chunk == null) {
            entity.dead = true;
            return false;
        } else {
            chunk.addCannonEntity(entity);
            this.entityList.add(entity);
            this.a(entity);
            return true;
        }
    }

    public boolean addEntity(EntityTNTPrimed entity) { // Vortex - addEntity for tnt to have less checks
        if (entity == null)
            return false;
        // CraftBukkit end
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);

        Chunk chunk = getChunkIfLoaded(i, j);

        if (!entity.attachedToPlayer && chunk == null) {
            entity.dead = true;
            return false;
        } else {
            chunk.addCannonEntity(entity);
            this.entityList.add(entity);
            this.a(entity);
            return true;
        }
    }

    public boolean addEntity(Entity entity) {
        // CraftBukkit start - Used for entities other than creatures
        return addEntity(entity, SpawnReason.DEFAULT);
    }

    public boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
        AsyncCatcher.catchOp("entity add");
        if (entity == null)
            return false;
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);
        boolean flag = entity.attachedToPlayer;
        if (entity instanceof EntityHuman)
            flag = true;
        Cancellable event = null;
        if (entity instanceof EntityLiving && !(entity instanceof EntityPlayer)) {
            boolean isAnimal = (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal || entity instanceof EntityGolem);
            boolean isMonster = (entity instanceof EntityMonster || entity instanceof EntityGhast || entity instanceof EntitySlime);
            if (spawnReason != CreatureSpawnEvent.SpawnReason.CUSTOM && ((
                    isAnimal && !this.allowAnimals) || (isMonster && !this.allowMonsters))) {
                entity.dead = true;
                return false;
            }
            event = CraftEventFactory.callCreatureSpawnEvent((EntityLiving) entity, spawnReason);
        } else if (entity instanceof EntityItem) {
            event = CraftEventFactory.callItemSpawnEvent((EntityItem) entity);
        } else if (entity.getBukkitEntity() instanceof org.bukkit.entity.Projectile) {
            event = CraftEventFactory.callProjectileLaunchEvent(entity);
        } else if (entity instanceof EntityExperienceOrb) {
            EntityExperienceOrb xp = (EntityExperienceOrb) entity;
            double radius = this.spigotConfig.expMerge;
            if (radius > 0.0D) {
                List<Entity> entities = getEntities(entity, entity.getBoundingBox().grow(radius, radius, radius));
                for (Entity e : entities) {
                    if (e instanceof EntityExperienceOrb) {
                        EntityExperienceOrb loopItem = (EntityExperienceOrb) e;
                        if (!loopItem.dead) {
                            xp.value += loopItem.value;
                            loopItem.die();
                        }
                    }
                }
            }
        }
        if (event != null && (event.isCancelled() || entity.dead)) {
            entity.dead = true;
            return false;
        }
        if (!flag && !isChunkLoaded(i, j, true)) {
            entity.dead = true;
            return false;
        }
        if (entity instanceof EntityHuman) {
            players.add((EntityHuman) entity);
            everyoneSleeping();
        }
        getChunkAt(i, j).a(entity);
        entityList.add(entity);
        a(entity);
        return true;
    }

    protected void a(Entity entity) {
        for (IWorldAccess iWorldAccess : u) {
            iWorldAccess.a(entity);
        }

        entity.valid = true; // CraftBukkit
    }

    protected void b(Entity entity) {
        for (IWorldAccess iWorldAccess : u) {
            iWorldAccess.b(entity);
        }

        entity.valid = false; // CraftBukkit
    }

    public void kill(Entity entity) {
        if (entity.passenger != null) {
            entity.passenger.mount(null);
        }

        if (entity.vehicle != null) {
            entity.mount(null);
        }

        entity.die();
        if (entity instanceof EntityHuman) {
            this.players.remove(entity);
            // Spigot start
            for (Object o : worldMaps.c) {
                if (o instanceof WorldMap) {
                    WorldMap map = (WorldMap) o;
                    map.i.remove(entity);
                    map.g.removeIf(worldMapHumanTracker -> worldMapHumanTracker.trackee == entity);
                }
            }
            // Spigot end
            this.everyoneSleeping();
            this.b(entity);
        }

    }

    public void removeEntity(Entity entity) {
        entity.die();
        if (entity instanceof EntityHuman) {
            players.remove(entity);
            everyoneSleeping();
        }

        if (!guardEntityList) { // Spigot - It will get removed after the tick if we are ticking
            int i = entity.ae;
            int j = entity.ag;

            if (entity.ad) {
                Chunk chunk = getChunkIfLoaded(i, j);
                if (chunk != null)
                    chunk.b(entity);
            }

            // CraftBukkit start - Decrement loop variable field if we've already ticked this entity
            int index = this.entityList.indexOf(entity);
            if (index != -1) {
                if (index <= this.tickPosition) {
                    this.tickPosition--;
                }
                this.entityList.remove(index);
            }
            // CraftBukkit end
        } // Spigot
        this.b(entity);
    }

    public void addIWorldAccess(IWorldAccess iworldaccess) {
        this.u.add(iworldaccess);
    }

    public List<AxisAlignedBB> getCubes(Entity entity, AxisAlignedBB axisalignedbb) {
        List<AxisAlignedBB> arraylist = Lists.newArrayList();
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);
        WorldBorder worldborder = this.getWorldBorder();
        boolean flag = entity.aT();
        boolean flag1 = this.a(worldborder, entity);

        boolean collides = entity.world.paperSpigotConfig.fallingBlocksCollideWithSigns && entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock;

        // Spigot start
        int ystart = Math.max((k - 1), 0);
        for (int chunkx = (i >> 4); chunkx <= ((j - 1) >> 4); chunkx++) {
            int cx = chunkx << 4;
            for (int chunkz = (i1 >> 4); chunkz <= ((j1 - 1) >> 4); chunkz++) {
                Chunk chunk = this.getChunkIfLoaded(chunkx, chunkz);
                if (chunk == null) {
                    // PaperSpigot start
                    if (entity.loadChunks) {
                        chunk = ((ChunkProviderServer) entity.world.chunkProvider).getChunkAt(chunkx, chunkz);
                    } else {
                        entity.inUnloadedChunk = true; // PaperSpigot - Remove entities in unloaded chunks
                        continue;
                    }
                    // PaperSpigot end
                }
                int cz = chunkz << 4;
                // Compute ranges within chunk
                int xstart = Math.max(i, cx);
                int xend = Math.min(j, (cx + 16));
                int zstart = Math.max(i1, cz);
                int zend = Math.min(j1, (cz + 16));
                // Loop through blocks within chunk
                for (int x = xstart; x < xend; x++) {
                    for (int z = zstart; z < zend; z++) {
                        for (int y = ystart; y < l; y++) {
                            if (flag && flag1) {
                                entity.h(false);
                            } else if (!flag && !flag1) {
                                entity.h(true);
                            }

                            IBlockData block;
                            if (!this.getWorldBorder().a(x, z) && flag1) {
                                block = Blocks.STONE.getBlockData();
                            } else {
                                block = chunk.getBlockData(x, y, z);
                            }
                            if (block != null) {
                                // PaperSpigot start - FallingBlocks and TNT collide with specific non-collidable blocks
                                Block b = block.getBlock();
                                if (collides && (b instanceof BlockSign || b instanceof BlockFenceGate || b instanceof BlockTorch || b instanceof BlockButtonAbstract || b instanceof BlockLever || b instanceof BlockTripwireHook || b instanceof BlockTripwire || b instanceof BlockChest || b instanceof BlockSlowSand || b instanceof BlockBed || b instanceof BlockEnderChest || b instanceof BlockEnchantmentTable || b instanceof BlockBrewingStand)) {
                                    AxisAlignedBB aabb = AxisAlignedBB.a(x, y, z, x + 1.0, y + 1.0, z + 1.0);
                                    if (axisalignedbb.b(aabb))
                                        arraylist.add(aabb);
                                } else {
                                    b.a(this, new BlockPosition(x, y, z), block, axisalignedbb, arraylist, entity);
                                }
                                // PaperSpigot end
                            }
                        }
                    }
                }
            }
        }
        // Spigot end

        return arraylist;
    }

    public boolean a(WorldBorder worldborder, Entity entity) {
        double d0 = worldborder.b();
        double d1 = worldborder.c();
        double d2 = worldborder.d();
        double d3 = worldborder.e();

        if (entity.aT()) {
            ++d0;
            ++d1;
            --d2;
            --d3;
        } else {
            --d0;
            --d1;
            ++d2;
            ++d3;
        }

        return entity.locX > d0 && entity.locX < d2 && entity.locZ > d1 && entity.locZ < d3;
    }

    public List<AxisAlignedBB> a(AxisAlignedBB axisalignedbb) {
        List<AxisAlignedBB> arraylist = Lists.newArrayList();
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (this.isLoaded(k1, 64, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        blockposition_mutableblockposition.c(k1, i2, l1);
                        IBlockData iblockdata;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
                            iblockdata = this.getType(blockposition_mutableblockposition);
                        } else {
                            iblockdata = Blocks.BEDROCK.getBlockData();
                        }

                        iblockdata.getBlock().a(this, blockposition_mutableblockposition, iblockdata, axisalignedbb, arraylist, null);
                    }
                }
            }
        }

        return arraylist;
    }

    public int a(float f) {
        float f1 = this.c(f);
        float f2 = 1.0F - (MathHelper.cos(f1 * 3.1415927F * 2.0F) * 2.0F + 0.5F);

        f2 = MathHelper.a(f2, 0.0F, 1.0F);
        f2 = 1.0F - f2;
        f2 = (float) ((double) f2 * (1.0D - (double) (this.j(f) * 5.0F) / 16.0D));
        f2 = (float) ((double) f2 * (1.0D - (double) (this.h(f) * 5.0F) / 16.0D));
        f2 = 1.0F - f2;
        return (int) (f2 * 11.0F);
    }

    public float c(float f) {
        return this.worldProvider.a(this.worldData.getDayTime(), f);
    }

    public float y() {
        return WorldProvider.a[this.worldProvider.a(this.worldData.getDayTime())];
    }

    public float d(float f) {
        return c(f) * 3.1415927F * 2.0F;
    }

    public BlockPosition q(BlockPosition blockposition) {
        return getChunkAtWorldCoords(blockposition).h(blockposition);
    }

    public BlockPosition q(int x, int z) {
        return getChunkAtWorldCoords(x, z).h(x, z);
    }

    public BlockPosition r(BlockPosition blockposition) {
        Chunk chunk = this.getChunkAtWorldCoords(blockposition);

        BlockPosition blockposition1;
        BlockPosition blockposition2;

        for (blockposition1 = new BlockPosition(blockposition.getX(), chunk.g() + 16, blockposition.getZ()); blockposition1.getY() >= 0; blockposition1 = blockposition2) {
            blockposition2 = blockposition1.down();
            Material material = chunk.getType(blockposition2).getMaterial();

            if (material.isSolid() && material != Material.LEAVES) {
                break;
            }
        }

        return blockposition1;
    }

    public void a(BlockPosition blockposition, Block block, int i) {
    }

    public void a(BlockPosition blockposition, Block block, int i, int j) {
    }

    public void b(BlockPosition blockposition, Block block, int i, int j) {
    }

    public void tickEntities() {
        int i;
        Entity entity;
        CrashReport crashreport;
        CrashReportSystemDetails crashreportsystemdetails;

        for (i = 0; i < k.size(); ++i) {
            entity = k.get(i);

            if (entity == null) {
                continue;
            }

            try {
                ++entity.ticksLived;
                entity.t_();
            } catch (Throwable throwable) {
                crashreport = CrashReport.a(throwable, "Ticking entity");
                crashreportsystemdetails = crashreport.a("Entity being ticked");
                entity.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            if (entity.dead) {
                k.remove(i--);
            }
        }

        entityList.removeAll(g);

        int j;
        int k;

        // Paper start - Set based removal lists
        for (Entity e : g) {
            j = e.ae;
            k = e.ag;
            if (e.ad) {
                Chunk chunk = getChunkIfLoaded(j, k);
                if (chunk != null)
                    chunk.b(e);
            }
        }

        for (Entity e : g) {
            b(e);
        }
        // Paper end

        g.clear();

        ActivationRange.activateEntities(this); // Spigot
        guardEntityList = true; // Spigot

        Entity lastEntity = null;
        for (tickPosition = 0; tickPosition < entityList.size(); tickPosition++) {
            entity = entityList.get(tickPosition);

            if (entity.vehicle != null) {
                if (!entity.vehicle.dead && entity.vehicle.passenger == entity) {
                    continue;
                }

                entity.vehicle.passenger = null;
                entity.vehicle = null;
            }

            if (!entity.dead) {
                try {
                    if (lastEntity != null && entity.merge(lastEntity)) {
                        entity.die();
                        lastEntity.potential += entity.potential;
                    } else {
                        lastEntity = entity;
                        g(entity);
                    }
                } catch (Throwable throwable1) {
                    String msg = "Entity threw exception at " + entity.world.getWorld().getName() + ":" + entity.locX + "," + entity.locY + "," + entity.locZ;
                    System.err.println(msg);
                    throwable1.printStackTrace();
                    entity.dead = true;
                }
            }

            if (entity.dead) {
                j = entity.ae;
                k = entity.ag;
                if (entity.ad) {
                    Chunk chunk = getChunkIfLoaded(j, k);
                    if (chunk != null)
                        chunk.b(entity);
                }

                guardEntityList = false; // Spigot
                entityList.remove(tickPosition--); // CraftBukkit - Use field for loop variable
                guardEntityList = true; // Spigot
                b(entity);
            }

        }
        guardEntityList = false; // Spigot

        M = true;

        // CraftBukkit start - From below, clean up tile entities before ticking them
        if (!c.isEmpty()) {
            tileEntityList.removeAll(c);
            c.clear();
        }

        // CraftBukkit end

        // Spigot start
        for (tileTickPosition = 0; tileTickPosition < tileEntityList.size(); tileTickPosition++) { // PaperSpigot - Disable tick limiters

            TileEntity tileentity = tileEntityList.get(tileTickPosition);

            // Spigot start
            if (tileentity == null) {
                getServer().getLogger().severe("Spigot has detected a null entity and has removed it, preventing a crash");
                tileEntityList.remove(tileTickPosition--);
                continue;
            }
            // Spigot end

            if (!tileentity.x() && tileentity.t()) {
                BlockPosition blockposition = tileentity.getPosition();

                if (isLoaded(blockposition) && N.a(blockposition)) {
                    try {
                        ((IUpdatePlayerListBox) tileentity).c();
                    } catch (Throwable throwable2) {
                        // PaperSpigot start - Prevent tile entity and entity crashes
                        String msg = "TileEntity threw exception at " + tileentity.world.getWorld().getName() + ":" + tileentity.position.getX() + "," + tileentity.position.getY() + "," + tileentity.position.getZ();
                        System.err.println(msg);
                        throwable2.printStackTrace();
                        getServer().getPluginManager().callEvent(new ServerExceptionEvent(new ServerInternalException(msg, throwable2)));
                        tileEntityList.remove(tileTickPosition--);
                        continue;
                        // PaperSpigot end
                    }
                }
            }

            if (tileentity.x()) {
                tileEntityList.remove(tileTickPosition--);
                BlockPosition position = tileentity.getPosition();
                if (isLoaded(position)) {
                    getChunkAtWorldCoords(position).e(position);
                }
            }
        }

        M = false;

        if (!b.isEmpty()) {
            for (TileEntity tileentity1 : b) {
                if (!tileentity1.x()) {
                    BlockPosition position = tileentity1.getPosition();
                    if (isLoaded(position)) {
                        getChunkAtWorldCoords(position).a(position, tileentity1);
                    }

                    notify(position);
                }
            }

            b.clear();
        }
    }

    public boolean a(TileEntity tileentity) {
        if (tileentity instanceof IUpdatePlayerListBox) {
            this.tileEntityList.add(tileentity);
        }
        return true;
    }

    public void a(Collection<TileEntity> collection) {
        if (this.M) {
            this.b.addAll(collection);
        } else {

            for (TileEntity tileentity : collection) {
                if (tileentity instanceof IUpdatePlayerListBox) {
                    this.tileEntityList.add(tileentity);
                }
            }
        }

    }

    public void g(Entity entity) {
        entityJoinedWorld(entity, true);
    }

    public void entityJoinedWorld(Entity entity, boolean flag) {
        int i = MathHelper.floor(entity.locX);
        int j = MathHelper.floor(entity.locZ);
        if (!ActivationRange.checkIfActive(entity)) {
            entity.ticksLived++;
            entity.inactiveTick();
            if (!isChunkLoaded(i, j, true) && ((entity instanceof EntityEnderPearl && this.paperSpigotConfig.removeUnloadedEnderPearls) || (entity instanceof EntityFallingBlock && this.paperSpigotConfig.removeUnloadedFallingBlocks) || (entity instanceof EntityTNTPrimed && this.paperSpigotConfig.removeUnloadedTNTEntities))) {
                entity.inUnloadedChunk = true;
                entity.die();
            }
        } else {
            entity.P = entity.locX;
            entity.Q = entity.locY;
            entity.R = entity.locZ;
            entity.lastYaw = entity.yaw;
            entity.lastPitch = entity.pitch;
            if (flag && entity.ad) {
                entity.ticksLived++;
                if (entity.vehicle != null) {
                    entity.ak();
                } else {
                    entity.t_();
                }
            }
            if (Double.isNaN(entity.locX) || Double.isInfinite(entity.locX))
                entity.locX = entity.P;
            if (Double.isNaN(entity.locY) || Double.isInfinite(entity.locY))
                entity.locY = entity.Q;
            if (Double.isNaN(entity.locZ) || Double.isInfinite(entity.locZ))
                entity.locZ = entity.R;
            if (Double.isNaN(entity.pitch) || Double.isInfinite(entity.pitch))
                entity.pitch = entity.lastPitch;
            if (Double.isNaN(entity.yaw) || Double.isInfinite(entity.yaw))
                entity.yaw = entity.lastYaw;
            int k = MathHelper.floor(entity.locX / 16.0D);
            int l = MathHelper.floor(entity.locY / 16.0D);
            int i1 = MathHelper.floor(entity.locZ / 16.0D);
            if (!entity.ad || entity.ae != k || entity.af != l || entity.ag != i1) {

                if (entity.loadChunks)
                    entity.loadChunks();

                if (entity.ad) {
                    Chunk chunk = getChunkIfLoaded(entity.ae, entity.ag);
                    if (chunk != null)
                        chunk.a(entity, entity.af);
                }

                Chunk chunk = getChunkIfLoaded(k, i1);
                if (chunk != null) {
                    entity.ad = true;
                    chunk.a(entity);
                } else {
                    entity.ad = false;
                }

            }
            if (flag && entity.ad && entity.passenger != null) {
                if (!entity.passenger.dead && entity.passenger.vehicle == entity) {
                    g(entity.passenger);
                } else {
                    entity.passenger.vehicle = null;
                    entity.passenger = null;
                }
            }
        }
    }

    public boolean b(AxisAlignedBB axisalignedbb) {
        return this.a(axisalignedbb, (Entity) null);
    }

    public boolean a(AxisAlignedBB axisalignedbb, Entity entity) {
        for (Entity entity1 : getEntities(null, axisalignedbb)) {
            // PaperSpigot start - Allow block placement if the placer cannot see the vanished blocker
            if (entity instanceof EntityPlayer && entity1 instanceof EntityPlayer) {
                if (!((EntityPlayer) entity).getBukkitEntity().canSee(((EntityPlayer) entity1).getBukkitEntity())) {
                    continue;
                }
            }

            if (!entity1.dead && entity1.k && entity1 != entity && (entity == null || entity.vehicle != entity1 && entity.passenger != entity1)) {
                return false;
            }
        }

        return true;
    }

    public boolean c(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a), j = MathHelper.floor(axisalignedbb.d), k = MathHelper.floor(axisalignedbb.b), l = MathHelper.floor(axisalignedbb.e), i1 = MathHelper.floor(axisalignedbb.c), j1 = MathHelper.floor(axisalignedbb.f);
        for (int k1 = i; k1 <= j; ++k1) {
            for (int l1 = k; l1 <= l; ++l1) {
                for (int i2 = i1; i2 <= j1; ++i2) {
                    if (this.getType(k1, l1, i2).getBlock().getMaterial() != Material.AIR) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean containsLiquid(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a), j = MathHelper.floor(axisalignedbb.d), k = MathHelper.floor(axisalignedbb.b), l = MathHelper.floor(axisalignedbb.e), i1 = MathHelper.floor(axisalignedbb.c), j1 = MathHelper.floor(axisalignedbb.f);
        for (int k1 = i; k1 <= j; ++k1) {
            for (int l1 = k; l1 <= l; ++l1) {
                for (int i2 = i1; i2 <= j1; ++i2) {
                    if (getType(k1, l1, i2).getBlock().getMaterial().isLiquid())
                        return true;
                }
            }
        }

        return false;
    }

    public boolean e(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a), j = MathHelper.floor(axisalignedbb.d + 1.0D), k = MathHelper.floor(axisalignedbb.b), l = MathHelper.floor(axisalignedbb.e + 1.0D), i1 = MathHelper.floor(axisalignedbb.c), j1 = MathHelper.floor(axisalignedbb.f + 1.0D);
        if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        Block block = this.getType(k1, l1, i2).getBlock();
                        if (block == Blocks.FIRE || block == Blocks.FLOWING_LAVA || block == Blocks.LAVA) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean a(AxisAlignedBB axisalignedbb, Material material, Entity entity) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        if (!this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
            return false;
        } else {
            boolean flag = false;
            Vec3D vec3d = new Vec3D(0.0D, 0.0D, 0.0D);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        IBlockData iblockdata = this.getType(k1, l1, i2);
                        Block block = iblockdata.getBlock();

                        if (block.getMaterial() == material) {
                            if (l >= ((l1 + 1) - BlockFluids.b(iblockdata.get(BlockFluids.LEVEL)))) {
                                flag = true;
                                vec3d = block.a(this, blockposition_mutableblockposition.c(k1, l1, i2), entity, vec3d);
                            }
                        }
                    }
                }
            }

            if (vec3d.b() > 0.0D && entity.aL()) {
                vec3d = vec3d.a();
                entity.motX += vec3d.a * 0.014D;
                entity.motY += vec3d.b * 0.014D;
                entity.motZ += vec3d.c * 0.014D;
            }

            return flag;
        }
    }

    public boolean a(AxisAlignedBB axisalignedbb, Material material) {
        int i = MathHelper.floor(axisalignedbb.a), j = MathHelper.floor(axisalignedbb.d + 1.0D), k = MathHelper.floor(axisalignedbb.b), l = MathHelper.floor(axisalignedbb.e + 1.0D), i1 = MathHelper.floor(axisalignedbb.c), j1 = MathHelper.floor(axisalignedbb.f + 1.0D);
        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    if (this.getType(k1, l1, i2).getBlock().getMaterial() == material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean b(AxisAlignedBB axisalignedbb, Material material) {
        int i = MathHelper.floor(axisalignedbb.a), j = MathHelper.floor(axisalignedbb.d + 1.0D), k = MathHelper.floor(axisalignedbb.b), l = MathHelper.floor(axisalignedbb.e + 1.0D), i1 = MathHelper.floor(axisalignedbb.c), j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    IBlockData iblockdata = this.getType(k1, l1, i2);
                    Block block = iblockdata.getBlock();

                    if (block.getMaterial() == material) {
                        int j2 = iblockdata.get(BlockFluids.LEVEL);
                        double d0 = (l1 + 1);

                        if (j2 < 8) {
                            d0 = (double) (l1 + 1) - (double) j2 / 8.0D;
                        }

                        if (d0 >= axisalignedbb.b) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Explosion explode(Entity entity, double d0, double d1, double d2, float f, boolean flag) {
        return createExplosion(entity, d0, d1, d2, f, false, flag);
    }

    public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        Explosion explosion = new Explosion(this, entity, d0, d1, d2, f, flag, flag1);
        explosion.a();
        explosion.a(true);
        return explosion;
    }

    public float a(Vec3D vec3d, AxisAlignedBB axisalignedbb) {
        double d0 = 1.0D / ((axisalignedbb.d - axisalignedbb.a) * 2.0D + 1.0D);
        double d1 = 1.0D / ((axisalignedbb.e - axisalignedbb.b) * 2.0D + 1.0D);
        double d2 = 1.0D / ((axisalignedbb.f - axisalignedbb.c) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) (f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) (f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) (f2 + d2)) {
                        if (this.rayTrace(new Vec3D(axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * f + d3, axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * f1, axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * f2 + d4), vec3d) == null) {
                            ++i;
                        }
                        ++j;
                    }
                }
            }

            return (float) i / (float) j;
        } else {
            return 0.0F;
        }
    }

    public boolean douseFire(EntityHuman entityhuman, BlockPosition blockposition, EnumDirection enumdirection) {
        blockposition = blockposition.shift(enumdirection);
        if (this.getType(blockposition).getBlock() == Blocks.FIRE) {
            this.a(entityhuman, 1004, blockposition, 0);
            this.setAir(blockposition);
            return true;
        } else {
            return false;
        }
    }

    public TileEntity getTileEntity(BlockPosition blockposition) {
        if (!this.isValidLocation(blockposition)) {
            return null;
        } else {
            // CraftBukkit start
            if (capturedTileEntities.containsKey(blockposition)) {
                return capturedTileEntities.get(blockposition);
            }
            // CraftBukkit end

            TileEntity tileentity = null;
            int i;
            TileEntity tileentity1;

            if (this.M) {
                for (i = 0; i < this.b.size(); ++i) {
                    tileentity1 = this.b.get(i);
                    if (!tileentity1.x() && tileentity1.getPosition().equals(blockposition)) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            if (tileentity == null) {
                tileentity = this.getChunkAtWorldCoords(blockposition).a(blockposition, Chunk.EnumTileEntityState.IMMEDIATE);
            }

            if (tileentity == null) {
                for (i = 0; i < this.b.size(); ++i) {
                    tileentity1 = this.b.get(i);
                    if (!tileentity1.x() && tileentity1.getPosition().equals(blockposition)) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            return tileentity;
        }
    }

    public TileEntity getTileEntity(Chunk ch, BlockPosition blockposition) {
        if (!this.isValidLocation(blockposition)) {
            return null;
        } else {
            // CraftBukkit start
            if (capturedTileEntities.containsKey(blockposition)) {
                return capturedTileEntities.get(blockposition);
            }
            // CraftBukkit end

            TileEntity tileentity = null;
            int i;
            TileEntity tileentity1;

            if (this.M) {
                for (i = 0; i < this.b.size(); ++i) {
                    tileentity1 = this.b.get(i);
                    if (!tileentity1.x() && tileentity1.getPosition().equals(blockposition)) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            if (tileentity == null) {
                tileentity = ch.a(blockposition, Chunk.EnumTileEntityState.IMMEDIATE);
            }

            if (tileentity == null) {
                for (i = 0; i < this.b.size(); ++i) {
                    tileentity1 = this.b.get(i);
                    if (!tileentity1.x() && tileentity1.getPosition().equals(blockposition)) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            return tileentity;
        }
    }

    public void setTileEntity(BlockPosition blockposition, TileEntity tileentity) {
        if (tileentity != null && !tileentity.x()) {
            // CraftBukkit start
            if (captureBlockStates) {
                tileentity.a(this);
                tileentity.a(blockposition);
                capturedTileEntities.put(blockposition, tileentity);
                return;
            }
            // CraftBukkit end
            if (this.M) {
                tileentity.a(blockposition);
                Iterator<TileEntity> iterator = this.b.iterator();

                while (iterator.hasNext()) {
                    TileEntity tileentity1 = iterator.next();
                    if (tileentity1.getPosition().equals(blockposition)) {
                        tileentity1.y();
                        iterator.remove();
                    }
                }

                tileentity.a(this); // Spigot - No null worlds
                this.b.add(tileentity);
            } else {
                this.a(tileentity);
                this.getChunkAtWorldCoords(blockposition).a(blockposition, tileentity);
            }
        }

    }

    public void t(BlockPosition blockposition) {
        TileEntity tileentity = this.getTileEntity(blockposition);

        if (tileentity != null && this.M) {
            tileentity.y();
            this.b.remove(tileentity);
        } else {
            if (tileentity != null) {
                this.b.remove(tileentity);
                this.tileEntityList.remove(tileentity);
            }

            this.getChunkAtWorldCoords(blockposition).e(blockposition);
        }

    }

    public void b(TileEntity tileentity) {
        this.c.add(tileentity);
    }

    public boolean u(BlockPosition blockposition) {
        IBlockData iblockdata = this.getType(blockposition);
        AxisAlignedBB axisalignedbb = iblockdata.getBlock().a(this, blockposition, iblockdata);

        return axisalignedbb != null && axisalignedbb.a() >= 1.0D;
    }

    public boolean d(BlockPosition blockposition, boolean flag) {
        if (!this.isValidLocation(blockposition)) {
            return flag;
        } else {
            if (chunkProvider.getChunkAt(blockposition).isEmpty()) {
                return flag;
            } else {
                Block block = this.getType(blockposition).getBlock();

                return block.getMaterial().k() && block.d();
            }
        }
    }

    public void B() {
        int i = this.a(1.0F);

        if (i != this.I) {
            this.I = i;
        }

    }

    public void setSpawnFlags(boolean flag, boolean flag1) {
        this.allowMonsters = flag;
        this.allowAnimals = flag1;
    }

    public void doTick() {
        this.p();
    }

    protected void C() {
        if (this.worldData.hasStorm()) {
            this.p = 1.0F;
            if (this.worldData.isThundering()) {
                this.r = 1.0F;
            }
        }
    }

    protected void p() {
        if (!this.worldProvider.o()) {
            int i = this.worldData.A();

            if (i > 0) {
                --i;
                this.worldData.i(i);
                this.worldData.setThunderDuration(this.worldData.isThundering() ? 1 : 2);
                this.worldData.setWeatherDuration(this.worldData.hasStorm() ? 1 : 2);
            }

            int j = this.worldData.getThunderDuration();

            if (j <= 0) {
                if (this.worldData.isThundering()) {
                    this.worldData.setThunderDuration(this.random.nextInt(12000) + 3600);
                } else {
                    this.worldData.setThunderDuration(this.random.nextInt(168000) + 12000);
                }
            } else {
                --j;
                this.worldData.setThunderDuration(j);
                if (j <= 0) {
                    this.worldData.setThundering(!this.worldData.isThundering());
                }
            }

            this.q = this.r;
            if (this.worldData.isThundering()) {
                this.r = (float) ((double) this.r + 0.01D);
            } else {
                this.r = (float) ((double) this.r - 0.01D);
            }

            this.r = MathHelper.a(this.r, 0.0F, 1.0F);
            int k = this.worldData.getWeatherDuration();

            if (k <= 0) {
                if (this.worldData.hasStorm()) {
                    this.worldData.setWeatherDuration(this.random.nextInt(12000) + 12000);
                } else {
                    this.worldData.setWeatherDuration(this.random.nextInt(168000) + 12000);
                }
            } else {
                --k;
                this.worldData.setWeatherDuration(k);
                if (k <= 0) {
                    this.worldData.setStorm(!this.worldData.hasStorm());
                }
            }

            this.o = this.p;
            if (this.worldData.hasStorm()) {
                this.p = (float) ((double) this.p + 0.01D);
            } else {
                this.p = (float) ((double) this.p - 0.01D);
            }

            this.p = MathHelper.a(this.p, 0.0F, 1.0F);

            // CraftBukkit start
            for (EntityHuman player : players) {
                if (player.world == this)
                    ((EntityPlayer) player).tickWeather();
            }
            // CraftBukkit end
        }
    }

    protected void D() {
        // this.chunkTickList.clear(); // CraftBukkit - removed

        int i;
        EntityHuman entityhuman;
        int j;
        int k;

        // Spigot start
        int optimalChunks = spigotConfig.chunksPerTick;
        // Quick conditions to allow us to exist early
        if (optimalChunks > 0) {
            // Keep chunks with growth inside of the optimal chunk range
            int chunksPerPlayer = Math.min(200, Math.max(1, (int) (((optimalChunks - players.size()) / (double) players.size()) + 0.5)));
            int randRange = 3 + chunksPerPlayer / 30;
            // Limit to normal tick radius - including view distance
            randRange = (randRange > chunkTickRadius) ? chunkTickRadius : randRange;
            // odds of growth happening vs growth happening in vanilla
            this.growthOdds = this.modifiedOdds = Math.max(35, Math.min(100, ((chunksPerPlayer + 1) * 100F) / 15F));
            // Spigot end
            for (i = 0; i < this.players.size(); ++i) {
                entityhuman = this.players.get(i);
                j = MathHelper.floor(entityhuman.locX / 16.0D);
                k = MathHelper.floor(entityhuman.locZ / 16.0D);

                // Spigot start - Always update the chunk the player is on
                long key = chunkToKey(j, k);
                int existingPlayers = Math.max(0, chunkTickList.get(key)); // filter out -1
                chunkTickList.put(key, (short) (existingPlayers + 1));

                // Check and see if we update the chunks surrounding the player this tick
                for (int chunk = 0; chunk < chunksPerPlayer; chunk++) {
                    int dx = (random.nextBoolean() ? 1 : -1) * random.nextInt(randRange);
                    int dz = (random.nextBoolean() ? 1 : -1) * random.nextInt(randRange);
                    long hash = chunkToKey(dx + j, dz + k);
                    if (!chunkTickList.contains(hash) && this.chunkProvider.isChunkLoaded(dx + j, dz + k)) {
                        chunkTickList.put(hash, (short) -1); // no players
                    }
                }
            }
            // Spigot End
        }

    }

    protected abstract int q();

    protected void a(int i, int j, Chunk chunk) {
        chunk.m();
    }

    protected void h() {
        this.D();
    }

    public void a(Block block, BlockPosition blockposition, Random random) {
        this.e = true;
        block.b(this, blockposition, this.getType(blockposition), random);
        this.e = false;
    }

    public boolean v(BlockPosition blockposition) {
        return this.e(blockposition, false);
    }

    public boolean w(BlockPosition blockposition) {
        return this.e(blockposition, true);
    }

    // Vortex start - Optimized snow fall and placing
    public boolean e(BlockPosition blockposition, boolean flag) {
        int y = blockposition.getY(), x = blockposition.getX(), z = blockposition.getZ();
        if (y >= 0 && y < 256 && getBiome(x, y, z).aV(x, y, z) <= 0.15F && this.b(EnumSkyBlock.BLOCK, x, y, z) < 10) {
            IBlockData iblockdata = this.getType(x, y, z);
            Block block = iblockdata.getBlock();

            if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && iblockdata.get(BlockFluids.LEVEL) == 0) {
                if (!flag) {
                    return true;
                }

                return !(getType(x - 1, y, z).getBlock().getMaterial() == Material.WATER
                        && getType(x + 1, y, z).getBlock().getMaterial() == Material.WATER
                        && getType(x, y, z - 1).getBlock().getMaterial() == Material.WATER
                        && getType(x, y, z + 1).getBlock().getMaterial() == Material.WATER);
            }
        }
        return false;
    }

    public boolean f(BlockPosition blockposition, boolean flag) {
        return f(blockposition.getX(), blockposition.getY(), blockposition.getZ(), flag);
    }

    public boolean f(int x, int y, int z, boolean flag) {
        return f(getBiome(x, y, z), x, y, z, flag);
    }

    public boolean f(BiomeBase biome, int x, int y, int z, boolean flag) {
        float f = biome.aV(x, y, z);

        if (f > 0.15F) {
            return false;
        } else if (!flag) {
            return true;
        } else {
            if (y >= 0 && y < 256 && this.b(EnumSkyBlock.BLOCK, x, y, z) < 10) {
                return getType(x, y, z).getBlock().getMaterial() == Material.AIR && canPlaceSnow(x, y, z);
            }

            return false;
        }
    }

    private boolean canPlaceSnow(int x, int y, int z) {
        IBlockData iblockdata = getType(x, y - 1, z);
        Block block = iblockdata.getBlock();

        return block != Blocks.ICE && block != Blocks.PACKED_ICE && (block.getMaterial() == Material.LEAVES || (block == Blocks.SNOW_LAYER && iblockdata.get(BlockSnow.LAYERS) >= 7 || block.c() && block.material.isSolid()));
    }
    // Vortex - end

    public boolean x(BlockPosition blockposition) {
        if(InvictusConfig.disableLighting)
            return true;
        boolean flag = false;

        if (!this.worldProvider.o()) {
            flag = this.updateLight(EnumSkyBlock.SKY, blockposition.getX(), blockposition.getY(), blockposition.getZ()); // PaperSpigot - Asynchronous lighting updates
        }

        flag |= this.updateLight(EnumSkyBlock.BLOCK, blockposition.getX(), blockposition.getY(), blockposition.getZ()); // PaperSpigot - Asynchronous lighting updates
        return flag;
    }

    public boolean c(EnumSkyBlock enumskyblock, BlockPosition blockposition, Chunk chunk, List<Chunk> neighbors) { // PaperSpigot
        return c(enumskyblock, blockposition.getX(), blockposition.getY(), blockposition.getZ(), chunk, neighbors);
    }

    public boolean c(EnumSkyBlock enumskyblock, int x, int y, int z, Chunk chunk, List<Chunk> neighbors) { // PaperSpigot
        if (chunk == null) {
            return false;
        } else {
            int i = 0;
            int j = 0;

            int k = this.b(enumskyblock, x, y, z);
            int l = this.a(x, y, z, enumskyblock);
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;

            if (l > k) {
                this.H[j++] = 133152;
            } else if (l < k) {
                this.H[j++] = 133152 | k << 18;

                while (i < j) {
                    l1 = this.H[i++];
                    i2 = (l1 & 63) - 32 + x;
                    j2 = (l1 >> 6 & 63) - 32 + y;
                    k2 = (l1 >> 12 & 63) - 32 + z;
                    int l3 = l1 >> 18 & 15;
                    l2 = this.b(enumskyblock, i2, j2, k2);
                    if (l2 == l3) {
                        this.a(enumskyblock, i2, j2, k2, 0);
                        if (l3 > 0) {
                            i3 = MathHelper.a(i2 - x);
                            j3 = MathHelper.a(j2 - y);
                            k3 = MathHelper.a(k2 - z);
                            if (i3 + j3 + k3 < 17) {
                                for (EnumDirection enumdirection : EnumDirection.values()) {
                                    int k4 = i2 + enumdirection.getAdjacentX();
                                    int l4 = j2 + enumdirection.getAdjacentY();
                                    int i5 = k2 + enumdirection.getAdjacentZ();

                                    int j5 = Math.max(1, this.getType(k4, l4, i5).getBlock().p());

                                    l2 = this.b(enumskyblock, k4, l4, i5);
                                    if (l2 == l3 - j5 && j < this.H.length) {
                                        this.H[j++] = k4 - x + 32 | l4 - y + 32 << 6 | i5 - z + 32 << 12
                                                | l3 - j5 << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                i = 0;
            }

            while (i < j) {
                l1 = this.H[i++];
                i2 = (l1 & 63) - 32 + x;
                j2 = (l1 >> 6 & 63) - 32 + y;
                k2 = (l1 >> 12 & 63) - 32 + z;
                int k5 = this.b(enumskyblock, i2, j2, k2);

                l2 = this.a(i2, j2, k2, enumskyblock);
                if (l2 != k5) {
                    this.a(enumskyblock, i2, j2, k2, l2);
                    if (l2 > k5) {
                        i3 = Math.abs(i2 - x);
                        j3 = Math.abs(j2 - y);
                        k3 = Math.abs(k2 - z);
                        boolean flag = j < this.H.length - 6;

                        if (i3 + j3 + k3 < 17 && flag) {

                            if (this.b(enumskyblock, i2 - 1, j2, k2) < l2) {
                                this.H[j++] = i2 - 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2 + 1, j2, k2) < l2) {
                                this.H[j++] = i2 + 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2 - 1, k2) < l2) {
                                this.H[j++] = i2 - x + 32 + (j2 - 1 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2 + 1, k2) < l2) {
                                this.H[j++] = i2 - x + 32 + (j2 + 1 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2, k2 - 1) < l2) {
                                this.H[j++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 - 1 - z + 32 << 12);
                            }

                            if (this.b(enumskyblock, i2, j2, k2 + 1) < l2) {
                                this.H[j++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 + 1 - z + 32 << 12);
                            }
                        }
                    }
                }
            }

            // PaperSpigot start - Asynchronous light updates
            if (chunk.world.paperSpigotConfig.useAsyncLighting) {
                chunk.pendingLightUpdates.decrementAndGet();
                if (neighbors != null) {
                    for (Chunk neighbor : neighbors) {
                        neighbor.pendingLightUpdates.decrementAndGet();
                    }
                }
            }
            // PaperSpigot end

            return true;
        }
    }

    public boolean updateLight(EnumSkyBlock enumskyblock, int x, int y, int z) {
        if(InvictusConfig.disableLighting)
            return true;
        Chunk chunk = getChunkIfLoaded(x >> 4, z >> 4);
        if (chunk == null || !chunk.areNeighborsLoaded(1))
            return false;
        else if (!chunk.world.paperSpigotConfig.useAsyncLighting)
            return c(enumskyblock, x, y, z, chunk, null);
        chunk.pendingLightUpdates.incrementAndGet();
        chunk.lightUpdateTime = chunk.world.getTime();
        List<Chunk> neighbors = new ArrayList<>();
        for (int cx = (x >> 4) - 1; cx <= (x >> 4) + 1; cx++) {
            for (int cz = (z >> 4) - 1; cz <= (z >> 4) + 1; cz++) {
                if (cx != x >> 4 && cz != z >> 4) {
                    Chunk neighbor = getChunkIfLoaded(cx, cz);
                    if (neighbor != null) {
                        neighbor.pendingLightUpdates.incrementAndGet();
                        neighbor.lightUpdateTime = chunk.world.getTime();
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        if (!Bukkit.isPrimaryThread())
            return c(enumskyblock, x, y, z, chunk, neighbors);
        this.lightingExecutor.submit(() -> {
            World.this.c(enumskyblock, x, y, z, chunk, neighbors);
        });
        return true;
    }

    /**
     * PaperSpigot - Asynchronous lighting updates
     */
    public boolean updateLight(EnumSkyBlock enumskyblock, BlockPosition position) {
        return updateLight(enumskyblock, position.getX(), position.getY(), position.getZ());
    }

    public boolean a(boolean flag) {
        return false;
    }

    public List<NextTickListEntry> a(Chunk chunk, boolean flag) {
        return null;
    }

    public void a(BlockPosition blockposition, Chunk chunk, Block block, int i, int j) {
    }

    public List<NextTickListEntry> a(StructureBoundingBox structureboundingbox, boolean flag) {
        return null;
    }

    public List<Entity> getEntities(Entity entity, AxisAlignedBB axisalignedbb) {
        return a(entity, axisalignedbb, IEntitySelector.d);
    }

    public List<Entity> a(Entity entity, AxisAlignedBB axisalignedbb, Predicate<? super Entity> predicate) {
        List<Entity> arraylist = Lists.newArrayList();
        int i = MathHelper.floor((axisalignedbb.a - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.d + 2.0D) / 16.0D);
        int k = MathHelper.floor((axisalignedbb.c - 2.0D) / 16.0D);
        int l = MathHelper.floor((axisalignedbb.f + 2.0D) / 16.0D);

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                Chunk chunk = getChunkIfLoaded(i1, j1);
                if (chunk != null) {
                    chunk.a(entity, axisalignedbb, arraylist, predicate);
                }
            }
        }

        return arraylist;
    }

    public <T extends Entity> List<T> a(Class<? extends T> oclass, Predicate<? super T> predicate) {
        List<T> arraylist = Lists.newArrayList();

        for (Entity entity : entityList) {
            if (oclass.isAssignableFrom(entity.getClass()) && predicate.apply((T) entity)) { // CraftBukkit - fix decompile error
                arraylist.add((T) entity);
            }
        }

        return arraylist;
    }

    public <T extends Entity> List<T> b(Class<? extends T> oclass, Predicate<? super T> predicate) {
        List<T> arraylist = Lists.newArrayList();

        for (Entity entity : players) {
            if (oclass.isAssignableFrom(entity.getClass()) && predicate.apply((T) entity)) { // CraftBukkit - fix decompile error
                arraylist.add((T) entity);
            }
        }

        return arraylist;
    }

    public <T extends Entity> List<T> a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb) {
        return this.a(oclass, axisalignedbb, IEntitySelector.d);
    }

    public <T extends Entity> List<T> a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, Predicate<? super T> predicate) {
        int i = MathHelper.floor((axisalignedbb.a - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.d + 2.0D) / 16.0D);
        int k = MathHelper.floor((axisalignedbb.c - 2.0D) / 16.0D);
        int l = MathHelper.floor((axisalignedbb.f + 2.0D) / 16.0D);
        List<T> arraylist = Lists.newArrayList();

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                Chunk chunk = getChunkIfLoaded(i1, j1);
                if (chunk != null) {
                    chunk.a(oclass, axisalignedbb, arraylist, predicate);
                }
            }
        }

        return arraylist;
    }

    public <T extends Entity> T a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, T t0) {

        Entity entity = null;
        double d0 = Double.MAX_VALUE;

        for (Entity entity1 : a(oclass, axisalignedbb)) {
            if (entity1 != t0 && IEntitySelector.d.apply(entity1)) {
                double d1 = t0.h(entity1);

                if (d1 <= d0) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }

        return (T) entity; // CraftBukkit fix decompile error
    }

    public Entity a(int i) {
        return entitiesById.get(i);
    }

    public void b(BlockPosition blockposition, TileEntity tileentity) {
        if (isValidLocation(blockposition)) {
            Chunk chunk = getChunkIfLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4);
            if (chunk != null)
                chunk.e();
        }
    }

    public int a(Class<?> oclass) {
        int i = 0;
        for (Entity entity : entityList) {
            // CraftBukkit start - Split out persistent check, don't apply it to special persistent mobs
            if (entity instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient) entity;
                if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
                    continue;
                }
            }

            if (oclass.isAssignableFrom(entity.getClass()))
                ++i;
        }

        return i;
    }

    public void b(Collection<Entity> collection) {
        for (Entity entity : collection) {
            entityList.add(entity);
            a(entity);
        }
    }

    public void c(Collection<Entity> collection) {
        g.addAll(collection);
    }

    public boolean a(Block block, BlockPosition blockposition, boolean flag, EnumDirection enumdirection, Entity entity, ItemStack itemstack) {
        Block block1 = getType(blockposition).getBlock();
        AxisAlignedBB axisalignedbb = flag ? null : block.a(this, blockposition, block.getBlockData());

        // CraftBukkit start - store default return
        boolean defaultReturn = (axisalignedbb == null || this.a(axisalignedbb, entity)) && (block1.getMaterial() == Material.ORIENTABLE && block == Blocks.ANVIL || block1.getMaterial().isReplaceable() && block.canPlace(this, blockposition, enumdirection, itemstack));
        BlockCanBuildEvent event = new BlockCanBuildEvent(this.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), CraftMagicNumbers.getId(block), defaultReturn);
        this.getServer().getPluginManager().callEvent(event);

        return event.isBuildable();
        // CraftBukkit end
    }

    public int F() {
        return this.a;
    }

    public void b(int i) {
        this.a = i;
    }

    public int getBlockPower(BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = this.getType(blockposition);

        return iblockdata.getBlock().b(this, blockposition, iblockdata, enumdirection);
    }

    public WorldType G() {
        return this.worldData.getType();
    }

    public int getBlockPower(BlockPosition blockposition) {
        int i = Math.max(0, this.getBlockPower(blockposition.down(), EnumDirection.DOWN));

        if (i >= 15) {
            return i;
        } else {
            i = Math.max(i, this.getBlockPower(blockposition.up(), EnumDirection.UP));
            if (i >= 15) {
                return i;
            } else {
                i = Math.max(i, this.getBlockPower(blockposition.north(), EnumDirection.NORTH));
                if (i >= 15) {
                    return i;
                } else {
                    i = Math.max(i, this.getBlockPower(blockposition.south(), EnumDirection.SOUTH));
                    if (i >= 15) {
                        return i;
                    } else {
                        i = Math.max(i, this.getBlockPower(blockposition.west(), EnumDirection.WEST));
                        if (i >= 15) {
                            return i;
                        } else {
                            i = Math.max(i, this.getBlockPower(blockposition.east(), EnumDirection.EAST));
                            return i;
                        }
                    }
                }
            }
        }
    }

    public boolean isBlockFacePowered(BlockPosition blockposition, EnumDirection enumdirection) {
        return getBlockFacePower(blockposition, enumdirection) > 0;
    }

    public int getBlockFacePower(BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = this.getType(blockposition);
        Block block = iblockdata.getBlock();

        return block.isOccluding() ? this.getBlockPower(blockposition) : block.a(this, blockposition, iblockdata, enumdirection);
    }

    public boolean isBlockIndirectlyPowered(BlockPosition blockposition) {
        return getBlockFacePower(blockposition.down(), EnumDirection.DOWN) > 0 ||
                (getBlockFacePower(blockposition.up(), EnumDirection.UP) > 0 ||
                        (getBlockFacePower(blockposition.north(), EnumDirection.NORTH) > 0 ||
                                (getBlockFacePower(blockposition.south(), EnumDirection.SOUTH) > 0 ||
                                        (getBlockFacePower(blockposition.west(), EnumDirection.WEST) > 0 ||
                                                getBlockFacePower(blockposition.east(), EnumDirection.EAST) > 0))));
    }

    public int A(BlockPosition blockposition) {
        int i = 0;
        for (EnumDirection enumdirection : EnumDirection.values()) {
            int l = this.getBlockFacePower(blockposition.shift(enumdirection), enumdirection);

            if (l >= 15) {
                return 15;
            }

            if (l > i) {
                i = l;
            }
        }

        return i;
    }

    public EntityHuman findNearbyPlayer(Entity entity, double d0) {
        return this.findNearbyPlayer(entity.locX, entity.locY, entity.locZ, d0);
    }

    public EntityHuman findNearbyPlayer(double d0, double d1, double d2, double d3) {
        double d4 = -1.0D, squaredDistance = d3 * d3;
        EntityHuman entityhuman = null;

        for (EntityHuman entityhuman1 : players) {
            // CraftBukkit start - Fixed an NPE
            if (entityhuman1 == null || entityhuman1.dead || entityhuman1.isSpectator()) {
                continue;
            }
            // CraftBukkit end

            double d5 = entityhuman1.e(d0, d1, d2);

            if ((d3 < 0.0D || d5 < squaredDistance) && (d4 == -1.0D || d5 < d4)) {
                d4 = d5;
                entityhuman = entityhuman1;
            }
        }

        return entityhuman;
    }

    public boolean isPlayerNearby(double d0, double d1, double d2, double d3) {
        double squaredDistance = d3 * d3;
        for (EntityHuman entityhuman : players) {
            if (!entityhuman.isSpectator()) { // Vortex - less check
                double d4 = entityhuman.e(d0, d1, d2);

                if (d3 < 0.0D || d4 < squaredDistance) {
                    return true;
                }
            }
        }

        return false;
    }

    // PaperSpigot start - Modified methods for affects spawning
    public EntityHuman findNearbyPlayerWhoAffectsSpawning(Entity entity, double d0) {
        return this.findNearbyPlayerWhoAffectsSpawning(entity.locX, entity.locY, entity.locZ, d0);
    }

    public EntityHuman findNearbyPlayerWhoAffectsSpawning(double d0, double d1, double d2, double d3) {
        double d4 = -1.0D, squaredDistance = d3 * d3;
        EntityHuman entityhuman = null;

        for (EntityHuman entityhuman1 : players) {
            // CraftBukkit start - Fixed an NPE
            if (entityhuman1 == null || entityhuman1.dead || !entityhuman1.affectsSpawning || entityhuman1.isSpectator()) {
                continue;
            }
            // CraftBukkit end

            double d5 = entityhuman1.e(d0, d1, d2);

            if ((d3 < 0.0D || d5 < squaredDistance) && (d4 == -1.0D || d5 < d4)) {
                d4 = d5;
                entityhuman = entityhuman1;
            }
        }

        return entityhuman;
    }

    public boolean isPlayerNearbyWhoAffectsSpawning(double d0, double d1, double d2, double d3) {
        double squaredDistance = d3 * d3;
        for (EntityHuman entityhuman : players) {
            if (!entityhuman.isSpectator()) { // Vortex - less check
                double d4 = entityhuman.e(d0, d1, d2);

                if (d3 < 0.0D || d4 < squaredDistance && entityhuman.affectsSpawning) {
                    return true;
                }
            }
        }

        return false;
    }
    // PaperSpigot end

    public EntityHuman a(String s) {
        for (EntityHuman entityhuman : players) {
            if (s.equals(entityhuman.getName())) {
                return entityhuman;
            }
        }

        return null;
    }

    public EntityHuman b(UUID uuid) {
        for (EntityHuman entityhuman : players) {
            if (uuid.equals(entityhuman.getUniqueID())) {
                return entityhuman;
            }
        }

        return null;
    }

    public void checkSession() throws ExceptionWorldConflict {
        this.dataManager.checkSession();
    }

    public long getSeed() {
        return this.worldData.getSeed();
    }

    public long getTime() {
        return this.worldData.getTime();
    }

    public long getDayTime() {
        return this.worldData.getDayTime();
    }

    public void setDayTime(long i) {
        this.worldData.setDayTime(i);
    }

    public BlockPosition getSpawn() {
        BlockPosition blockposition = new BlockPosition(this.worldData.c(), this.worldData.d(), this.worldData.e());

        if (!this.getWorldBorder().a(blockposition)) {
            blockposition = this.getHighestBlockYAt(new BlockPosition(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return blockposition;
    }

    public void B(BlockPosition blockposition) {
        this.worldData.setSpawn(blockposition);
    }

    public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
        return true;
    }

    public void broadcastEntityEffect(Entity entity, byte b0) {
    }

    public IChunkProvider N() {
        return this.chunkProvider;
    }

    public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        block.a(this, blockposition, this.getType(blockposition), i, j);
    }

    public IDataManager getDataManager() {
        return this.dataManager;
    }

    public WorldData getWorldData() {
        return this.worldData;
    }

    public GameRules getGameRules() {
        return this.worldData.x();
    }

    public void everyoneSleeping() {
    }

    // CraftBukkit start
    // Calls the method that checks to see if players are sleeping
    // Called by CraftPlayer.setPermanentSleeping()
    public void checkSleepStatus() {
        this.everyoneSleeping();
    }
    // CraftBukkit end

    public float h(float f) {
        return (this.q + (this.r - this.q) * f) * this.j(f);
    }

    public float j(float f) {
        return this.o + (this.p - this.o) * f;
    }

    public boolean R() {
        return (double) this.h(1.0F) > 0.9D;
    }

    public boolean S() {
        return (double) this.j(1.0F) > 0.2D;
    }

    public boolean isRainingAt(int x, int y, int z) {
        if (!S() || !i(x, y, z) || q(x, z).getY() > y) {
            return false;
        }
        BiomeBase biomebase = getBiome(x, y, z);
        return !biomebase.d() && (!f(biomebase, x, y, z, false) && biomebase.e());
    }

    public boolean isRainingAt(BlockPosition blockposition) {
        return isRainingAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public boolean D(BlockPosition blockposition) {
        return getBiome(blockposition).f();
    }

    public PersistentCollection T() {
        return this.worldMaps;
    }

    public void a(String s, PersistentBase persistentbase) {
        this.worldMaps.a(s, persistentbase);
    }

    public PersistentBase a(Class<? extends PersistentBase> oclass, String s) {
        return this.worldMaps.get(oclass, s);
    }

    public int b(String s) {
        return this.worldMaps.a(s);
    }

    public void a(int i, BlockPosition blockposition, int j) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.a(i, blockposition, j);
        }
    }

    public void triggerEffect(int i, BlockPosition blockposition, int j) {
        this.a(null, i, blockposition, j);
    }

    public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
        try {
            for (IWorldAccess iWorldAccess : this.u) {
                iWorldAccess.a(entityhuman, i, blockposition, j);
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Playing level event");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Level event being played");

            crashreportsystemdetails.a("Block coordinates", CrashReportSystemDetails.a(blockposition));
            crashreportsystemdetails.a("Event source", entityhuman);
            crashreportsystemdetails.a("Event type", i);
            crashreportsystemdetails.a("Event data", j);
            throw new ReportedException(crashreport);
        }
    }

    public int getHeight() {
        return 256;
    }

    public int V() {
        return this.worldProvider.o() ? 128 : 256;
    }

    public Random a(int i, int j, int k) {
        long l = (long) i * 341873128712L + (long) j * 132897987541L + this.getWorldData().getSeed() + (long) k;

        this.random.setSeed(l);
        return this.random;
    }

    public BlockPosition a(String s, BlockPosition blockposition) {
        return this.N().findNearestMapFeature(this, s, blockposition);
    }

    public CrashReportSystemDetails a(CrashReport crashreport) {
        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Affected level", 1);

        crashreportsystemdetails.a("Level name", (this.worldData == null ? "????" : this.worldData.getName()));
        crashreportsystemdetails.a("All players", players.size() + " total; " + players);
        crashreportsystemdetails.a("Chunk stats", chunkProvider.getName());

        try {
            this.worldData.a(crashreportsystemdetails);
        } catch (Throwable throwable) {
            crashreportsystemdetails.a("Level Data Unobtainable", throwable);
        }

        return crashreportsystemdetails;
    }

    public void c(int i, BlockPosition blockposition, int j) {
        for (IWorldAccess iWorldAccess : this.u) {
            iWorldAccess.b(i, blockposition, j);
        }
    }

    public Calendar Y() {
        if (this.getTime() % 600L == 0L) {
            this.K.setTimeInMillis(MinecraftServer.az());
        }

        return this.K;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void updateAdjacentComparators(BlockPosition blockposition, Block block) {

        for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (this.isLoaded(blockposition1)) {
                IBlockData iblockdata = this.getType(blockposition1);

                if (Blocks.UNPOWERED_COMPARATOR.e(iblockdata.getBlock())) {
                    iblockdata.getBlock().doPhysics(this, blockposition1, iblockdata, block);
                } else if (iblockdata.getBlock().isOccluding()) {
                    blockposition1 = blockposition1.shift(enumdirection);
                    iblockdata = this.getType(blockposition1);
                    if (Blocks.UNPOWERED_COMPARATOR.e(iblockdata.getBlock())) {
                        iblockdata.getBlock().doPhysics(this, blockposition1, iblockdata, block);
                    }
                }
            }
        }

    }

    public DifficultyDamageScaler E(BlockPosition blockposition) {
        long i = 0L;
        float f = 0.0F;

        if (this.isLoaded(blockposition)) {
            f = this.y();
            i = this.getChunkAtWorldCoords(blockposition).w();
        }

        return new DifficultyDamageScaler(this.getDifficulty(), this.getDayTime(), i, f);
    }

    public EnumDifficulty getDifficulty() {
        return this.getWorldData().getDifficulty();
    }

    public int ab() {
        return this.I;
    }

    public void c(int i) {
        this.I = i;
    }

    public boolean ad() {
        return this.isLoading;
    }

    public PersistentVillage ae() {
        return this.villages;
    }

    public WorldBorder getWorldBorder() {
        return this.N;
    }

    public boolean c(int i, int j) {
        BlockPosition blockposition = this.getSpawn();
        int k = i * 16 + 8 - blockposition.getX();
        int l = j * 16 + 8 - blockposition.getZ();
        return k >= -128 && k <= 128 && l >= -128 && l <= 128 && this.keepSpawnInMemory; // CraftBukkit - Added 'this.keepSpawnInMemory'
    }
}
