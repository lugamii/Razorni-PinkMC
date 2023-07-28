package net.minecraft.server;

import com.eatthepath.uuid.FastUUID;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerList {

    public static final File a = new File("banned-players.json");
    public static final File b = new File("banned-ips.json");
    public static final File c = new File("ops.json");
    public static final File d = new File("whitelist.json");
    private static final Logger f = LogManager.getLogger();
    private static final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    public final List<EntityPlayer> players = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final MinecraftServer server;
    private final Map<UUID, EntityPlayer> j = Maps.newHashMap();
    private final GameProfileBanList k;
    private final IpBanList l;
    private final OpList operators;
    private final WhiteList whitelist;
    private final Map<UUID, ServerStatisticManager> o;
    public final Map<String, EntityPlayer> playersByName = new org.spigotmc.CaseInsensitiveMap<>();
    // CraftBukkit start
    private final CraftServer cserver;
    public IPlayerFileData playerFileData;
    public int maxPlayers;
    private boolean hasWhitelist;
    private int r;
    private int u;

    public PlayerList(MinecraftServer minecraftserver) {
        this.cserver = minecraftserver.server = new CraftServer(minecraftserver, this);
        minecraftserver.console = org.bukkit.craftbukkit.command.ColouredConsoleSender.getInstance();
        minecraftserver.reader.addCompleter(new org.bukkit.craftbukkit.command.ConsoleCommandCompleter(minecraftserver.server));

        this.k = new GameProfileBanList(PlayerList.a);
        this.l = new IpBanList(PlayerList.b);
        this.operators = new OpList(PlayerList.c);
        this.whitelist = new WhiteList(PlayerList.d);
        this.o = Maps.newHashMap();
        this.server = minecraftserver;
        this.k.a(false);
        this.l.a(false);
        this.maxPlayers = 8;
    }

    public void a(NetworkManager networkmanager, EntityPlayer entityplayer) {
        GameProfile gameprofile = entityplayer.getProfile();
        UserCache usercache = this.server.getUserCache();
        GameProfile gameprofile1 = usercache.a(gameprofile.getId());
        String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();

        usercache.a(gameprofile);
        NBTTagCompound nbttagcompound = this.a(entityplayer);
        // CraftBukkit start - Better rename detection
        if (nbttagcompound != null && nbttagcompound.hasKey("bukkit")) {
            NBTTagCompound bukkit = nbttagcompound.getCompound("bukkit");
            s = bukkit.hasKeyOfType("lastKnownName", 8) ? bukkit.getString("lastKnownName") : s;
        }
        // CraftBukkit end

        // PaperSpigot start - support PlayerInitialSpawnEvent
        PlayerInitialSpawnEvent event = new org.bukkit.event.player.PlayerInitialSpawnEvent(
                entityplayer.getBukkitEntity(), new Location(entityplayer.world.getWorld(), entityplayer.locX,
                entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch));
        this.server.server.getPluginManager().callEvent(event);

        Location newLoc = event.getSpawnLocation();
        WorldServer serv = ((CraftWorld) newLoc.getWorld()).getHandle();
        entityplayer.world = serv;
        entityplayer.locX = newLoc.getX();
        entityplayer.locY = newLoc.getY();
        entityplayer.locZ = newLoc.getZ();
        entityplayer.yaw = newLoc.getYaw();
        entityplayer.pitch = newLoc.getPitch();
        entityplayer.dimension = serv.dimension;
        entityplayer.spawnWorld = entityplayer.world.worldData.getName();
        // PaperSpigot end
        entityplayer.spawnIn(serv);

        String s1 = "local";

        if (networkmanager.getSocketAddress() != null) {
            s1 = networkmanager.getSocketAddress().toString();
        }

        // Spigot start - spawn location event
        Player bukkitPlayer = entityplayer.getBukkitEntity();
        PlayerSpawnLocationEvent ev = new PlayerSpawnLocationEvent(bukkitPlayer, bukkitPlayer.getLocation());
        Bukkit.getPluginManager().callEvent(ev);

        Location loc = ev.getSpawnLocation();

        entityplayer.spawnIn(((CraftWorld) loc.getWorld()).getHandle());
        entityplayer.setPosition(loc.getX(), loc.getY(), loc.getZ());
        entityplayer.setYawPitch(loc.getYaw(), loc.getPitch());
        // Spigot end

        WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);
        WorldData worlddata = worldserver.getWorldData();
        BlockPosition blockposition = worldserver.getSpawn();

        entityplayer.playerInteractManager.b(worldserver.getWorldData().getGameType());

        PlayerConnection playerconnection = new PlayerConnection(this.server, networkmanager, entityplayer);
        playerconnection.sendPacket(
                new PacketPlayOutLogin(entityplayer.getId(), entityplayer.playerInteractManager.getGameMode(),
                        worlddata.isHardcore(), worldserver.worldProvider.getDimension(), worldserver.getDifficulty(),
                        Math.min(this.getMaxPlayers(), 60), worlddata.getType(),
                        worldserver.getGameRules().getBoolean("reducedDebugInfo"))); // CraftBukkit - cap player list to 60
        entityplayer.getBukkitEntity().sendSupportedChannels(); // CraftBukkit
        playerconnection.sendPacket(new PacketPlayOutCustomPayload("MC|Brand", new PacketDataSerializer(Unpooled.buffer()).a(this.getServer().getServerModName())));
        playerconnection.sendPacket(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        playerconnection.sendPacket(new PacketPlayOutSpawnPosition(blockposition));
        playerconnection.sendPacket(new PacketPlayOutAbilities(entityplayer.abilities));
        playerconnection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.inventory.itemInHandIndex));
        entityplayer.getStatisticManager().d();
        // Vortex - Remove achievements
        //entityplayer.getStatisticManager().updateStatistics(entityplayer);
        this.sendScoreboard((ScoreboardServer) worldserver.getScoreboard(), entityplayer);

        String joinMessage;
        if (!entityplayer.getName().equalsIgnoreCase(s)) {
            joinMessage = "\u00A7e" + LocaleI18n.a("multiplayer.player.joined.renamed", entityplayer.getName(), s);
        } else {
            joinMessage = "\u00A7e" + LocaleI18n.a("multiplayer.player.joined", entityplayer.getName());
        }

        this.onPlayerJoin(entityplayer, joinMessage);

        worldserver = server.getWorldServer(entityplayer.dimension);
        playerconnection.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw,
                entityplayer.pitch);
        this.b(entityplayer, worldserver);
        if (!this.server.getResourcePack().isEmpty()) {
            entityplayer.setResourcePack(this.server.getResourcePack(), this.server.getResourcePackHash());
        }

        for (MobEffect effect : entityplayer.getEffects()) {
            playerconnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), effect));
        }

        entityplayer.syncInventory();
        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Riding", 10)) {
            Entity entity = EntityTypes.a(nbttagcompound.getCompound("Riding"), worldserver);

            if (entity != null) {
                entity.attachedToPlayer = true;
                worldserver.addEntity(entity);
                entityplayer.mount(entity);
                entity.attachedToPlayer = false;
            }
        }

        if (InvictusConfig.connectionLogs)
            PlayerList.f.info(entityplayer.getName() + "[" + s1 + "] logged in with entity id " + entityplayer.getId()
                + " at ([" + entityplayer.world.worldData.getName() + "]" + entityplayer.locX + ", " + entityplayer.locY
                + ", " + entityplayer.locZ + ")");
    }

    public void sendScoreboard(ScoreboardServer scoreboardserver, EntityPlayer entityplayer) {
        List<ScoreboardObjective> list = Lists.newArrayList();

        for (ScoreboardTeam scoreboardTeam : scoreboardserver.getTeams()) {
            entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardTeam, 0));
        }

        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjectiveForSlot(i);

            if (scoreboardobjective != null && !list.contains(scoreboardobjective)) {

                for (Packet<PacketListenerPlayOut> packet : scoreboardserver.getScoreboardScorePacketsForObjective(scoreboardobjective)) {
                    entityplayer.playerConnection.sendPacket(packet);
                }

                list.add(scoreboardobjective);
            }
        }

    }

    public void setPlayerFileData(WorldServer[] aworldserver) {
        if (playerFileData != null)
            return; // CraftBukkit
        this.playerFileData = aworldserver[0].getDataManager().getPlayerFileData();
        aworldserver[0].getWorldBorder().a(new IWorldBorderListener() {
            public void a(WorldBorder worldborder, double d0) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder,
                        PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
            }

            public void a(WorldBorder worldborder, double d0, double d1, long i) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder,
                        PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));
            }

            public void a(WorldBorder worldborder, double d0, double d1) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder,
                        PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
            }

            public void a(WorldBorder worldborder, int i) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder,
                        PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_TIME));
            }

            public void b(WorldBorder worldborder, int i) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder,
                        PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
            }

            public void b(WorldBorder worldborder, double d0) {
            }

            public void c(WorldBorder worldborder, double d0) {
            }
        });
    }

    public void a(EntityPlayer entityplayer, WorldServer worldserver) {
        WorldServer worldserver1 = entityplayer.u();

        if (worldserver != null) {
            worldserver.getPlayerChunkMap().removePlayer(entityplayer);
        }

        worldserver1.getPlayerChunkMap().addPlayer(entityplayer);
        worldserver1.chunkProviderServer.getChunkAt((int) entityplayer.locX >> 4, (int) entityplayer.locZ >> 4);
    }

    public int d() {
        return PlayerChunkMap.getFurthestViewableBlock(this.s());
    }

    public NBTTagCompound a(EntityPlayer entityplayer) {
        if (!InvictusConfig.playerDataSaving)
            return null;
        return playerFileData.load(entityplayer);
    }

    protected void savePlayerFile(EntityPlayer entityplayer) {
        if (!InvictusConfig.playerDataSaving)
            return;
        this.playerFileData.save(entityplayer);
        ServerStatisticManager serverstatisticmanager = this.o.get(entityplayer.getUniqueID());

        if (serverstatisticmanager != null) {
            serverstatisticmanager.b();
        }

    }

    public void onPlayerJoin(EntityPlayer entityPlayer, String joinMessage) { // CraftBukkit added param
        this.players.add(entityPlayer);
        this.playersByName.put(entityPlayer.getName(), entityPlayer); // Spigot
        this.j.put(entityPlayer.getUniqueID(), entityPlayer);

        WorldServer worldserver = this.server.getWorldServer(entityPlayer.dimension);
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this.cserver.getPlayer(entityPlayer), joinMessage);
        this.cserver.getPluginManager().callEvent(playerJoinEvent);
        joinMessage = playerJoinEvent.getJoinMessage();
        if (joinMessage != null && joinMessage.length() > 0)
            for (IChatBaseComponent line : CraftChatMessage.fromString(joinMessage))
                this.server.getPlayerList().sendAll(new PacketPlayOutChat(line));
        ChunkIOExecutor.adjustPoolSize(getPlayerCount());

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        CraftPlayer player = entityPlayer.getBukkitEntity();
        for (EntityPlayer onlinePlayer : players) {
            CraftPlayer online = onlinePlayer.getBukkitEntity();
            if (!InvictusConfig.hidePlayersFromTab || online.canSee(player))
                onlinePlayer.playerConnection.sendPacket(packet);
            if (!InvictusConfig.hidePlayersFromTab || player.canSee(online))
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, onlinePlayer));
        }

        // CraftBukkit start - Only add if the player wasn't moved in the event
        if (entityPlayer.world == worldserver && !worldserver.players.contains(entityPlayer)) {
            worldserver.addEntity(entityPlayer);
            this.a(entityPlayer, (WorldServer) null);
        }
        // CraftBukkit end
    }

    public void d(EntityPlayer entityplayer) {
        entityplayer.u().getPlayerChunkMap().movePlayer(entityplayer);
    }

    public String disconnect(EntityPlayer entityplayer) {
        // CraftBukkit start - Quitting must be before we do final save of data, in case
        // plugins need to modify it
        org.bukkit.craftbukkit.event.CraftEventFactory.handleInventoryCloseEvent(entityplayer);

        Player bukkit = cserver.getPlayer(entityplayer);
        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(bukkit,
                "\u00A7e" + entityplayer.getName() + " left the game.");
        cserver.getPluginManager().callEvent(playerQuitEvent);

        entityplayer.lastActivity = System.currentTimeMillis();

        entityplayer.getBukkitEntity().disconnect(playerQuitEvent.getQuitMessage());
        // CraftBukkit end

        this.savePlayerFile(entityplayer);
        WorldServer worldserver = entityplayer.u();

        if (entityplayer.vehicle != null && !(entityplayer.vehicle instanceof EntityPlayer)) { // CraftBukkit - Don't remove players
            worldserver.removeEntity(entityplayer.vehicle);
        }

        worldserver.kill(entityplayer);
        worldserver.getPlayerChunkMap().removePlayer(entityplayer);
        this.players.remove(entityplayer);
        this.playersByName.remove(entityplayer.getName()); // Spigot
        UUID uuid = entityplayer.getUniqueID();
        EntityPlayer entityplayer1 = this.j.get(uuid);
        if (entityplayer1 == entityplayer) {
            this.j.remove(uuid);
            this.o.remove(uuid);
        }

        CraftingManager craftingManager = CraftingManager.getInstance();
        CraftInventoryView lastView = (CraftInventoryView) craftingManager.lastCraftView;
        if (lastView != null && lastView.getHandle() instanceof ContainerPlayer && lastView.getPlayer() == bukkit)
            craftingManager.lastCraftView = null;

        // CraftBukkit start
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityplayer);
        for (EntityPlayer player : players) {
            player.playerConnection.sendPacket(packet);
        }
        // This removes the scoreboard (and player reference) for the specific player in
        // the manager
        cserver.getScoreboardManager().removePlayer(entityplayer.getBukkitEntity());
        // CraftBukkit end

        ChunkIOExecutor.adjustPoolSize(this.getPlayerCount()); // CraftBukkit

        return playerQuitEvent.getQuitMessage(); // CraftBukkit
    }

    public EntityPlayer processLogin(GameProfile gameprofile, EntityPlayer player) { // CraftBukkit - added EntityPlayer
        return player;
        // CraftBukkit end
    }

    public EntityPlayer attemptLogin(LoginListener loginlistener, GameProfile gameprofile, String hostname) {
        UUID uuid = EntityHuman.a(gameprofile);
        ArrayList<EntityPlayer> arraylist = Lists.newArrayList();
        EntityPlayer entityplayer;
        for (EntityPlayer player : this.players) {
            entityplayer = player;
            if (entityplayer.getUniqueID().equals(uuid))
                arraylist.add(entityplayer);
        }
        for (EntityPlayer entityPlayer : arraylist) {
            entityplayer = entityPlayer;
            savePlayerFile(entityplayer);
            entityplayer.playerConnection.disconnect("You logged in from another location");
        }
        SocketAddress socketaddress = loginlistener.networkManager.getSocketAddress();
        EntityPlayer entity = new EntityPlayer(this.server, this.server.getWorldServer(0), gameprofile, new PlayerInteractManager(this.server.getWorldServer(0)));
        CraftPlayer craftPlayer = entity.getBukkitEntity();
        PlayerLoginEvent event = new PlayerLoginEvent(craftPlayer, hostname, ((InetSocketAddress) socketaddress).getAddress(), ((InetSocketAddress) loginlistener.networkManager.getRawAddress()).getAddress());
        if (getProfileBans().isBanned(gameprofile) && !getProfileBans().get(gameprofile).hasExpired()) {
            GameProfileBanEntry gameprofilebanentry = this.k.get(gameprofile);
            String s = "You are banned from this server!\nReason: " + gameprofilebanentry.getReason();
            if (gameprofilebanentry.getExpires() != null)
                s = s + "\nYour ban will be removed on " + g.format(gameprofilebanentry.getExpires());
            if (!gameprofilebanentry.hasExpired())
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, s);
        } else if (!isWhitelisted(gameprofile)) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, SpigotConfig.whitelistMessage);
        } else if (getIPBans().isBanned(socketaddress) && !getIPBans().get(socketaddress).hasExpired()) {
            IpBanEntry ipbanentry = this.l.get(socketaddress);
            String s = "Your IP address is banned from this server!\nReason: " + ipbanentry.getReason();
            if (ipbanentry.getExpires() != null)
                s = s + "\nYour ban will be removed on " + g.format(ipbanentry.getExpires());
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, s);
        } else if (this.players.size() >= this.maxPlayers && !f(gameprofile)) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, SpigotConfig.serverFullMessage);
        }
        this.cserver.getPluginManager().callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            loginlistener.disconnect(event.getKickMessage());
            return null;
        }
        return entity;
    }

    // CraftBukkit start
    public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i, boolean flag) {
        return this.moveToWorld(entityplayer, i, flag, null, true);
    }

    public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i, boolean flag, Location location,
                                    boolean avoidSuffocation) {
        entityplayer.u().getTracker().untrackPlayer(entityplayer);

        entityplayer.u().getPlayerChunkMap().removePlayer(entityplayer);
        this.players.remove(entityplayer);
        this.playersByName.remove(entityplayer.getName()); // Spigot
        this.server.getWorldServer(entityplayer.dimension).removeEntity(entityplayer);
        BlockPosition blockposition = entityplayer.getBed();
        boolean flag1 = entityplayer.isRespawnForced();
        org.bukkit.World fromWorld = entityplayer.getBukkitEntity().getWorld();
        entityplayer.viewingCredits = false;

        entityplayer.copyTo(entityplayer, flag);
        entityplayer.d(entityplayer.getId());
        entityplayer.o(entityplayer);

        BlockPosition blockposition1;

        // CraftBukkit start - fire PlayerRespawnEvent
        if (location == null) {
            boolean isBedSpawn = false;
            CraftWorld cworld = (CraftWorld) this.server.server.getWorld(entityplayer.spawnWorld);
            if (cworld != null && blockposition != null) {
                blockposition1 = EntityHuman.getBed(cworld.getHandle(), blockposition, flag1);
                if (blockposition1 != null) {
                    isBedSpawn = true;
                    location = new Location(cworld, blockposition1.getX() + 0.5, blockposition1.getY(),
                            blockposition1.getZ() + 0.5);
                } else {
                    entityplayer.setRespawnPosition(null, true);
                    entityplayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(0, 0.0F));
                }
            }

            if (location == null) {
                cworld = (CraftWorld) this.server.server.getWorlds().get(0);
                blockposition = cworld.getHandle().getSpawn();
                location = new Location(cworld, blockposition.getX() + 0.5, blockposition.getY(),
                        blockposition.getZ() + 0.5);
            }

            Player respawnPlayer = cserver.getPlayer(entityplayer);
            PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn);
            cserver.getPluginManager().callEvent(respawnEvent);
            // Spigot Start
            if (entityplayer.playerConnection.isDisconnected()) {
                return entityplayer;
            }
            // Spigot End

            location = respawnEvent.getRespawnLocation();
            entityplayer.reset();
        } else {
            location.setWorld(server.getWorldServer(i).getWorld());
        }
        WorldServer worldserver = ((CraftWorld) location.getWorld()).getHandle();
        entityplayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());
        // CraftBukkit end

        worldserver.chunkProviderServer.getChunkAt((int) entityplayer.locX >> 4, (int) entityplayer.locZ >> 4);

        while (avoidSuffocation && !worldserver.getCubes(entityplayer, entityplayer.getBoundingBox()).isEmpty()
                && entityplayer.locY < 256.0D) {
            entityplayer.setPosition(entityplayer.locX, entityplayer.locY + 1.0D, entityplayer.locZ);
        }
        // CraftBukkit start
        byte actualDimension = (byte) (worldserver.getWorld().getEnvironment().getId());
        // Force the client to refresh their chunk cache
        if (fromWorld.getEnvironment() == worldserver.getWorld().getEnvironment()) {
            entityplayer.playerConnection.sendPacket(
                    new PacketPlayOutRespawn((byte) (actualDimension >= 0 ? -1 : 0), worldserver.getDifficulty(),
                            worldserver.getWorldData().getType(), entityplayer.playerInteractManager.getGameMode()));
        }
        entityplayer.playerConnection.sendPacket(new PacketPlayOutRespawn(actualDimension, worldserver.getDifficulty(),
                worldserver.getWorldData().getType(), entityplayer.playerInteractManager.getGameMode()));
        entityplayer.spawnIn(worldserver);
        entityplayer.dead = false;
        entityplayer.playerConnection.teleport(new Location(worldserver.getWorld(), entityplayer.locX,
                entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch));
        entityplayer.setSneaking(false);
        blockposition1 = worldserver.getSpawn();
        entityplayer.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(blockposition1));
        entityplayer.playerConnection.sendPacket(
                new PacketPlayOutExperience(entityplayer.exp, entityplayer.expTotal, entityplayer.expLevel));
        this.b(entityplayer, worldserver);

        if (!entityplayer.playerConnection.isDisconnected()) {
            worldserver.getPlayerChunkMap().addPlayer(entityplayer);
            worldserver.addEntity(entityplayer);
            this.players.add(entityplayer);
            this.playersByName.put(entityplayer.getName(), entityplayer); // Spigot
            this.j.put(entityplayer.getUniqueID(), entityplayer);
        }
        // Added from changeDimension
        updateClient(entityplayer); // Update health, etc...
        entityplayer.updateAbilities();
        for (MobEffect o1 : entityplayer.getEffects()) {
            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), o1));
        }
        // entityplayer1.syncInventory();
        // CraftBukkit end
        entityplayer.setHealth(entityplayer.getHealth());

        // CraftBukkit start
        // Don't fire on respawn
        if (fromWorld != location.getWorld()) {
            server.server.getPluginManager()
                    .callEvent(new PlayerChangedWorldEvent(entityplayer.getBukkitEntity(), fromWorld));
        }

        // Save player file again if they were disconnected
        if (entityplayer.playerConnection.isDisconnected()) {
            this.savePlayerFile(entityplayer);
        }

        // CraftBukkit end
        return entityplayer;
    }

    // CraftBukkit start - Replaced the standard handling of portals with a more
    // customised method.
    public void changeDimension(EntityPlayer entityplayer, int i, TeleportCause cause) {
        WorldServer exitWorld = null;
        if (entityplayer.dimension < CraftWorld.CUSTOM_DIMENSION_OFFSET) { // plugins must specify exit from custom
            // Bukkit worlds
            // only target existing worlds (compensate for allow-nether/allow-end as false)
            for (WorldServer world : this.server.worlds) {
                if (world.dimension == i) {
                    exitWorld = world;
                }
            }
        }

        Location enter = entityplayer.getBukkitEntity().getLocation();
        Location exit = null;
        boolean useTravelAgent = false; // don't use agent for custom worlds or return from THE_END
        if (exitWorld != null) {
            if ((cause == TeleportCause.END_PORTAL) && (i == 0)) {
                // THE_END -> NORMAL; use bed if available, otherwise default spawn
                exit = entityplayer.getBukkitEntity()
                        .getBedSpawnLocation();
                if (exit == null || ((CraftWorld) exit.getWorld()).getHandle().dimension != 0) {
                    exit = exitWorld.getWorld().getSpawnLocation();
                }
            } else {
                // NORMAL <-> NETHER or NORMAL -> THE_END
                exit = this.calculateTarget(enter, exitWorld);
                useTravelAgent = true;
            }
        }

        TravelAgent agent = exit != null ? (TravelAgent) ((CraftWorld) exit.getWorld()).getHandle().getTravelAgent()
                : org.bukkit.craftbukkit.CraftTravelAgent.DEFAULT; // return arbitrary TA to compensate for
        // implementation dependent plugins
        agent.setCanCreatePortal(cause != TeleportCause.END_PORTAL); // PaperSpigot - Configurable end credits, don't
        // allow End Portals to create portals

        PlayerPortalEvent event = new PlayerPortalEvent(entityplayer.getBukkitEntity(), enter, exit, agent, cause);
        event.useTravelAgent(useTravelAgent);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null) {
            return;
        }

        // PaperSpigot - Configurable end credits, if a plugin sets to use a travel
        // agent even if the cause is an end portal, ignore it
        exit = cause != TeleportCause.END_PORTAL && event.useTravelAgent()
                ? event.getPortalTravelAgent().findOrCreate(event.getTo())
                : event.getTo();
        if (exit == null) {
            return;
        }
        exitWorld = ((CraftWorld) exit.getWorld()).getHandle();

        org.bukkit.event.player.PlayerTeleportEvent tpEvent = new org.bukkit.event.player.PlayerTeleportEvent(
                entityplayer.getBukkitEntity(), enter, exit, cause);
        Bukkit.getServer().getPluginManager().callEvent(tpEvent);
        if (tpEvent.isCancelled() || tpEvent.getTo() == null) {
            return;
        }

        Vector velocity = entityplayer.getBukkitEntity().getVelocity();
        boolean before = exitWorld.chunkProviderServer.forceChunkLoad;
        exitWorld.chunkProviderServer.forceChunkLoad = true;
        exitWorld.getTravelAgent().adjustExit(entityplayer, exit, velocity);
        exitWorld.chunkProviderServer.forceChunkLoad = before;

        this.moveToWorld(entityplayer, exitWorld.dimension, true, exit, false); // Vanilla doesn't check for suffocation
        // when handling portals, so neither
        // should we
        if (entityplayer.motX != velocity.getX() || entityplayer.motY != velocity.getY()
                || entityplayer.motZ != velocity.getZ()) {
            entityplayer.getBukkitEntity().setVelocity(velocity);
        }
    }

    // Copy of original changeWorld(Entity, int, WorldServer, WorldServer) method
    // with only location calculation logic
    public Location calculateTarget(Location enter, World target) {
        WorldServer worldserver = ((CraftWorld) enter.getWorld()).getHandle();
        WorldServer worldserver1 = target.getWorld().getHandle();
        int i = worldserver.dimension;

        double y = enter.getY();
        float yaw = enter.getYaw();
        float pitch = enter.getPitch();
        double d0 = enter.getX();
        double d1 = enter.getZ();
        double d2 = 8.0D;
        /*
         * double d0 = entity.locX; double d1 = entity.locZ; double d2 = 8.0D; float f =
         * entity.yaw;
         *
         * worldserver.methodProfiler.a("moving");
         */
        if (worldserver1.dimension == -1) {
            d0 = MathHelper.a(d0 / d2, worldserver1.getWorldBorder().b() + 16.0D,
                    worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 / d2, worldserver1.getWorldBorder().c() + 16.0D,
                    worldserver1.getWorldBorder().e() - 16.0D);
            /*
             * entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch); if
             * (entity.isAlive()) { worldserver.entityJoinedWorld(entity, false); }
             */
        } else if (worldserver1.dimension == 0) {
            d0 = MathHelper.a(d0 * d2, worldserver1.getWorldBorder().b() + 16.0D,
                    worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 * d2, worldserver1.getWorldBorder().c() + 16.0D,
                    worldserver1.getWorldBorder().e() - 16.0D);
            /*
             * entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch); if
             * (entity.isAlive()) { worldserver.entityJoinedWorld(entity, false); }
             */
        } else {
            BlockPosition blockposition;

            if (i == 1) {
                // use default NORMAL world spawn instead of target
                worldserver1 = this.server.worlds.get(0);
                blockposition = worldserver1.getSpawn();
            } else {
                blockposition = worldserver1.getDimensionSpawn();
            }

            d0 = blockposition.getX();
            y = blockposition.getY();
            d1 = blockposition.getZ();

            /*
             * entity.setPositionRotation(d0, entity.locY, d1, 90.0F, 0.0F); if
             * (entity.isAlive()) { worldserver.entityJoinedWorld(entity, false); }
             */
        }

        // worldserver.methodProfiler.b();
        if (i != 1) {
            d0 = MathHelper.clamp((int) d0, -29999872, 29999872);
            d1 = MathHelper.clamp((int) d1, -29999872, 29999872);
            /*
             * if (entity.isAlive()) { entity.setPositionRotation(d0, entity.locY, d1,
             * entity.yaw, entity.pitch); worldserver1.getTravelAgent().a(entity, f);
             * worldserver1.addEntity(entity); worldserver1.entityJoinedWorld(entity,
             * false); }
             *
             * worldserver.methodProfiler.b();
             */
        }

        // entity.spawnIn(worldserver1);
        return new Location(worldserver1.getWorld(), d0, y, d1, yaw, pitch);
    }

    // copy of original a(Entity, int, WorldServer, WorldServer) method with only
    // entity repositioning logic
    public void repositionEntity(Entity entity, Location exit, boolean portal) {
        WorldServer worldserver = (WorldServer) entity.world;
        WorldServer worldserver1 = ((CraftWorld) exit.getWorld()).getHandle();
        int i = worldserver.dimension;

        entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
        if (entity.isAlive()) {
            worldserver.entityJoinedWorld(entity, false);
        }

        if (i != 1) {

            if (entity.isAlive()) {
                if (portal) {
                    Vector velocity = entity.getBukkitEntity().getVelocity();
                    worldserver1.getTravelAgent().adjustExit(entity, exit, velocity);
                    entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
                    if (entity.motX != velocity.getX() || entity.motY != velocity.getY()
                            || entity.motZ != velocity.getZ()) {
                        entity.getBukkitEntity().setVelocity(velocity);
                    }
                }
                worldserver1.addEntity(entity);
                worldserver1.entityJoinedWorld(entity, false);
            }

        }

        entity.spawnIn(worldserver1);
        // CraftBukkit end
    }

    public void tick() {
        if (++this.u > 600) {
            this.sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, this.players));
            this.u = 0;
        }
    }

    public void sendAll(Packet<PacketListenerPlayOut> packet) {
        for (EntityPlayer p : players) {
            p.playerConnection.sendPacket(packet);
        }
    }

    // CraftBukkit start - add a world/entity limited version
    public void sendAll(Packet<PacketListenerPlayOut> packet, EntityHuman entityhuman) {
        if (entityhuman instanceof EntityPlayer)
            for (EntityPlayer entityplayer : players) {
                if (!entityplayer.getBukkitEntity().canSee(((EntityPlayer) entityhuman).getBukkitEntity()))
                    continue;
                entityplayer.playerConnection.sendPacket(packet);
            }
        else for (EntityPlayer entityplayer : players)
            entityplayer.playerConnection.sendPacket(packet);
    }

    public void sendAll(Packet<PacketListenerPlayOut> packet, World world) {
        for (EntityPlayer p : players) {
            p.playerConnection.sendPacket(packet);
        }
    }
    // CraftBukkit end

    public void a(Packet<PacketListenerPlayOut> packet, int i) {
        for (EntityPlayer p : players) {
            if (p.dimension == i) {
                p.playerConnection.sendPacket(packet);
            }
        }
    }

    public void a(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.getScoreboardTeam();

        if (scoreboardteambase != null) {
            for (String s : scoreboardteambase.getPlayerNameSet()) {
                EntityPlayer entityplayer = this.getPlayer(s);

                if (entityplayer != null && entityplayer != entityhuman) {
                    entityplayer.sendMessage(ichatbasecomponent);
                }
            }

        }
    }

    public void b(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.getScoreboardTeam();

        if (scoreboardteambase == null) {
            this.sendMessage(ichatbasecomponent);
        } else {
            for (EntityPlayer p : players) {
                if (p.getScoreboardTeam() != scoreboardteambase) {
                    p.sendMessage(ichatbasecomponent);
                }
            }

        }
    }

    public String b(boolean flag) {
        StringBuilder s = new StringBuilder();
        List<EntityPlayer> arraylist = Lists.newArrayList(this.players);

        for (int i = 0; i < arraylist.size(); ++i) {
            if (i > 0) {
                s.append(", ");
            }

            s.append(arraylist.get(i).getName());
            if (flag) {
                s.append(" (").append(arraylist.get(i).getUniqueID().toString()).append(")");
            }
        }

        return s.toString();
    }

    public String[] f() {
        String[] astring = new String[this.players.size()];

        for (int i = 0; i < this.players.size(); ++i) {
            astring[i] = players.get(i).getName();
        }

        return astring;
    }

    public GameProfile[] g() {
        GameProfile[] agameprofile = new GameProfile[this.players.size()];

        for (int i = 0; i < this.players.size(); ++i) {
            agameprofile[i] = players.get(i).getProfile();
        }

        return agameprofile;
    }

    public GameProfileBanList getProfileBans() {
        return this.k;
    }

    public IpBanList getIPBans() {
        return this.l;
    }

    public void addOp(GameProfile gameprofile) {
        this.operators.add(new OpListEntry(gameprofile, this.server.p(), this.operators.b(gameprofile)));
        Player player = this.server.server.getPlayer(gameprofile.getId());
        if (player != null)
            player.recalculatePermissions();
    }

    public void removeOp(GameProfile gameprofile) {
        this.operators.remove(gameprofile);
        Player player = this.server.server.getPlayer(gameprofile.getId());
        if (player != null)
            player.recalculatePermissions();
    }

    public boolean isWhitelisted(GameProfile gameprofile) {
        return !this.hasWhitelist || this.operators.d(gameprofile) || this.whitelist.d(gameprofile);
    }

    public boolean isOp(GameProfile gameprofile) {
        return this.operators.d(gameprofile);
    }

    public EntityPlayer getPlayer(String s) {
        return this.playersByName.get(s); // Spigot
    }

    public void sendPacketNearby(double d0, double d1, double d2, double d3, int i, Packet packet) {
        sendPacketNearby(null, d0, d1, d2, d3, i, packet);
    }

    public void sendPacketNearby(EntityHuman entityhuman, double d0, double d1, double d2, double d3, int i, Packet packet) {
        double squared = d3 * d3;
        for (EntityPlayer entityplayer : this.players) {
            if (!(entityhuman instanceof EntityPlayer) || entityplayer.getBukkitEntity().canSee(((EntityPlayer) entityhuman).getBukkitEntity()))
                if (entityplayer != entityhuman && entityplayer.dimension == i) {
                    double d4 = d0 - entityplayer.locX;
                    double d5 = d1 - entityplayer.locY;
                    double d6 = d2 - entityplayer.locZ;
                    if (d4 * d4 + d5 * d5 + d6 * d6 < squared)
                        entityplayer.playerConnection.sendPacket(packet);
                }
        }
    }

    public void sendPacketNearbyIncludingSelf(EntityHuman entityhuman, double d0, double d1, double d2, double d3, int i, Packet packet) {
        double squared = d3 * d3;
        for (EntityPlayer entityplayer : this.players) {
            if (entityhuman == null || entityplayer.getBukkitEntity().canSeeEntity(entityhuman.getBukkitEntity()))
                if (entityplayer.dimension == i) {
                    double d4 = d0 - entityplayer.locX;
                    double d5 = d1 - entityplayer.locY;
                    double d6 = d2 - entityplayer.locZ;
                    if (d4 * d4 + d5 * d5 + d6 * d6 < squared)
                        entityplayer.playerConnection.sendPacket(packet);
                }
        }
    }

    public void savePlayers() {
        for (EntityPlayer p : players) {
            this.savePlayerFile(p);
        }
    }

    public void addWhitelist(GameProfile gameprofile) {
        this.whitelist.add(new WhiteListEntry(gameprofile));
    }

    public void removeWhitelist(GameProfile gameprofile) {
        this.whitelist.remove(gameprofile);
    }

    public WhiteList getWhitelist() {
        return this.whitelist;
    }

    public String[] getWhitelisted() {
        return this.whitelist.getEntries();
    }

    public OpList getOPs() {
        return this.operators;
    }

    public String[] n() {
        return this.operators.getEntries();
    }

    public void reloadWhitelist() {
    }

    public void b(EntityPlayer entityplayer, WorldServer worldserver) {
        WorldBorder worldborder = entityplayer.world.getWorldBorder(); // CraftBukkit

        entityplayer.playerConnection.sendPacket(
                new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
        entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateTime(worldserver.getTime(),
                worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")));
        if (worldserver.S()) {
            // CraftBukkit start - handle player weather
            entityplayer.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL, false);
            entityplayer.updateWeather(-worldserver.p, worldserver.p, -worldserver.r, worldserver.r);
            // CraftBukkit end
        }

    }

    public void updateClient(EntityPlayer entityplayer) {
        entityplayer.updateInventory(entityplayer.defaultContainer);
        // entityplayer.triggerHealthUpdate();
        entityplayer.getBukkitEntity().updateScaledHealth(); // CraftBukkit - Update scaled health on respawn and worldchange
        entityplayer.playerConnection
                .sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.inventory.itemInHandIndex));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String[] getSeenPlayers() {
        return this.server.worlds.get(0).getDataManager().getPlayerFileData().getSeenPlayers(); // CraftBukkit
    }

    public boolean getHasWhitelist() {
        return this.hasWhitelist;
    }

    public void setHasWhitelist(boolean flag) {
        this.hasWhitelist = flag;
    }

    public List<EntityPlayer> b(String s) {
        ArrayList<EntityPlayer> arraylist = Lists.newArrayList();

        for (EntityPlayer entityplayer : this.players) {
            if (entityplayer.w().equals(s)) {
                arraylist.add(entityplayer);
            }
        }

        return arraylist;
    }

    public int s() {
        return this.r;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public NBTTagCompound t() {
        return null;
    }

    private void a(EntityPlayer entityplayer, EntityPlayer entityplayer1, World world) {
        if (entityplayer1 != null) {
            entityplayer.playerInteractManager.setGameMode(entityplayer1.playerInteractManager.getGameMode());
        }

        entityplayer.playerInteractManager.b(world.getWorldData().getGameType());
    }

    public void u() {
        for (EntityPlayer p : players) {
            p.playerConnection.disconnect(this.server.server.getShutdownMessage()); // CraftBukkit - add custom shutdown message
        }

    }

    public void sendMessage(IChatBaseComponent[] iChatBaseComponents) {
        for (IChatBaseComponent component : iChatBaseComponents) {
            sendMessage(component, true);
        }
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent, boolean flag) {
        this.server.sendMessage(ichatbasecomponent);
        this.sendAll(new PacketPlayOutChat(CraftChatMessage.fixComponent(ichatbasecomponent), (byte) (flag ? 1 : 0)));
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.sendMessage(ichatbasecomponent, true);
    }

    public ServerStatisticManager a(EntityHuman entityhuman) {
        UUID uuid = entityhuman.getUniqueID();
        ServerStatisticManager serverstatisticmanager = uuid == null ? null : this.o.get(uuid);

        if (serverstatisticmanager == null) {
            File file = new File(this.server.getWorldServer(0).getDataManager().getDirectory(), "stats");
            File file1 = new File(file, FastUUID.toString(uuid) + ".json");

            if (!file1.exists()) {
                File file2 = new File(file, entityhuman.getName() + ".json");

                if (file2.exists() && file2.isFile()) {
                    file2.renameTo(file1);
                }
            }

            serverstatisticmanager = new ServerStatisticManager(this.server, file1);
            serverstatisticmanager.a();
            this.o.put(uuid, serverstatisticmanager);
        }

        return serverstatisticmanager;
    }

    public void a(int i) {
        this.r = i;
        if (this.server.worldServer != null) {
            // CraftBukkit start
            for (int k = 0; k < server.worlds.size(); ++k) {
                WorldServer worldserver = server.worlds.get(0);
                // CraftBukkit end

                if (worldserver != null) {
                    worldserver.getPlayerChunkMap().a(i);
                }
            }

        }
    }

    public List<EntityPlayer> v() {
        return this.players;
    }

    public EntityPlayer a(UUID uuid) {
        return this.j.get(uuid);
    }

    public boolean f(GameProfile gameprofile) {
        return false;
    }
}
