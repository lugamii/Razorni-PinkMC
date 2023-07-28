package net.minecraft.server;

import com.eatthepath.uuid.FastUUID;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import eu.vortexdev.invictusspigot.InvictusSpigot;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import jline.console.ConsoleReader;
import joptsimple.OptionSet;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.event.server.ServerShutdownEvent;
import org.spigotmc.WatchdogThread;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
// CraftBukkit end

public abstract class MinecraftServer implements Runnable, ICommandListener, IAsyncTaskHandler {

    public static boolean HAS_PROTOCOL_SUPPORT = false;

    public final MethodProfiler methodProfiler = new MethodProfiler();
    public static final Logger LOGGER = LogManager.getLogger();
    public static final File a = new File("usercache.json");

    private static MinecraftServer l;
    protected final ICommandHandler b;
    protected final Random s = new Random();
    protected final Proxy e;
    protected final Queue<FutureTask<?>> j = new java.util.concurrent.ConcurrentLinkedQueue<>(); // Spigot, PAIL: Rename
    private final List<IUpdatePlayerListBox> p = Lists.newArrayList();
    private final ServerConnection q; // Spigot
    private final ServerPing r = new ServerPing();
    private final YggdrasilAuthenticationService V;
    private final MinecraftSessionService W;
    private final GameProfileRepository Y;
    private final UserCache Z;

    private final Object stopLock = new Object();
    public final Queue<Runnable> processQueue = new ConcurrentLinkedQueue<Runnable>() {
        @Override
        public boolean add(Runnable runnable) {
            postToMainThread(runnable);
            return true;
        }
    };
    public Convertable convertable;
    public File universe;
    public WorldServer[] worldServer;
    public String f;
    public int g;
    public long[][] i;
    // CraftBukkit start
    public List<WorldServer> worlds = new ArrayList<>();
    public org.bukkit.craftbukkit.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    public org.bukkit.command.RemoteConsoleCommandSender remoteConsole;
    public ConsoleReader reader;
    public Thread primaryThread;
    public int autosavePeriod;
    public double[] recentTps = new double[ 3 ]; // PaperSpigot - Fine have your darn compat with bad plugins
    private String serverIp;
    private int u = -1;
    private PlayerList v;
    private boolean isRunning = true;
    private boolean isStopped;
    private int ticks;
    private boolean spawnAnimals;
    private boolean spawnNPCs;
    private boolean pvpMode;
    // CraftBukkit end
    private boolean allowFlight;
    private String motd;
    private int F;
    private int G = 0;
    private KeyPair H;
    // CraftBukkit end
    private String I;
    private String J;
    private boolean demoMode;
    private boolean N;
    private String O = "";
    private String P = "";
    private boolean U;
    private Thread serverThread;
    private long ab = az();
    // CraftBukkit start
    private boolean hasStopped = false;
    private Exception lastException;

    public MinecraftServer(OptionSet options, Proxy proxy, File file1) {
        q = new ServerConnection(this);
        io.netty.util.ResourceLeakDetector.setEnabled(false); // Spigot - disable
        this.e = proxy;
        MinecraftServer.l = this;

        this.Z = new UserCache(this, file1);
        this.b = this.h();
        // this.convertable = new WorldLoaderServer(file); // CraftBukkit - moved to DedicatedServer.init
        this.V = new YggdrasilAuthenticationService(proxy, FastUUID.toString(UUID.randomUUID()));
        this.W = this.V.createMinecraftSessionService();
        this.Y = this.V.createProfileRepository();

        // CraftBukkit start
        this.options = options;
        // Try to see if we're actually running in a terminal, disable jline if not
        if (System.console() == null && System.getProperty("jline.terminal") == null) {
            System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
            Main.useJline = false;
        }

        try {
            reader = new ConsoleReader(System.in, System.out);
            reader.setExpandEvents(false); // Avoid parsing exceptions for uncommonly used event designators
        } catch (Throwable e) {
            try {
                // Try again with jline disabled for Windows users without C++ 2008 Redistributable
                System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
                System.setProperty("user.language", "en");
                Main.useJline = false;
                reader = new ConsoleReader(System.in, System.out);
                reader.setExpandEvents(false);
            } catch (IOException ex) {
                LOGGER.warn((String) null, ex);
            }
        }
        Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.util.ServerShutdownThread(this));
        serverThread = primaryThread = new Thread(this, "Server thread");
    }

    public static void main(final OptionSet options) { // CraftBukkit - replaces main(String[] astring)
        DispenserRegistry.c();

        try {
            DedicatedServer dedicatedserver = new DedicatedServer(options);

            if (options.has("port")) {
                int port = (Integer) options.valueOf("port");
                if (port > 0) {
                    dedicatedserver.setPort(port);
                }
            }

            if (options.has("universe")) {
                dedicatedserver.universe = (File) options.valueOf("universe");
            }

            if (options.has("world")) {
                dedicatedserver.setWorld((String) options.valueOf("world"));
            }

            dedicatedserver.primaryThread.start();
            // CraftBukkit end
        } catch (Exception exception) {
            MinecraftServer.LOGGER.fatal("Failed to start the minecraft server", exception);
        }

    }

    public static MinecraftServer getServer() {
        return MinecraftServer.l;
    }

    public static long az() {
        return System.currentTimeMillis();
    }

    public abstract PropertyManager getPropertyManager();

    protected CommandDispatcher h() {
        return new CommandDispatcher();
    }
    // CraftBukkit end

    protected abstract boolean init() throws IOException;

    protected void a(String s) {
        if (this.getConvertable().isConvertable(s)) {
            MinecraftServer.LOGGER.info("Converting map!");
            this.getConvertable().convert(s, new IProgressUpdate() {
                private long b = System.currentTimeMillis();

                public void a(String s) {
                }

                public void a(int i) {
                    if (System.currentTimeMillis() - this.b >= 1000L) {
                        this.b = System.currentTimeMillis();
                        MinecraftServer.LOGGER.info("Converting... " + i + "%");
                    }

                }

                public void c(String s) {
                }
            });
        }

    }

    protected synchronized void b(String s) {
    }

    protected void a(String s, String s1, long i, WorldType worldtype, String s2) {
        this.a(s);
        this.worldServer = new WorldServer[3];

        int worldCount = 3;

        for (int j = 0; j < worldCount; ++j) {
            WorldServer world;
            byte dimension = 0;

            if (j == 1) {
                if (getAllowNether()) {
                    dimension = -1;
                } else {
                    continue;
                }
            }

            if (j == 2) {
                if (server.getAllowEnd()) {
                    dimension = 1;
                } else {
                    continue;
                }
            }

            String worldType = org.bukkit.World.Environment.getEnvironment(dimension).toString().toLowerCase();
            String name = (dimension == 0) ? s : s + "_" + worldType;

            org.bukkit.generator.ChunkGenerator gen = this.server.getGenerator(name);
            WorldSettings worldsettings = new WorldSettings(i, this.getGamemode(), this.getGenerateStructures(),
                    this.isHardcore(), worldtype);
            worldsettings.setGeneratorSettings(s2);

            if (j == 0) {
                IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), s1, true);
                WorldData worlddata = idatamanager.getWorldData();
                if (worlddata == null) {
                    worlddata = new WorldData(worldsettings, s1);
                }
                worlddata.checkName(s1); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to
                // take the last loaded world as respawn (in this case the end)
                world = (WorldServer) (new WorldServer(this, idatamanager, worlddata, dimension, getServer().methodProfiler,
                        org.bukkit.World.Environment.getEnvironment(dimension), gen)).b();
                world.a(worldsettings);
                this.server.scoreboardManager = new org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager(this,
                        world.getScoreboard());
            } else {
                String dim = "DIM" + dimension;

                File newWorld = new File(new File(name), dim);
                File oldWorld = new File(new File(s), dim);

                if ((!newWorld.isDirectory()) && (oldWorld.isDirectory())) {
                    MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder required ----");
                    MinecraftServer.LOGGER.info(
                            "Unfortunately due to the way that Minecraft implemented multiworld support in 1.6, Bukkit requires that you move your "
                                    + worldType + " folder to a new location in order to operate correctly.");
                    MinecraftServer.LOGGER.info(
                            "We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Bukkit in the future.");
                    MinecraftServer.LOGGER.info("Attempting to move " + oldWorld + " to " + newWorld + "...");

                    if (newWorld.exists()) {
                        MinecraftServer.LOGGER.warn("A file or folder already exists at " + newWorld + "!");
                        MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                    } else if (newWorld.getParentFile().mkdirs()) {
                        if (oldWorld.renameTo(newWorld)) {
                            MinecraftServer.LOGGER.info("Success! To restore " + worldType
                                    + " in the future, simply move " + newWorld + " to " + oldWorld);
                            // Migrate world data too.
                            try {
                                com.google.common.io.Files.copy(new File(new File(s), "level.dat"),
                                        new File(new File(name), "level.dat"));
                            } catch (IOException exception) {
                                MinecraftServer.LOGGER.warn("Unable to migrate world data.");
                            }
                            MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder complete ----");
                        } else {
                            MinecraftServer.LOGGER.warn("Could not move folder " + oldWorld + " to " + newWorld + "!");
                            MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                        }
                    } else {
                        MinecraftServer.LOGGER.warn("Could not create path for " + newWorld + "!");
                        MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                    }
                }

                IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), name, true);
                // world =, b0 to dimension, s1 to name, added Environment and gen
                WorldData worlddata = idatamanager.getWorldData();
                if (worlddata == null) {
                    worlddata = new WorldData(worldsettings, name);
                }
                worlddata.checkName(name); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to
                // take the last loaded world as respawn (in this case the end)
                world = (WorldServer) new SecondaryWorldServer(this, idatamanager, dimension, this.worlds.get(0),
                        null, worlddata, org.bukkit.World.Environment.getEnvironment(dimension), gen)
                        .b();
            }

            this.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldInitEvent(world.getWorld()));

            world.addIWorldAccess(new WorldManager(this, world));
            world.getWorldData().setGameType(this.getGamemode());

            worlds.add(world);
            getPlayerList().setPlayerFileData(worlds.toArray(new WorldServer[0]));
        }

        // CraftBukkit end
        this.a(this.getDifficulty());
        this.k();
    }

    protected void k() {
        // CraftBukkit start - fire WorldLoadEvent and handle whether or not to keep the
        // spawn in memory
        int i;

        for (WorldServer worldserver : worlds) {
            if (!worldserver.getWorld().getKeepSpawnInMemory()) {
                continue;
            }

            BlockPosition blockposition = worldserver.getSpawn();
            long j = az();
            i = 0;

            for (int k = -192; k <= 192 && this.isRunning(); k += 16) {
                for (int l = -192; l <= 192 && this.isRunning(); l += 16) {
                    long i1 = az();

                    if (i1 - j > 1000L) {
                        this.a_("Preparing spawn area", i * 100 / 625);
                        j = i1;
                    }

                    ++i;
                    worldserver.chunkProviderServer.getChunkAt(blockposition.getX() + k >> 4, blockposition.getZ() + l >> 4, () -> {
                    });
                }
            }
        }

        for (WorldServer world : this.worlds) {
            this.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldLoadEvent(world.getWorld()));
        }
        // CraftBukkit end
        this.s();
    }

    protected void a(String s, IDataManager idatamanager) {
        File file = new File(idatamanager.getDirectory(), "resources.zip");

        if (file.isFile()) {
            this.setResourcePack("level://" + s + "/" + file.getName(), "");
        }

    }

    public abstract boolean getGenerateStructures();

    public abstract WorldSettings.EnumGamemode getGamemode();

    public void setGamemode(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        // CraftBukkit start
        for (int i = 0; i < this.worlds.size(); ++i) {
            getServer().worlds.get(i).getWorldData().setGameType(worldsettings_enumgamemode);
        }

    }

    public abstract EnumDifficulty getDifficulty();

    public abstract boolean isHardcore();

    public abstract int p();

    public abstract boolean q();

    public abstract boolean r();
    // PaperSpigot End

    protected void a_(String s, int i) {
        this.f = s;
        this.g = i;
        MinecraftServer.LOGGER.info(s + ": " + i + "%");
    }

    protected void s() {
        this.f = null;
        this.g = 0;

        this.server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD); // CraftBukkit
    }

    protected void saveChunks(boolean flag) throws ExceptionWorldConflict { // CraftBukkit - added throws
        if (!this.N) {

            // CraftBukkit start
            for (WorldServer worldserver : worlds) {
                // CraftBukkit end

                if (worldserver != null) {
                    if (!flag) {
                        MinecraftServer.LOGGER.info("Saving chunks for level '" + worldserver.getWorldData().getName() + "'/" + worldserver.worldProvider.getName());
                    }

                    try {
                        worldserver.save(true, null);
                        worldserver.saveLevel(); // CraftBukkit
                    } catch (ExceptionWorldConflict exceptionworldconflict) {
                        MinecraftServer.LOGGER.warn(exceptionworldconflict.getMessage());
                    }
                }
            }

        }
    }

    public void stop() throws ExceptionWorldConflict { // CraftBukkit - added throws
        // CraftBukkit start - prevent double stopping on multiple threads
        synchronized (stopLock) {
            if (hasStopped) return;
            hasStopped = true;
        }
        // CraftBukkit end
        if (!this.N) {
            this.server.getPluginManager().callEvent(new ServerShutdownEvent(this.server));
            MinecraftServer.LOGGER.info("Stopping server");

            // CraftBukkit start
            if (this.server != null) {
                this.server.disablePlugins();
            }
            // CraftBukkit end
            if (this.aq() != null) {
                this.aq().b();
            }

            if (this.v != null) {
                MinecraftServer.LOGGER.info("Saving players");
                this.v.savePlayers();
                this.v.u();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                } // CraftBukkit - SPIGOT-625 - give server at least a chance to send packets
            }

            if (this.worldServer != null) {
                MinecraftServer.LOGGER.info("Saving worlds");
                this.saveChunks(false);
            }
            InvictusSpigot.INSTANCE.shutdown();

            // Spigot start
            if (org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly) {
                LOGGER.info("Saving usercache.json");
                this.Z.c();
            }
            //Spigot end
        }
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void c(String s) {
        this.serverIp = s;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void safeShutdown() {
        this.isRunning = false;
    }

    public File y() {
        return new File(".");
    }

    // PaperSpigot start - Further improve tick loop
    private static final int TPS = 250;
    private static final long SEC_IN_NANO = 1000000000;
    private static final long TICK_TIME = SEC_IN_NANO / TPS;
    private static final int SAMPLE_INTERVAL = 20;
    private static int SAMPLE_SKIPS = 5;
    public static int currentTick = 0;

    public final RollingAverage tps = new RollingAverage(5, 20);
    public final RollingAverage tps1 = new RollingAverage(60, 20);
    public final RollingAverage tps5 = new RollingAverage(60 * 5, 20);
    private long curTime = System.nanoTime(), tickSection = curTime;

    @Override
    public void run() {
        try {
            if (this.init()) {
                this.ab = az();
                this.r.setServerInfo(new ServerPing.ServerData("1.8.8", 47));

                Arrays.fill(recentTps, 20);

                InvictusSpigot.INSTANCE.getThreadingManager().getSpigotTickingPool().scheduleAtFixedRate(() -> {
                    try {
                        serverThread = primaryThread = Thread.currentThread();
                        curTime = System.nanoTime();
                        if (++MinecraftServer.currentTick % SAMPLE_INTERVAL == 0) {
                            final long diff = curTime - tickSection;
                            if (SAMPLE_SKIPS < 0) {
                                double currentTps = 1E9 / diff * SAMPLE_INTERVAL;
                                tps.add(currentTps, diff);
                                tps1.add(currentTps, diff);
                                tps5.add(currentTps, diff);

                                // Backwards compat with bad plugins
                                recentTps[0] = tps.getAverage();
                                recentTps[1] = tps1.getAverage();
                                recentTps[2] = tps5.getAverage();
                            } else SAMPLE_SKIPS--;
                            tickSection = curTime;
                        }

                        A();
                    } catch (Exception exception) {
                        lastException = exception;
                    }
                }, 0, 50, TimeUnit.MILLISECONDS);

                InvictusSpigot.INSTANCE.getThreadingManager().getSpigotTickingPool().scheduleAtFixedRate(() -> {
                    int count = this.j.size();
                    FutureTask<?> entry;
                    while (count-- > 0 && (entry = this.j.poll()) != null)
                        SystemUtils.a(entry, LOGGER);
                }, TICK_TIME / 10, TICK_TIME / 10, TimeUnit.NANOSECONDS);

                while (isRunning) {
                    if (lastException != null) throw lastException;
                    Thread.sleep(50);
                }

                InvictusSpigot.INSTANCE.getThreadingManager().getSpigotTickingPool().shutdown();

                // Spigot end
            } else {
                this.a((CrashReport) null);
            }
        } catch (Throwable throwable) {
            MinecraftServer.LOGGER.error("Encountered an unexpected exception", throwable);
            // Spigot Start
            if (throwable.getCause() != null) {
                MinecraftServer.LOGGER.error("\tCause of unexpected exception was", throwable.getCause());
            }
            // Spigot End
            CrashReport crashreport;

            if (throwable instanceof ReportedException) {
                crashreport = this.b(((ReportedException) throwable).a());
            } else {
                crashreport = this.b(new CrashReport("Exception in server tick loop", throwable));
            }

            File file = new File(new File(this.y(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.a(file)) {
                MinecraftServer.LOGGER.error("This crash report has been saved to: " + file.getAbsolutePath());
            } else {
                MinecraftServer.LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.a(crashreport);
        } finally {
            this.serverThread = this.primaryThread = Thread.currentThread();
            try {
                WatchdogThread.doStop();
                this.isStopped = true;
                stop();
            } catch (Throwable throwable1) {
                LOGGER.error("Exception stopping the server", throwable1);
            } finally {
                try {
                    this.reader.getTerminal().restore();
                } catch (Exception ignored) {}
                z();
            }
        }

    }

    protected void a(CrashReport crashreport) {
    }

    protected void z() {
    }

    protected void A() throws ExceptionWorldConflict { // CraftBukkit - added throws
        ++this.ticks;
        this.B();

        if (autosavePeriod > 0 && this.ticks % autosavePeriod == 0) { // CraftBukkit
            this.v.savePlayers();
            // Spigot Start
            // We replace this with saving each individual world as this.saveChunks(...) is
            // broken,
            // and causes the main thread to sleep for random amounts of time depending on
            // chunk activity
            // Also pass flag to only save modified chunks
            server.playerCommandState = true;
            for (World world : worlds) {
                world.getWorld().save(false);
            }
            server.playerCommandState = false;
            // this.saveChunks(true);
            // Spigot End

        }
        org.spigotmc.WatchdogThread.tick(); // Spigot
    }

    public void B() {
        this.aq().c();

        this.server.getScheduler().mainThreadHeartbeat(this.ticks);

        ChunkIOExecutor.tick();

        if (ticks % 20 == 0) {
            for (EntityPlayer player : v.players) {
                player.playerConnection.sendPacket(new PacketPlayOutUpdateTime(player.world.getTime(), player.getPlayerTime(), player.world.getGameRules().getBoolean("doDaylightCycle")));
            }
        }

        for (WorldServer worldserver : worlds) {
            boolean old = worldserver.ticking;
            if(!worldserver.checkTicking() && !old)
                continue;

            CrashReport crashreport;

            try {
                worldserver.doTick();
            } catch (Throwable throwable) {
                try {
                    crashreport = CrashReport.a(throwable, "Exception ticking world");
                } catch (Throwable t) {
                    throw new RuntimeException("Error generating crash report", t);
                }
                worldserver.a(crashreport);
                throw new ReportedException(crashreport);
            }

            try {
                worldserver.tickEntities();
            } catch (Throwable throwable1) {
                try {
                    crashreport = CrashReport.a(throwable1,
                            "Exception ticking world entities [World Name: " + worldserver.getWorld().getName()
                                    + ", IWorldAccess Size: " + worldserver.u.size() + ", Entities: "
                                    + worldserver.entityList.size() + "]");
                } catch (Throwable t) {
                    throw new RuntimeException("Error generating crash report", t);
                }
                worldserver.a(crashreport);
                throw new ReportedException(crashreport);
            }
            worldserver.tracker.updatePlayers();
            worldserver.explosionDensityCache.clear();
        }

        if (InvictusConfig.updateLatency) {
            this.v.tick();
        }

        for (IUpdatePlayerListBox iUpdatePlayerListBox : this.p) {
            iUpdatePlayerListBox.c();
        }
    }

    public boolean getAllowNether() {
        return true;
    }

    public void a(IUpdatePlayerListBox iupdateplayerlistbox) {
        this.p.add(iupdateplayerlistbox);
    }

    public void C() {
    }

    public File d(String s) {
        return new File(this.y(), s);
    }

    public void info(String s) {
        MinecraftServer.LOGGER.info(s);
    }

    public void warning(String s) {
        MinecraftServer.LOGGER.warn(s);
    }

    public WorldServer getWorldServer(int i) {
        // CraftBukkit start
        for (WorldServer world : worlds) {
            if (world.dimension == i) {
                return world;
            }
        }
        return worlds.get(0);
        // CraftBukkit end
    }

    public String E() {
        return this.serverIp;
    }

    public int F() {
        return this.u;
    }

    public String G() {
        return this.motd;
    }

    public String getVersion() {
        return "1.8.8";
    }

    public int I() {
        return this.v.getPlayerCount();
    }

    public int J() {
        return this.v.getMaxPlayers();
    }

    public String[] getPlayers() {
        return this.v.f();
    }

    public GameProfile[] L() {
        return this.v.g();
    }

    public boolean isDebugging() {
        return this.getPropertyManager().getBoolean("debug", false); // CraftBukkit - don't hardcode
    }

    public void g(String s) {
        MinecraftServer.LOGGER.error(s);
    }

    public void h(String s) {
        if (this.isDebugging()) {
            MinecraftServer.LOGGER.info(s);
        }

    }

    public String getServerModName() {
        return "InvictusSpigot"; // PaperSpigot - PaperSpigot > // Spigot - Spigot > // CraftBukkit - cb > vanilla!
    }

    public CrashReport b(CrashReport crashreport) {
        if (this.v != null) {
            crashreport.g().a("Player Count", new Callable() {
                public String a() {
                    return MinecraftServer.this.v.getPlayerCount() + " / " + MinecraftServer.this.v.getMaxPlayers() + "; " + MinecraftServer.this.v.v();
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
        }

        return crashreport;
    }

    public List<String> tabCompleteCommand(ICommandListener icommandlistener, String s, BlockPosition blockposition) {
        return server.tabComplete(icommandlistener, s, blockposition);
    }

    public boolean O() {
        return true; // CraftBukkit
    }

    public String getName() {
        return "Server";
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        MinecraftServer.LOGGER.info(ichatbasecomponent.c());
    }

    public boolean a(int i, String s) {
        return true;
    }

    public ICommandHandler getCommandHandler() {
        return this.b;
    }

    public KeyPair Q() {
        return this.H;
    }

    public int R() {
        return this.u;
    }

    public void setPort(int i) {
        this.u = i;
    }

    public String S() {
        return this.I;
    }

    public void i(String s) {
        this.I = s;
    }

    public boolean T() {
        return this.I != null;
    }

    public String U() {
        return this.J;
    }

    public void a(KeyPair keypair) {
        this.H = keypair;
    }

    public void a(EnumDifficulty enumdifficulty) {
        // CraftBukkit start
        for (WorldServer worldserver : this.worlds) {
            // CraftBukkit end

            if (worldserver != null) {
                if (worldserver.getWorldData().isHardcore()) {
                    worldserver.getWorldData().setDifficulty(EnumDifficulty.HARD);
                    worldserver.setSpawnFlags(true, true);
                } else {
                    worldserver.getWorldData().setDifficulty(enumdifficulty);
                    worldserver.setSpawnFlags(this.getSpawnMonsters(), this.spawnAnimals);
                }
            }
        }

    }

    protected boolean getSpawnMonsters() {
        return true;
    }

    public boolean X() {
        return this.demoMode;
    }

    public void b(boolean flag) {
        this.demoMode = flag;
    }

    public void c(boolean flag) {

    }

    public Convertable getConvertable() {
        return this.convertable;
    }

    public void aa() {
        this.N = true;
        this.getConvertable().d();

        // CraftBukkit start
        for (WorldServer worldserver : this.worlds) {
            // CraftBukkit end

            if (worldserver != null) {
                worldserver.saveLevel();
            }
        }

        this.getConvertable().e(this.worlds.get(0).getDataManager().g()); // CraftBukkit
        this.safeShutdown();
    }

    public String getResourcePack() {
        return this.O;
    }

    public String getResourcePackHash() {
        return this.P;
    }

    public void setResourcePack(String s, String s1) {
        this.O = s;
        this.P = s1;
    }

    public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
    }

    public void b(MojangStatisticsGenerator mojangstatisticsgenerator) {
    }

    public boolean getSnooperEnabled() {
        return false;
    }

    public abstract boolean ae();

    public boolean getOnlineMode() {
        return server.getOnlineMode(); // CraftBukkit
    }

    public void setOnlineMode(boolean flag) {

    }

    public boolean getSpawnAnimals() {
        return this.spawnAnimals;
    }

    public void setSpawnAnimals(boolean flag) {
        this.spawnAnimals = flag;
    }

    public boolean getSpawnNPCs() {
        return this.spawnNPCs;
    }

    public void setSpawnNPCs(boolean flag) {
        this.spawnNPCs = flag;
    }

    public abstract boolean ai();

    public boolean getPVP() {
        return this.pvpMode;
    }

    public void setPVP(boolean flag) {
        this.pvpMode = flag;
    }

    public boolean getAllowFlight() {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean flag) {
        this.allowFlight = flag;
    }

    public abstract boolean getEnableCommandBlock();

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String s) {
        this.motd = s;
    }

    public int getMaxBuildHeight() {
        return this.F;
    }

    public void c(int i) {
        this.F = i;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public PlayerList getPlayerList() {
        return this.v;
    }

    public void a(PlayerList playerlist) {
        this.v = playerlist;
    }

    // Spigot Start
    public ServerConnection getServerConnection() {
        return this.q;
    }

    // Spigot End
    public ServerConnection aq() {
        return q; // Spigot
    }

    public boolean as() {
        return false;
    }

    public abstract String a(WorldSettings.EnumGamemode worldsettings_enumgamemode, boolean flag);

    public int at() {
        return this.ticks;
    }

    public BlockPosition getChunkCoordinates() {
        return BlockPosition.ZERO;
    }

    public Vec3D d() {
        return new Vec3D(0.0D, 0.0D, 0.0D);
    }

    public World getWorld() {
        return this.worlds.get(0); // CraftBukkit
    }

    public void setWorld(String s) {
        this.J = s;
    }

    public Entity f() {
        return null;
    }

    public int getSpawnProtection() {
        return 16;
    }

    public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return false;
    }

    public boolean getForceGamemode() {
        return this.U;
    }

    public void setForceGamemode(boolean flag) {
        this.U = flag;
    }

    public Proxy ay() {
        return this.e;
    }

    public int getIdleTimeout() {
        return this.G;
    }

    public void setIdleTimeout(int i) {
        this.G = i;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(this.getName());
    }

    public boolean aB() {
        return true;
    }

    public MinecraftSessionService aD() {
        return this.W;
    }

    public GameProfileRepository getGameProfileRepository() {
        return this.Y;
    }

    public UserCache getUserCache() {
        return this.Z;
    }

    public ServerPing aG() {
        return this.r;
    }

    public Entity a(UUID uuid) {
        // CraftBukkit start
        for (WorldServer worldserver : worlds) {
            // CraftBukkit end

            if (worldserver != null) {
                Entity entity = worldserver.getEntity(uuid);

                if (entity != null) {
                    return entity;
                }
            }
        }

        return null;
    }

    public boolean getSendCommandFeedback() {
        return getServer().worlds.get(0).getGameRules().getBoolean("sendCommandFeedback");
    }

    public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
    }

    public int aI() {
        return 29999984;
    }

    public <V> ListenableFuture<V> a(Callable<V> callable) {
        Validate.notNull(callable);
        if (!this.isMainThread()) { // CraftBukkit && !this.isStopped()) {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);

            // Spigot start
            j.add(listenablefuturetask);
            return listenablefuturetask;
            // Spigot end
        } else {
            try {
                return Futures.immediateFuture(callable.call());
            } catch (Exception exception) {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }

    public ListenableFuture<Object> postToMainThread(Runnable runnable) {
        Validate.notNull(runnable);
        return this.a(Executors.callable(runnable));
    }

    public boolean isMainThread() {
        return (Thread.currentThread() == this.serverThread);
    }

    public int aK() {
        return 256;
    }

    public long aL() {
        return this.ab;
    }

    public Thread aM() {
        return this.serverThread;
    }

    public static class RollingAverage {
        private final int size;
        private long time;
        private double total;
        private int index = 0;
        private final double[] samples;
        private final long[] times;

        RollingAverage(int size, int TPS) {
            this.size = size;
            this.time = size * SEC_IN_NANO;
            this.total = TPS * SEC_IN_NANO * size;
            this.samples = new double[size];
            this.times = new long[size];
            for (int i = 0; i < size; i++) {
                this.samples[i] = TPS;
                this.times[i] = SEC_IN_NANO;
            }
        }

        RollingAverage(int size) {
            this(size, TPS);
        }

        public void add(double x, long t) {
            time -= times[index];
            total -= samples[index] * times[index];
            samples[index] = x;
            times[index] = t;
            time += t;
            total += x * t;
            if (++index == size) {
                index = 0;
            }
        }

        public double getAverage() {
            return total / time;
        }
    }


}
