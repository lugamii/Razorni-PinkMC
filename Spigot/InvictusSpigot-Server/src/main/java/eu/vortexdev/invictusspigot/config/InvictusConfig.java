package eu.vortexdev.invictusspigot.config;

import com.google.common.base.Throwables;
import eu.vortexdev.invictusspigot.command.*;
import eu.vortexdev.invictusspigot.util.BukkitUtil;
import eu.vortexdev.invictusspigot.util.JavaUtil;
import eu.vortexdev.invictusspigot.util.commenter.YamlCommenter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;

public class InvictusConfig {
    private static final String HEADER =
            "        InvictusSpigot\r\n"
                    + " Made by Vortex Development ©\r\n"
                    + " https://discord.gg/j39HfaP7zN \r\n";

    public static YamlConfiguration CONFIG;
    private static File FILE;
    private static final Map<String, Command> commands = new HashMap<>();
    protected static final YamlCommenter commenter = new YamlCommenter();

    public static ChatColor mainColor = ChatColor.RED;
    public static String prefix = "&4[InvictusSpigot] &f";
    public static List<String> tpsCommand = new ArrayList<>();
    public static boolean connectionLogs = true;

    public static boolean spawnerParticles = true;
    public static int chunkMobLimit = 0;
    public static boolean disableMaxNearbyEntities = false;
    public static boolean disableLightLevelsCheck = false;
    public static boolean disableRandomSpawnLocations = false;
    public static int minSpawnDelay = 200;
    public static int maxSpawnDelay = 800;
    public static int spawnCount = 4;
    public static int spawnRange = 4;
    public static int maxNearbyEntities = 6;
    public static boolean requiredPlayersNearby = true;
    public static int requiredPlayersNearbyRange = 16;

    public static boolean playerDataSaving = true;
    public static boolean hidePlayersFromTab = false;
    public static boolean autoRespawn = false;
    public static boolean optimizePrinter = false;
    public static boolean patchFreecamCegging = true;
    public static boolean patchArmorNametags = true;
    public static boolean patchDropAndEat = true;
    public static boolean idleTimer = false;

    public static double brewingBoost = 1.0;
    public static double cookingBoost = 1.0;
    public static double fallDamageBoost = 1.0;
    public static float armorDamage = 12.0F;

    public static boolean blockPhysicsEvent = true;
    public static boolean playerMoveEvent = true;
    public static boolean hopperInventoryMoveItemEvent = true;
    public static boolean sandEntityChangeBlockEvent = true;
    public static boolean sandBlockPhysicsEvent = true;
    public static boolean redstoneBlockPhysicsEvent = true;

    public static float potionFallSpeed = 0.05F;
    public static float potionThrowMultiplier = 0.5F;
    public static float potionThrowOffset = -10.0F;
    public static boolean smoothHealPotions = true;

    public static int randomTickSpeed = 3;
    public static boolean tickWeather = true;
    public static boolean tickVillages = true;
    public static boolean updateLatency = true;
    public static boolean disableLighting = false;

    public static boolean sleepCheck = false;
    public static boolean flightCheck = false;
    public static boolean borderDamageCheck = false;

    public static int chunkLoadingThreads = 3;
    public static int chunkPlayersPerThread = 100;
    public static boolean unloadChunks = true;

    public static int maxPistonPush = 12;
    public static boolean infiniteWaterSources = true;
    public static boolean gravelGravity = true;
    public static boolean decayDirt = true;
    public static boolean decayLeaf = true;
    public static boolean dropExplosionBlocks = true;
    public static boolean dropFloatingPlants = true;
    public static boolean grassIgnoreLight = false;
    public static boolean waterDestroysRedstone = true;
    public static boolean generateObsidian = true;
    public static boolean generateCobble = true;
    public static boolean generateStone = true;

    public static boolean mobAi = true;
    public static boolean mobSpawn = true;

    public static boolean entityCollisions = true;
    public static int collisionsDelay = 4;

    public static boolean itemFizzSounds = true;
    public static boolean fluidFizzSounds = true;
    public static boolean mobRandomSounds = false;
    public static boolean footstepSounds = true;
    public static boolean explosionSounds = true;

    public static boolean explosionParticles = true;
    public static boolean tntParticles = true;
    public static boolean footstepParticles = true;

    public static boolean fixSandUnloadingChunk = false;
    public static boolean hideSand = false;
    public static boolean hideTnt = false;
    public static boolean fixEastWest = true;
    public static boolean pistonEntityPushing = false;

    public static boolean dispenserRandomness = true;
    public static boolean optimizeTntMovement = true;
    public static boolean optimizeSandMovement = false;

    private static void commands() {
        commands.put("knockback", new KnockbackCommand());
        commands.put("potion", new PotionCommand());
        commands.put("setslots", new SetSlotsCommand());
        commands.put("killentities", new KillEntitiesCommand());
        commands.put("configreload", new ConfigReloadCommand());
        commands.put("chunkunload", new ChunkUnloadCommand());
        commands.put("setnotrack", new SetNoTrackCommand());
    }

    public static void init(File file) {
        FILE = file;
        CONFIG = new YamlConfiguration();
        try {
            JavaUtil.createIfNotExists(FILE);
            CONFIG.load(FILE);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load vortex.yml file, please correct your syntax errors", exception);
            throw Throwables.propagate(exception);
        }
        CONFIG.options().copyDefaults(true);

        commenter.setHeader(HEADER);

        readConfig(InvictusConfig.class, null);
    }

    private static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers()) && (method.getParameterTypes()).length == 0 && method.getReturnType() == void.class)
                try {
                    method.setAccessible(true);
                    method.invoke(null);
                } catch (InvocationTargetException exception) {
                    throw Throwables.propagate(exception.getCause());
                } catch (Exception exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, exception);
                }
        }
        for (PearlConfig setting : PearlConfig.values()) {
            String con = setting.getConfigSection();
            if (con.isEmpty())
                continue;
            CONFIG.addDefault(con, setting.getValue());
            setting.setValue(CONFIG.get(con, setting.getValue()));
        }
        try {
            CONFIG.save(FILE);
            commenter.saveComments(FILE, instance != null);
        } catch (IOException exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + FILE, exception);
        }
    }

    public static void registerCommands() {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register( entry.getKey(), "InvictusSpigot", entry.getValue() );
        }
    }

    public static void save() {
        save(null);
    }

    public static void save(Object instance) {
        try {
            JavaUtil.saveFile(CONFIG, FILE);
            commenter.saveComments(FILE, instance != null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void set(String path, Object val) {
        CONFIG.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getBoolean(path, CONFIG.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getDouble(path, CONFIG.getDouble(path));
    }

    private static float getFloat(String path, float def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getFloat(path, CONFIG.getFloat(path));
    }

    private static int getInt(String path, int def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getInt(path, CONFIG.getInt(path));
    }

    private static <T> List getList(String path, T def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getList(path, CONFIG.getList(path));
    }

    private static String getString(String path, String def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getString(path, CONFIG.getString(path));
    }

    // Performance
    private static void tickVillages() {
        tickVillages = getBoolean("performance.tick.villages", tickVillages);
        commenter.addComment("performance.tick.villages", "Toggles villages ticking. Disable if villages aren't used on the server");
    }
    private static void tickWeather() {
        tickWeather = getBoolean("performance.tick.weather", tickWeather);
        commenter.addComment("performance.tick.weather", "Toggles weather ticking");
    }
    private static void updateLatency() {
        updateLatency = getBoolean("performance.tick.tabLatency", updateLatency);
        commenter.addComment("performance.tick.tabLatency", "Toggles tab latency ticking. Updates the ping of players in tab");
    }
    private static void randomTickSpeed() {
        randomTickSpeed = getInt("performance.randomTickSpeed", randomTickSpeed);
        commenter.addComment("performance.randomTickSpeed", "Speed of ticking blocks. Can be changed using /gamerule randomTickSpeed <value>");
    }
    private static void disableLighting() {
        disableLighting = getBoolean("performance.disableLighting", disableLighting);
        commenter.addComment("performance.disableLighting", "Toggles spigot lighting system (Useful for Practice)");
    }
    
    // Spawners
    private static void chunkMobLimit() {
        chunkMobLimit = getInt("spawners.chunkMobLimit", chunkMobLimit);
        commenter.addComment("spawners.chunkMobLimit", "Edit spawners mob amount limit in chunk. 0 - to disable.");
    }
    private static void minSpawnDelay() {
        minSpawnDelay = getInt("spawners.minSpawnDelay", minSpawnDelay);
        commenter.addComment("spawners.minSpawnDelay", "Edit minimum spawner spawn delay");
    }
    private static void maxSpawnDelay() {
        maxSpawnDelay = getInt("spawners.maxSpawnDelay", maxSpawnDelay);
        commenter.addComment("spawners.maxSpawnDelay", "Edit maximum spawner spawn delay");
    }
    private static void spawnCount() {
        spawnCount = getInt("spawners.spawnCount", spawnCount);
        commenter.addComment("spawners.spawnCount", "Edit amount of entities to spawn");
    }
    private static void spawnRange() {
        spawnRange = getInt("spawners.spawnRange", spawnRange);
        commenter.addComment("spawners.spawnRange", "Edit entity spawn range from spawner");
    }
    private static void disableRandomSpawnLocations() {
        disableRandomSpawnLocations = getBoolean("spawners.disableRandomSpawnLocations", disableRandomSpawnLocations);
        commenter.addComment("spawners.disableRandomSpawnLocations", "Toggles randomized spawn locations");
    }
    private static void disableLightLevelsCheck() {
        disableLightLevelsCheck = getBoolean("spawners.disableLightLevelsCheck", disableLightLevelsCheck);
        commenter.addComment("spawners.disableLightLevelsCheck", "Toggles light level check for spawners");
    }

    private static void disableMaxNearbyEntities() {
        disableMaxNearbyEntities = getBoolean("spawners.disableMaxNearbyEntities", disableMaxNearbyEntities);
        commenter.addComment("spawners.disableMaxNearbyEntities", "Toggles max nearby entities around spawner check");
    }
    private static void maxNearbyEntities() {
        maxNearbyEntities = getInt("spawners.maxNearbyEntities", maxNearbyEntities);
        commenter.addComment("spawners.maxNearbyEntities", "Edit amount of entities around spawner to prevent spawning");
    }

    private static void requiredPlayers() {
        requiredPlayersNearby = getBoolean("spawners.requiredPlayersNearby", requiredPlayersNearby);
        commenter.addComment("spawners.requiredPlayersNearby", "Toggles required players nearby check");
    }
    private static void requiredPlayerRange() {
        requiredPlayersNearbyRange = getInt("spawners.requiredPlayersNearbyRange", requiredPlayersNearbyRange);
        commenter.addComment("spawners.requiredPlayersNearbyRange", "Edit range of required players from spawner");
    }

    private static void spawnerParticles() {
        spawnerParticles = getBoolean("spawners.particles", spawnerParticles);
        commenter.addComment("spawners.particles", "Toggles spawner particles");
    }

    // Mobs
    private static void mobAi() {
        mobAi = getBoolean("mobs.ai", mobAi);
        commenter.addComment("mobs.ai", "Toggles mob AI");
    }
    private static void mobSpawn() {
        mobSpawn = getBoolean("mobs.naturalSpawn", mobSpawn);
        commenter.addComment("mobs.naturalSpawn", "Toggles natural mob spawning. If disabled, mobs are spawning only from spawners. Can be changed using /gamerule doMobSpawning <value>");
    }
    private static void collisionsDelay() {
        collisionsDelay = getInt("mobs.collisionsDelay", collisionsDelay);
        commenter.addComment("mobs.collisionsDelay", "Cooldown between next entity collision with other entities. Recommended to keep 4 by default");
    }
    private static void entityCollisions() {
        entityCollisions = getBoolean("mobs.entityCollisions", entityCollisions);
        commenter.addComment("mobs.entityCollisions", "Toggles entity collisions");
    }

    // Potions
    private static void potionFallSpeed() {
        potionFallSpeed = getFloat("potions.gravity", potionFallSpeed);
        commenter.addComment("potions.gravity", "Potions fall speed value");
    }
    private static void potionThrowMultiplier() {
        potionThrowMultiplier = getFloat("potions.speed", potionThrowMultiplier);
        commenter.addComment("potions.speed", "Potions throwing multiplier value");
    }
    private static void potionThrowOffset() {
        potionThrowOffset = getFloat("potions.verticalOffset", potionThrowOffset);
        commenter.addComment("potions.verticalOffset", "Potions vertical throwing offset value");
    }
    private static void smoothHealPotions() {
        smoothHealPotions = getBoolean("potions.smoothHealPotions", smoothHealPotions);
        commenter.addComment("potions.smoothHealPotions", "Make heal potion throwing smoother (SagePvP Potions Replica)");
    }

    // Player
    private static void playerDataSaving() {
        playerDataSaving = getBoolean("player.playerDataSaving", playerDataSaving);
        commenter.addComment("player.playerDataSaving", "Toggles player data saving");
    }
    private static void patchFreecamCegging() {
        patchFreecamCegging = getBoolean("player.patchFreecamCegging", patchFreecamCegging);
        commenter.addComment("player.patchFreecamCegging", "Toggles fix for freecam cegging. May cause performance issues");
    }
    private static void patchDropAndEat() {
        patchDropAndEat = getBoolean("player.patchDropAndEat", patchDropAndEat);
        commenter.addComment("player.patchDropAndEat", "Toggles patch while dropping food and eating.");
    }
    private static void patchArmorNametags() {
        patchArmorNametags = getBoolean("player.patchArmorNametags", patchArmorNametags);
        commenter.addComment("player.patchArmorNametags", "Toggles patch of armor visibility using Nametags cheat.");
    }
    private static void hidePlayersFromTab() {
        hidePlayersFromTab = getBoolean("player.hidePlayersFromTab", hidePlayersFromTab);
        commenter.addComment("player.hidePlayersFromTab", "Hide hidden players from player tab");
    }
    private static void autoRespawn() {
        autoRespawn = getBoolean("player.autoRespawn", autoRespawn);
        commenter.addComment("player.autoRespawn", "Auto respawn immediately after dying");
    }
    private static void optimizePrinter() {
        optimizePrinter = getBoolean("player.optimizePrinter", optimizePrinter);
        commenter.addComment("player.optimizePrinter", "Removes block placing cooldown delay");
    }
    private static void armorDamage() {
        armorDamage = getFloat("player.hitArmorDamage", armorDamage);
        commenter.addComment("player.hitArmorDamage", "Changes armor damage per hit. Default is 12.0. For HCF recommends 4.0");
    }
    private static void idleTimer() {
        idleTimer = getBoolean("player.idleTimer", idleTimer);
        commenter.addComment("player.idleTimer", "Toggles player idle timer (Afk timer)");
    }
    private static void flightCheck() {
        flightCheck = getBoolean("player.flightCheck", flightCheck);
        commenter.addComment("player.flightCheck", "Toggles player flight check. Disable if you have any anticheat on");
    }
    private static void borderDamageCheck() {
        borderDamageCheck = getBoolean("player.borderDamageCheck", borderDamageCheck);
        commenter.addComment("player.borderDamageCheck", "Toggles player damage outside of border check every tick");
    }
    private static void sleepCheck() {
        sleepCheck = getBoolean("player.everybodySleepCheck", sleepCheck);
        commenter.addComment("player.everybodySleepCheck", "Toggles everybody sleep checking. Disable if beds mechanic isn't used on the server");
    }

    // World
    private static void optimizeSandMovement() {
        optimizeSandMovement = getBoolean("world.optimizeSandMovement", optimizeSandMovement);
        commenter.addComment("world.optimizeSandMovement", "Optimizes sand movement. Attention: breaks floating plants sand trap!");
    }
    private static void fixSandUnloadingChunk() {
        fixSandUnloadingChunk = getBoolean("world.fixSandUnloading", fixSandUnloadingChunk);
        commenter.addComment("world.fixSandUnloading", "Fixes sand not loading the chunks");
    }
    private static void dropFloatingPlants() {
        dropFloatingPlants = getBoolean("world.dropFloatingPlants", dropFloatingPlants);
        commenter.addComment("world.dropFloatingPlants", "Toggles dropping floating plants. Recommending for Hub servers");
    }
    private static void dispenserRandomness() {
        dispenserRandomness = getBoolean("world.dispenserRandomness", dispenserRandomness);
        commenter.addComment("world.dispenserRandomness", "Toggles dispenser randomness. Optimizes cannons");
    }
    private static void pistonEntityPushing() {
        pistonEntityPushing = getBoolean("world.pistonEntityPushing", pistonEntityPushing);
        commenter.addComment("cannons.pistonEntityPushing", "Toggles pushing out entities out of piston check. Optimizes cannons");
    }
    private static void maxPistonPush() {
        maxPistonPush = getInt("world.maxPistonPush", maxPistonPush);
        commenter.addComment("world.maxPistonPush", "Edit max piston push blocks amount");
    }
    private static void waterDestroysRedstone() {
        waterDestroysRedstone = getBoolean("world.waterDestroysRedstone", waterDestroysRedstone);
        commenter.addComment("world.waterDestroysRedstone", "Toggles water destroying redstone. Used for cannons.");
    }
    private static void gravelGravity() {
        gravelGravity = getBoolean("world.gravelGravity", gravelGravity);
        commenter.addComment("world.gravelGravity", "Toggles gravel physics");
    }
    private static void decayDirt() {
        decayDirt = getBoolean("world.dirtDecay", decayDirt);
        commenter.addComment("world.dirtDecay", "Toggles dirt decaying");
    }
    private static void decayLeaf() {
        decayLeaf = getBoolean("world.leafDecay", decayLeaf);
        commenter.addComment("world.leafDecay", "Toggles leaf decaying");
    }
    private static void infiniteWaterSources() {
        infiniteWaterSources = getBoolean("world.infiniteWaterSources", infiniteWaterSources);
        commenter.addComment("world.infiniteWaterSources", "Toggles infinite water sources");
    }
    private static void grassIgnoreLight() {
        grassIgnoreLight = getBoolean("world.grassIgnoreLight", grassIgnoreLight);
        commenter.addComment("world.grassIgnoreLight", "Toggles grass ignoring light for spread");
    }
    private static void dropExplosionBlocks() {
        dropExplosionBlocks = getBoolean("world.dropExplosionBlocks", dropExplosionBlocks);
        commenter.addComment("world.dropExplosionBlocks", "Toggles explosions dropping blocks");
    }

    // Fluids
    private static void generateObsidian() {
        generateObsidian = getBoolean("fluids.generateObsidian", generateObsidian);
        commenter.addComment("fluids.generateObsidian", "Toggles fluids generate obsidian");
    }
    private static void generateCobble() {
        generateCobble = getBoolean("fluids.generateCobble", generateCobble);
        commenter.addComment("fluids.generateCobble", "Toggles fluids generate cobble");
    }
    private static void generateStone() {
        generateStone = getBoolean("fluids.generateStone", generateStone);
        commenter.addComment("fluids.generateStone", "Toggles fluids generate stone");
    }

    // Chunks
    private static void unloadChunks() {
        unloadChunks = getBoolean("chunks.unload", unloadChunks);
        commenter.addComment("chunks.unload", "Toggles chunk unloading");
    }
    private static void chunkLoadingThreads() {
        chunkLoadingThreads = getInt("chunks.threads", chunkLoadingThreads);
        commenter.addComment("chunks.threads", "The amount of threads used for loading chunks");
    }
    private static void chunkPlayersPerThread() {
        chunkPlayersPerThread = getInt("chunks.playersPerThread", chunkPlayersPerThread);
        commenter.addComment("chunks.playersPerThread", "The amount of players for each thread");
    }

    // Boost
    private static void brewingBoost() {
        brewingBoost = getDouble("boost.brewing", brewingBoost);
        commenter.addComment("boost.brewing", "Multiplier of potion brewing speed");
    }
    private static void cookingBoost() {
        cookingBoost = getDouble("boost.cooking", cookingBoost);
        commenter.addComment("boost.cooking", "Multiplier of cooking speed");
    }
    private static void fallDamageBoost() {
        fallDamageBoost = getDouble("boost.fallDamage", fallDamageBoost);
        commenter.addComment("boost.fallDamage", "Multiplier of fall damage");
    }

    // Sounds
    private static void fluidFizzSounds() {
        fluidFizzSounds = getBoolean("sounds.fluidsFizzSounds", fluidFizzSounds);
        commenter.addComment("sounds.fluidsFizzSounds", "Toggles all water & lava fizz sounds");
    }
    private static void itemFizzSounds() {
        itemFizzSounds = getBoolean("sounds.itemFizzSounds", itemFizzSounds);
        commenter.addComment("sounds.itemFizzSounds", "Toggles item fizz sounds on burn");
    }
    private static void footstepSounds() {
        footstepSounds = getBoolean("sounds.footstepSounds", footstepSounds);
        commenter.addComment("sounds.footstepSounds", "Toggles all entities footstep sounds");
    }
    private static void mobRandomSounds() {
        mobRandomSounds = getBoolean("sounds.mobRandomSounds", mobRandomSounds);
        commenter.addComment("sounds.mobRandomSounds", "Toggles useless random mobs sounds");
    }
    private static void explosionSounds() {
        explosionSounds = getBoolean("sounds.explosionSounds", explosionSounds);
        commenter.addComment("sounds.explosionSounds", "Toggles explosions sounds");
    }

    // Events
    private static void firePlayerMoveEvent() {
        playerMoveEvent = getBoolean("events.playerMoveEvent", playerMoveEvent);
        commenter.addComment("events.playerMoveEvent", "Toggles player move event");
    }
    private static void fireInventoryMoveItemEvent() {
        hopperInventoryMoveItemEvent = getBoolean("events.inventoryMoveItemEvent", hopperInventoryMoveItemEvent);
        commenter.addComment("events.inventoryMoveItemEvent", "Toggles super laggy hopper event");
    }
    private static void fireSandEntityChangeBlockEvent() {
        sandEntityChangeBlockEvent = getBoolean("events.sandEntityChangeBlockEvent", sandEntityChangeBlockEvent);
        commenter.addComment("events.sandEntityChangeBlockEvent", "Toggles falling blocks fire ECBE");
    }
    private static void fireBlockPhysicsEvent() {
        blockPhysicsEvent = getBoolean("events.blockPhysicsEvent", blockPhysicsEvent);
        commenter.addComment("events.blockPhysicsEvent", "Toggles fire BPE on block physics");
    }
    private static void fireSandBlockPhysicsEvent() {
        sandBlockPhysicsEvent = getBoolean("events.sandBlockPhysicsEvent", sandBlockPhysicsEvent);
        commenter.addComment("events.sandBlockPhysicsEvent", "Toggles falling blocks fire BPE");
    }
    private static void fireRedstoneBlockPhysicsEvent() {
        redstoneBlockPhysicsEvent = getBoolean("events.redstoneBlockPhysicsEvent", redstoneBlockPhysicsEvent);
        commenter.addComment("events.redstoneBlockPhysicsEvent", "Toggles redstone fire BPE");
    }

    // Particles
    private static void explosionParticles() {
        explosionParticles = getBoolean("particles.explosionParticles", explosionParticles);
        commenter.addComment("particles.explosionParticles", "Toggles explosions particles");
    }
    private static void tntParticles() {
        tntParticles = getBoolean("particles.tntParticles", tntParticles);
        commenter.addComment("particles.tntParticles", "Toggles TNT particles");
    }
    private static void footstepParticles() {
        footstepParticles = getBoolean("particles.footstepParticles", footstepParticles);
        commenter.addComment("particles.footstepParticles", "Toggles footstep particles");
    }

    // Entities
    private static void hideTnt() {
        hideTnt = getBoolean("entities.hideTnt", hideTnt);
        commenter.addComment("entities.hideTnt", "Toggles visibility of TNT entities.");
    }
    private static void hideSand() {
        hideSand = getBoolean("entities.hideSand", hideSand);
        commenter.addComment("entities.hideSand", "Toggles visibility of falling blocks.");
    }

    // Cannons
    private static void optimizeTntMovement() {
        optimizeTntMovement = getBoolean("cannons.optimizeTNTMovement", optimizeTntMovement);
        commenter.addComment("cannons.optimizeTNTMovement", "TacoSpigot's tnt movement optimization.");
    }
    private static void fixEastWest() {
        fixEastWest = getBoolean("cannons.fix-east-west-cannoning", fixEastWest);
        commenter.addComment("cannons.fix-east-west-cannoning", "TacoSpigot's east west cannoning fix.");
    }

    // Other
    private static void connectionLogs() {
        connectionLogs = getBoolean("other.connectionLogs", connectionLogs);
        commenter.addComment("other.connectionLogs", "Toggles all logs in the console on player join");
    }

    private static void mainColor() {
        try {
            mainColor = ChatColor.valueOf(getString("other.themeColor", "RED").toUpperCase());
        } catch (Exception e) {
            MinecraftServer.LOGGER.error("Invalid theme color, defaulting to red! Change it in our config.");
            mainColor = ChatColor.RED;
        }
        commenter.addComment("other.themeColor", "Edit spigot's theme color");
    }

    private static void prefix() {
        prefix = BukkitUtil.translate(getString("other.prefix", prefix));
        commenter.addComment("other.prefix", "Edit spigot's commands prefix");
    }

    private static void tpsCommand() {
        tpsCommand.clear();
        tpsCommand.addAll(BukkitUtil.translate(getList("other.tpsCommand", Arrays.asList(
                "&7§m-------------------------",
                "&6Server Info:",
                "&7* &eTPS: %list_tps%",
                "&7* &eMemory: &a%used_ram%/%total_ram% MB",
                "&7* &eUptime: &a%uptime%",
                "&7* &eOnline: &a%total_player_online%/%max_slot%",
                "&7* &eChunks: &a%total_chunk_loaded% &eEntities: &a%total_entity%",
                "&7&m-------------------------"
        ))));
        commenter.addComment("other.tpsCommand", "Edit spigot's TPS command message (%free_ram% , %current_tps% works too)");
    }

}
