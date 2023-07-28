package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import eu.vortexdev.api.protocol.MovementListenerAdapter;
import eu.vortexdev.api.protocol.PacketListenerAdapter;
import eu.vortexdev.invictusspigot.InvictusSpigot;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.GenericFutureListener;
import net.jafama.FastMath;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.LazyPlayerSet;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.NumberConversions;
import org.github.paperspigot.PaperSpigotConfig;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class PlayerConnection implements PacketListenerPlayIn, IUpdatePlayerListBox {

    private static final Logger c = LogManager.getLogger();
    private static final AtomicIntegerFieldUpdater<PlayerConnection> chatSpamField = AtomicIntegerFieldUpdater.newUpdater(PlayerConnection.class, "chatThrottle");
    private static final int SURVIVAL_PLACE_DISTANCE_SQUARED = 36;
    private static final int CREATIVE_PLACE_DISTANCE_SQUARED = 49;
    private final static List<Integer> invalidItems = Arrays.asList(8, 9, 10, 11, 26, 34, 36, 43, 51, 52, 55, 59, 60, 62, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 125, 127, 132, 140, 141, 142, 144); // Vortex - Use arraylist
    public final NetworkManager networkManager;
    protected final org.bukkit.craftbukkit.CraftServer server;
    private final MinecraftServer minecraftServer;
    private final Map<Integer, Short> n = new HashMap<>(); // Vortex
    public EntityPlayer player;
    public boolean processedDisconnect; // CraftBukkit - added
    private int e;
    private int f;
    private int g;
    private int i;
    private long j;
    private long k;
    // CraftBukkit start - multithreaded fields
    private volatile int chatThrottle;
    // CraftBukkit end
    private int m;
    private double o;
    private double p;
    private double q;
    private boolean checkMovement = true;
    private int lastDropTick = MinecraftServer.currentTick;
    private int dropCount = 0;
    // Get position of last block hit for BlockDamageLevel.STOPPED
    private double lastPosX = Double.MAX_VALUE;
    private double lastPosY = Double.MAX_VALUE;
    private double lastPosZ = Double.MAX_VALUE;
    private float lastPitch = Float.MAX_VALUE;
    private float lastYaw = Float.MAX_VALUE;

    private boolean justTeleported;
    private boolean hasMoved; // Spigot
    // Spigot start - limit place/interactions
    private long lastPlace = -1;
    private int packets = 0;

    public PlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        networkmanager.a(this);
        this.minecraftServer = minecraftserver;
        this.networkManager = networkmanager;
        this.player = entityplayer;
        entityplayer.playerConnection = this;

        // CraftBukkit start - add fields and methods
        this.server = minecraftserver.server;
    }

    public CraftPlayer getPlayer() {
        return player.getBukkitEntity();
    }

    public void c() {
        if (++e - k > 40L) {
            k = e;
            j = d();
            i = (int) j;
            sendPacket(new PacketPlayOutKeepAlive(i));
        }

        for (int spam; (spam = chatThrottle) > 0 && !chatSpamField.compareAndSet(this, spam, spam - 1); ) ;

        if (m > 0) {
            --m;
        }

        if (InvictusConfig.idleTimer && player.D() > 0L && minecraftServer.getIdleTimeout() > 0 && MinecraftServer.az() - player.D() > (minecraftServer.getIdleTimeout() * 1000L * 60L)) {
            player.resetIdleTimer();
            disconnect("You have been idle for too long!");
        }

    }

    public NetworkManager a() {
        return this.networkManager;
    }

    public void disconnect(String s) {

        PlayerKickEvent event = new PlayerKickEvent(this.player.getBukkitEntity().getPlayer(), s, EnumChatFormat.YELLOW + this.player.getName() + " left the game.");

        if (this.server.getServer().isRunning()) {
            this.server.getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            return;
        }

        this.networkManager.k(); // Vortex - Stop reading packets firstly

        ChatComponentText chatcomponenttext = new ChatComponentText(event.getReason());
        this.networkManager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), (GenericFutureListener) future -> { // CraftBukkit - fix decompile error
            networkManager.close(chatcomponenttext);
        });

        this.a(chatcomponenttext);
        this.minecraftServer.postToMainThread(networkManager::l);
    }

    public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
        if (packetplayinsteervehicle.a() >= Float.MAX_VALUE || packetplayinsteervehicle.b() >= Float.MAX_VALUE) // Vortex - Anticrash
            this.disconnect("Invalid vehicle move packet received");
        PlayerConnectionUtils.ensureMainThread(packetplayinsteervehicle, this, this.player.u());
        this.player.a(packetplayinsteervehicle.a(), packetplayinsteervehicle.b(), packetplayinsteervehicle.c(), packetplayinsteervehicle.d());
    }

    private boolean b(PacketPlayInFlying packetplayinflying) {
        return !Doubles.isFinite(packetplayinflying.a()) || !Doubles.isFinite(packetplayinflying.b()) || !Doubles.isFinite(packetplayinflying.c()) || !Floats.isFinite(packetplayinflying.e()) || !Floats.isFinite(packetplayinflying.d());
    }

    public void a(PacketPlayInFlying packetplayinflying) {
        PlayerConnectionUtils.ensureMainThread(packetplayinflying, this, player.u());
        if (b(packetplayinflying)) {
            disconnect("Invalid move packet received");
        } else {
            if (!player.viewingCredits) {
                double diffY = packetplayinflying.b() - p;
                double diff = 0.0D;
                if (packetplayinflying.g()) {
                    double diffX = packetplayinflying.a() - o, diffZ = packetplayinflying.c() - q;
                    diff = diffX * diffX + diffY * diffY + diffZ * diffZ;
                    if (!checkMovement && diff < 0.25D)
                        checkMovement = true;
                }

                Player craftPlayer = getPlayer();

                if (!hasMoved) {
                    Location curPos = craftPlayer.getLocation();
                    lastPosX = curPos.getX();
                    lastPosY = curPos.getY();
                    lastPosZ = curPos.getZ();
                    lastYaw = curPos.getYaw();
                    lastPitch = curPos.getPitch();
                    hasMoved = true;
                }

                Location from = new Location(craftPlayer.getWorld(), lastPosX, lastPosY, lastPosZ, lastYaw, lastPitch); // Get the Players previous Event location.
                Location to = craftPlayer.getLocation(); // We don't actually have to clone the object. Waste of cpu.

                // If the packet contains movement information then we update the To location
                // with the correct XYZ.
                if (packetplayinflying.hasPos && packetplayinflying.y != -999.0D) {
                    to.setX(packetplayinflying.x);
                    to.setY(packetplayinflying.y);
                    to.setZ(packetplayinflying.z);
                }

                // If the packet contains look information then we update the To location with
                // the correct Yaw & Pitch.
                if (packetplayinflying.hasLook) {
                    to.setYaw(packetplayinflying.yaw);
                    to.setPitch(packetplayinflying.pitch);
                }

                double delta = FastMath.pow(lastPosX - to.getX(), 2.0D) + FastMath.pow(lastPosY - to.getY(), 2.0D) + FastMath.pow(lastPosZ - to.getZ(), 2.0D);
                float deltaAngle = Math.abs(lastYaw - to.getYaw()) + Math.abs(lastPitch - to.getPitch());

                // Prevent 40 event-calls for less than a single pixel of movement.
                AxisAlignedBB axisalignedbb = player.boundingBox.grow(0.0625, 0.0625, 0.0625).a(0.0D, -0.55D, 0.0D);
                if (checkMovement && !player.dead) {
                    boolean hasLook = packetplayinflying.hasLook && deltaAngle > 0.0F, hasPos = packetplayinflying.hasPos && delta > 0.0F;                    for (MovementListenerAdapter adapter : InvictusSpigot.INSTANCE.getMovementListeners()) {
                        if (hasPos) {
                            adapter.onUpdateLocation(craftPlayer, from, to, packetplayinflying);
                            if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ())
                                adapter.onUpdateBlockLocation(craftPlayer, from, to);
                        }
                        if (hasLook) {
                            adapter.onUpdateRotation(craftPlayer, from, to, packetplayinflying);
                        }
                    }
                    if ((delta > 0.00390625D || deltaAngle > 10.0F)) {
                        lastPosX = to.getX();
                        lastPosY = to.getY();
                        lastPosZ = to.getZ();
                        lastYaw = to.getYaw();
                        lastPitch = to.getPitch();
                        if (InvictusConfig.playerMoveEvent) {
                            PlayerMoveEvent event = new PlayerMoveEvent(craftPlayer, from, to);
                            server.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                player.playerConnection.sendPacket(new PacketPlayOutPosition(from.getX(), from.getY() + 1.6200000047683716D, from.getZ(), from.getYaw(), from.getPitch(), Collections.emptySet()));
                                return;
                            } else if (!to.equals(event.getTo()) && !event.isCancelled()) {
                                player.getBukkitEntity().teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                                return;
                            }
                        }
                        if (!from.equals(getPlayer().getLocation()) && justTeleported) {
                            justTeleported = false;
                            return;
                        }
                    }

                    f = e;
                    double locX;
                    double locY;
                    double locZ;
                    float yaw;
                    float pitch;

                    Entity entity = player.vehicle;

                    if (entity != null) {
                        yaw = player.yaw;
                        pitch = player.pitch;
                        entity.al();
                        locX = player.locX;
                        locY = player.locY;
                        locZ = player.locZ;
                        if (packetplayinflying.h()) {
                            yaw = packetplayinflying.d();
                            pitch = packetplayinflying.e();
                        }

                        player.onGround = packetplayinflying.f();
                        player.l();
                        player.setLocation(locX, locY, locZ, yaw, pitch);
                        entity.al();

                        this.minecraftServer.getPlayerList().d(player);
                        entity.ai = true;
                        if (diff > 4.0D) {
                            sendPacket(new PacketPlayOutEntityTeleport(entity));
                            a(player.locX, player.locY, player.locZ, player.yaw, player.pitch);
                        }

                        if (checkMovement) {
                            o = player.locX;
                            p = player.locY;
                            q = player.locZ;
                        }

                        minecraftServer.getWorldServer(player.dimension).g(player);
                        return;
                    }

                    WorldServer worldserver = minecraftServer.getWorldServer(player.dimension);
                    if (player.isSleeping()) { // Prevent player from moving while sleeping
                        player.l();
                        player.setLocation(o, p, q, player.yaw, player.pitch);
                        worldserver.g(player);
                        return;
                    }

                    diffY = player.locY;
                    o = player.locX;
                    p = player.locY;
                    q = player.locZ;
                    locX = player.locX;
                    locY = player.locY;
                    locZ = player.locZ;
                    yaw = player.yaw;
                    pitch = player.pitch;

                    if (packetplayinflying.g()) {
                        if (packetplayinflying.b() == -999.0D)
                            packetplayinflying.a(false);
                        locX = packetplayinflying.a();
                        locY = packetplayinflying.b();
                        locZ = packetplayinflying.c();
                        if (Math.abs(packetplayinflying.a()) > 3.0E7D || Math.abs(packetplayinflying.c()) > 3.0E7D) {
                            disconnect("Illegal position");
                            return;
                        }
                    }

                    if (packetplayinflying.h()) {
                        yaw = packetplayinflying.d();
                        pitch = packetplayinflying.e();
                    }

                    player.l();
                    player.setLocation(o, p, q, yaw, pitch);
                    if (!checkMovement)
                        return;

                    double deltaX = locX - player.locX;
                    double deltaY = locY - player.locY;
                    double deltaZ = locZ - player.locZ;

                    double positionOffset = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

                    if (positionOffset - (player.motX * player.motX + player.motY * player.motY + player.motZ * player.motZ) > SpigotConfig.movedTooQuicklyThreshold) {
                        c.warn(player.getName() + " moved too quickly! (" + deltaX + ", " + deltaY + ", " + deltaZ + ")");
                        a(o, p, q, player.yaw, player.pitch);
                        return;
                    }

                    boolean isNotColliding = worldserver.getCubes(player, player.boundingBox.shrink(0.0625, 0.0625, 0.0625)).isEmpty();

                    if (player.onGround && !packetplayinflying.f() && deltaY > 0.0D) {
                        player.bF();
                    }

                    player.move(deltaX, deltaY, deltaZ);
                    player.checkMovement(deltaX, deltaY, deltaZ);
                    player.onGround = packetplayinflying.f();

                    double oldYDelta = deltaY;
                    deltaX = locX - player.locX;
                    deltaY = locY - player.locY;
                    if (deltaY > -0.5D || deltaY < 0.5D) {
                        deltaY = 0.0D;
                    }
                    deltaZ = locZ - player.locZ;
                    positionOffset = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

                    boolean wrongly = false;
                    if (positionOffset > SpigotConfig.movedWronglyThreshold && !player.isSleeping() && !player.playerInteractManager.isCreative()) {
                        wrongly = true;
                        c.warn(player.getName() + " moved wrongly!");
                    }

                    player.setLocation(locX, locY, locZ, yaw, pitch);

                    if (!player.noclip) {
                        // logic: player wasn't colliding, but he moved inside of a block, so we tp him back
                        if (isNotColliding && (wrongly || !worldserver.getCubes(player, player.boundingBox.shrink(0.0625, 0.0625, 0.0625)).isEmpty()) && !this.player.isSleeping()) {
                            a(o, p, q, yaw, pitch);
                            return;
                        }
                    }

                    if (InvictusConfig.flightCheck) {
                        if (!player.abilities.canFly && !worldserver.c(axisalignedbb)) {
                            if (oldYDelta >= -0.03125D) {
                                ++g;
                                if (g > 80) {
                                    c.warn(player.getName() + " was kicked for floating too long!");
                                    disconnect("Flying is not enabled on this server");
                                    return;
                                }
                            }
                        } else {
                            g = 0;
                        }
                    }

                    player.onGround = packetplayinflying.f();
                    minecraftServer.getPlayerList().d(player); // send chunk packets
                    player.a(player.locY - diffY, packetplayinflying.f()); // fall on blocks + dust particles
                } else if (e - f > 20) {
                    a(o, p, q, player.yaw, player.pitch);
                }
            }
        }
    }

    public void a(double d0, double d1, double d2, float f, float f1) {
        this.a(d0, d1, d2, f, f1, Collections.emptySet());
    }

    public void a(double d0, double d1, double d2, float f, float f1,
                  Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
        // CraftBukkit start - Delegate to teleport(Location)
        Player player = this.getPlayer();
        Location from = player.getLocation();

        double x = d0;
        double y = d1;
        double z = d2;
        float yaw = f;
        float pitch = f1;
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X)) {
            x += from.getX();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y)) {
            y += from.getY();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z)) {
            z += from.getZ();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT)) {
            yaw += from.getYaw();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT)) {
            pitch += from.getPitch();
        }

        Location to = new Location(this.getPlayer().getWorld(), x, y, z, yaw, pitch);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from.clone(), to.clone(),
                PlayerTeleportEvent.TeleportCause.UNKNOWN);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled() || to.equals(event.getTo())) {
            set.clear(); // Can't relative teleport
            to = event.isCancelled() ? event.getFrom() : event.getTo();
            d0 = to.getX();
            d1 = to.getY();
            d2 = to.getZ();
            f = to.getYaw();
            f1 = to.getPitch();
        }

        this.internalTeleport(d0, d1, d2, f, f1, set);
    }

    public void teleport(Location dest) {
        internalTeleport(dest.getX(), dest.getY(), dest.getZ(), dest.getYaw(), dest.getPitch(), Collections.emptySet());
    }

    private void internalTeleport(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
        if (Float.isNaN(f)) {
            f = 0;
        }

        if (Float.isNaN(f1)) {
            f1 = 0;
        }
        this.justTeleported = true;
        // CraftBukkit end
        this.checkMovement = false;
        this.o = d0;
        this.p = d1;
        this.q = d2;
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X)) {
            this.o += this.player.locX;
        }

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y)) {
            this.p += this.player.locY;
        }

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z)) {
            this.q += this.player.locZ;
        }

        float f2 = f;
        float f3 = f1;

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT)) {
            f2 = f + this.player.yaw;
        }

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT)) {
            f3 = f1 + this.player.pitch;
        }

        // CraftBukkit start - update last location
        this.lastPosX = this.o;
        this.lastPosY = this.p;
        this.lastPosZ = this.q;
        this.lastYaw = f2;
        this.lastPitch = f3;
        // CraftBukkit end

        this.player.setLocation(this.o, this.p, this.q, f2, f3);
        this.player.playerConnection.sendPacket(new PacketPlayOutPosition(d0, d1, d2, f, f1, set));
    }

    public void a(PacketPlayInBlockDig packetplayinblockdig) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockdig, this, this.player.u());
        if (this.player.dead)
            return; // CraftBukkit
        this.player.resetIdleTimer();
        // CraftBukkit start
        switch (SyntheticClass_1.a[packetplayinblockdig.c().ordinal()]) {
            case 1: // DROP_ITEM
                if (!this.player.isSpectator()) {
                    // limit how quickly items can be dropped
                    // If the ticks aren't the same then the count starts from 0 and we update the
                    // lastDropTick.
                    if (this.lastDropTick != MinecraftServer.currentTick) {
                        this.dropCount = 0;
                        this.lastDropTick = MinecraftServer.currentTick;
                    } else {
                        // Else we increment the drop count and check the amount.
                        this.dropCount++;
                        if (this.dropCount >= 20) {
                            c.warn(this.player.getName() + " dropped their items too quickly!");
                            this.disconnect("You dropped your items too quickly (Hacking?)");
                            return;
                        }
                    }
                    // CraftBRukkit end
                    this.player.a(false);
                    if (InvictusConfig.patchDropAndEat && this.player.isEating()) {
                        this.player.bU(); // stopUsingItem | invictusspigot - Fix for eating and blocking and etc while // running
                    }
                }

                return;

            case 2: // DROP_ALL_ITEMS
                if (!this.player.isSpectator()) {
                    this.player.a(true);
                }

                return;

            case 3: // RELEASE_USE_ITEM
                this.player.bU();
                return;

            case 4: // START_DESTROY_BLOCK
            case 5: // ABORT_DESTROY_BLOCK
            case 6: // STOP_DESTROY_BLOCK
                BlockPosition blockposition = packetplayinblockdig.a();

                double d0 = this.player.locX - (blockposition.getX() + 0.5D);
                double d1 = this.player.locY - (blockposition.getY() + 0.5D) + 1.5D;
                double d2 = this.player.locZ - (blockposition.getZ() + 0.5D);
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > 36.0D) {
                    return;
                } else if (blockposition.getY() >= this.minecraftServer.getMaxBuildHeight()) {
                    return;
                } else {
                    WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
                    Chunk ch = worldserver.getChunkAtWorldCoords(blockposition);
                    IBlockData iblockdata = worldserver.getType(ch, blockposition.getX(), blockposition.getY(), blockposition.getZ(), true);

                    if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                        if (!this.minecraftServer.a(worldserver, blockposition, this.player) && worldserver.getWorldBorder().a(blockposition)) {
                            this.player.playerInteractManager.a(ch, blockposition, packetplayinblockdig.b());
                        } else {
                            CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, ch.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ()), blockposition, packetplayinblockdig.b(), this.player.inventory.getItemInHand(), false);
                            sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));

                            TileEntity tileentity = worldserver.getTileEntity(ch, blockposition);
                            if (tileentity != null) {
                                sendPacket(tileentity.getUpdatePacket());
                            }

                        }
                    } else {
                        if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                            this.player.playerInteractManager.a(ch, iblockdata, blockposition);
                        } else if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                            this.player.playerInteractManager.e();
                        }
                        if (iblockdata.getBlock().getMaterial() != Material.AIR) {
                            sendPacket(new PacketPlayOutBlockChange(blockposition, iblockdata));
                        }
                    }

                    return;
                }

            default:
                throw new IllegalArgumentException("Invalid player action");
        }
        // CraftBukkit end
    }

    public void a(PacketPlayInBlockPlace packetplayinblockplace) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockplace, this, this.player.u());
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        boolean throttled = false;
        // PaperSpigot - Allow disabling the player interaction limiter
        if (PaperSpigotConfig.interactLimitEnabled && lastPlace != -1
                && packetplayinblockplace.timestamp - lastPlace < 30 && packets++ >= 4) {
            throttled = true;
        } else if (packetplayinblockplace.timestamp - lastPlace >= 30 || lastPlace == -1) {
            lastPlace = InvictusConfig.optimizePrinter ? -1L : packetplayinblockplace.timestamp;
            packets = 0;
        }
        // Spigot end

        // CraftBukkit start
        if (this.player.dead)
            return;
        // CraftBukkit - if rightclick decremented the item, always send the update
        // packet. */
        // this is not here for CraftBukkit's own functionality; rather it is to fix
        // a notch bug where the item doesn't update correctly.
        boolean always = false;
        // CraftBukkit end

        ItemStack itemstack = this.player.inventory.getItemInHand();
        boolean flag = false;
        BlockPosition blockposition = packetplayinblockplace.a();
        EnumDirection enumdirection = EnumDirection.fromType1(packetplayinblockplace.getFace());

        this.player.resetIdleTimer();
        if (packetplayinblockplace.getFace() == 255) {
            if (itemstack == null) {
                return;
            }

            // CraftBukkit start
            int itemstackAmount = itemstack.count;
            // Spigot start - skip the event if throttled
            if (!throttled) {
                // Raytrace to look for 'rogue armswings'
                float f1 = this.player.pitch;
                float f2 = this.player.yaw;
                Vec3D vec3d = new Vec3D(player.locX, player.locY + player.getHeadHeight(), player.locZ);

                float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
                float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
                float f5 = -MathHelper.cos(-f1 * 0.017453292F);
                double d3 = player.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.CREATIVE ? 5.0D : 4.5D;
                Vec3D vec3d1 = vec3d.add(f4 * f5 * d3, MathHelper.sin(-f1 * 0.017453292F) * d3, f3 * f5 * d3);
                MovingObjectPosition movingobjectposition = this.player.world.rayTrace(vec3d, vec3d1, false);

                boolean cancelled;
                if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                    org.bukkit.event.player.PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.RIGHT_CLICK_AIR, itemstack);
                    cancelled = event.useItemInHand() == Event.Result.DENY;
                } else {
                    if (player.playerInteractManager.firedInteract) {
                        player.playerInteractManager.firedInteract = false;
                        cancelled = player.playerInteractManager.interactResult;
                    } else {
                        org.bukkit.event.player.PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, movingobjectposition.a(), movingobjectposition.direction, itemstack, true);
                        cancelled = event.useItemInHand() == Event.Result.DENY;
                    }
                }

                if (!cancelled) {
                    this.player.playerInteractManager.useItem(this.player, this.player.world, itemstack);
                } else {
                    this.player.updateInventory(this.player.activeContainer);
                }
            }
            // Spigot end

            // CraftBukkit - notch decrements the counter by 1 in the above method with food,
            // snowballs and so forth, but he does it in a place that doesn't cause the
            // inventory update packet to get sent
            always = (itemstack.count != itemstackAmount) || itemstack.getItem() == Item.getItemOf(Blocks.WATERLILY);
            // CraftBukkit end
        } else if (blockposition.getY() >= this.minecraftServer.getMaxBuildHeight() - 1 && (enumdirection == EnumDirection.UP || blockposition.getY() >= this.minecraftServer.getMaxBuildHeight())) {
            ChatMessage chatmessage = new ChatMessage("build.tooHigh", this.minecraftServer.getMaxBuildHeight());

            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.player.playerConnection.sendPacket(new PacketPlayOutChat(chatmessage));
            flag = true;
        } else {
            // CraftBukkit start - Check if we can actually do something over this large a
            // distance
            Location eyeLoc = this.getPlayer().getEyeLocation();
            double reachDistance = NumberConversions.square(eyeLoc.getX() - blockposition.getX()) + NumberConversions.square(eyeLoc.getY() - blockposition.getY()) + NumberConversions.square(eyeLoc.getZ() - blockposition.getZ());
            if (reachDistance > (this.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE ? CREATIVE_PLACE_DISTANCE_SQUARED : SURVIVAL_PLACE_DISTANCE_SQUARED)) {
                return;
            }

            if (!worldserver.getWorldBorder().a(blockposition)) {
                return;
            }

            if (this.checkMovement && this.player.e(blockposition.getX() + 0.5D, blockposition.getY() + 0.5D, blockposition.getZ() + 0.5D) < 64.0D && !this.minecraftServer.a(worldserver, blockposition, this.player) && worldserver.getWorldBorder().a(blockposition)) {
                always = throttled || !this.player.playerInteractManager.interact(this.player, worldserver, itemstack, blockposition, enumdirection, packetplayinblockplace.d(), packetplayinblockplace.e(), packetplayinblockplace.f());
            }

            flag = true;
        }

        if (flag) {
            sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
            sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition.shift(enumdirection)));
        }

        itemstack = this.player.inventory.getItemInHand();
        if (itemstack != null && itemstack.count == 0) {
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = null;
            itemstack = null;
        }

        if (itemstack == null || itemstack.l() == 0) {
            this.player.g = true;
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = ItemStack
                    .b(this.player.inventory.items[this.player.inventory.itemInHandIndex]);
            Slot slot = this.player.activeContainer.getSlot(this.player.inventory,
                    this.player.inventory.itemInHandIndex);

            this.player.activeContainer.b();
            this.player.g = false;
            // CraftBukkit - TODO CHECK IF NEEDED -- new if structure might not need
            // 'always'. Kept it in for now, but may be able to remove in future

            if (!ItemStack.matches(this.player.inventory.getItemInHand(), packetplayinblockplace.getItemStack()) || always)
                sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, slot.rawSlotIndex, this.player.inventory.getItemInHand()));
        }

    }



    public void a(PacketPlayInSpectate packetplayinspectate) {
        PlayerConnectionUtils.ensureMainThread(packetplayinspectate, this, this.player.u());
        if (this.player.isSpectator()) {
            Entity entity = null;

            // CraftBukkit - use the worlds array list
            for (WorldServer worldserver : minecraftServer.worlds) {

                if (worldserver != null) {
                    entity = packetplayinspectate.a(worldserver);
                    if (entity != null) {
                        break;
                    }
                }
            }

            if (entity != null) {
                this.player.setSpectatorTarget(this.player);
                this.player.mount(null);

                this.player.getBukkitEntity().teleport(entity.getBukkitEntity(), PlayerTeleportEvent.TeleportCause.SPECTATE);

            }
        }

    }

    // CraftBukkit start
    public void a(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {
        PlayerConnectionUtils.ensureMainThread(packetplayinresourcepackstatus, this, this.player.u());
        PlayerResourcePackStatusEvent.Status status = PlayerResourcePackStatusEvent.Status
                .values()[packetplayinresourcepackstatus.b.ordinal()];

        this.server.getPluginManager()
                .callEvent(new PlayerResourcePackStatusEvent(getPlayer(), status, packetplayinresourcepackstatus.a));
    }
    // CraftBukkit end

    public void a(IChatBaseComponent ichatbasecomponent) {
        // CraftBukkit start - Rarely it would send a disconnect line twice
        if (this.processedDisconnect) {
            return;
        } else {
            this.processedDisconnect = true;
        }
        if (InvictusConfig.connectionLogs)
            PlayerConnection.c.info(this.player.getName() + " lost connection: " + ichatbasecomponent.c());
        this.player.q();
        String quitMessage = this.minecraftServer.getPlayerList().disconnect(this.player);
        if (quitMessage != null && !quitMessage.isEmpty()) {
            this.minecraftServer.getPlayerList().sendMessage(CraftChatMessage.fromString(quitMessage));
        }
    }

    public void sendPacket(final Packet<?> packet) {
        if (packet == null || this.processedDisconnect)
            return;
        if (packet instanceof PacketPlayOutChat) {
            PacketPlayOutChat packetplayoutchat = (PacketPlayOutChat) packet;
            EntityHuman.EnumChatVisibility entityhuman_enumchatvisibility = this.player.getChatFlags();
            if (entityhuman_enumchatvisibility == EntityHuman.EnumChatVisibility.HIDDEN)
                return;
            else if (entityhuman_enumchatvisibility == EntityHuman.EnumChatVisibility.SYSTEM && !packetplayoutchat.b())
                return;
        } else if (packet instanceof PacketPlayOutSpawnPosition) {
            PacketPlayOutSpawnPosition packet6 = (PacketPlayOutSpawnPosition) packet;
            this.player.compassTarget = new Location(this.getPlayer().getWorld(), packet6.position.getX(), packet6.position.getY(),
                    packet6.position.getZ());
        }
        try {
            this.networkManager.handle(packet);
            for (PacketListenerAdapter adapter : InvictusSpigot.INSTANCE.getPacketListeners()) {
                adapter.onSend(this, packet);
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Sending packet");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Packet being sent");
            crashreportsystemdetails.a("Packet class", () -> packet.getClass().getCanonicalName());
            throw new ReportedException(crashreport);
        }
    }

    public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
        // CraftBukkit start
        if (this.player.dead)
            return;
        PlayerConnectionUtils.ensureMainThread(packetplayinhelditemslot, this, this.player.u());
        int slot = packetplayinhelditemslot.a();
        if (slot >= 0 && slot < PlayerInventory.getHotbarSize()) { // Vortex - Anti crash
            PlayerItemHeldEvent event = new PlayerItemHeldEvent(this.getPlayer(), this.player.inventory.itemInHandIndex, slot);
            this.server.getPluginManager().callEvent(event);
            this.player.resetIdleTimer();
            if (event.isCancelled()) {
                this.sendPacket(new PacketPlayOutHeldItemSlot(this.player.inventory.itemInHandIndex));
                return;
            }
            // CraftBukkit end
            this.player.inventory.itemInHandIndex = slot;
        } else {
            PlayerConnection.c.warn(this.player.getName() + " tried to set an invalid carried item");
            this.disconnect("Invalid hotbar selection (Hacking?)"); // CraftBukkit //Spigot "Nope" -> Descriptive reason
        }
    }

    public void a(PacketPlayInChat packetplayinchat) {

        // CraftBukkit start - async chat
        boolean isSync = packetplayinchat.a().startsWith("/");
        if (isSync) {
            PlayerConnectionUtils.ensureMainThread(packetplayinchat, this, this.player.u());
        }
        // CraftBukkit end
        if (this.player.dead || this.player.getChatFlags() == EntityHuman.EnumChatVisibility.HIDDEN) {
            ChatMessage chatmessage = new ChatMessage("chat.cannotSend");
            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.sendPacket(new PacketPlayOutChat(chatmessage));
        } else {
            this.player.resetIdleTimer();
            String s = StringUtils.normalizeSpace(packetplayinchat.a());

            for (int i = 0; i < s.length(); ++i) {
                if (!SharedConstants.isAllowedChatCharacter(s.charAt(i))) {
                    // CraftBukkit start - threadsafety
                    if (!isSync) {
                        Waitable<?> waitable = new Waitable() {
                            @Override
                            protected Object evaluate() {
                                PlayerConnection.this.disconnect("Illegal characters in chat");
                                return null;
                            }
                        };

                        this.minecraftServer.postToMainThread(waitable);

                        try {
                            waitable.get();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        this.disconnect("Illegal characters in chat");
                    }
                    // CraftBukkit end
                    return;
                }
            }

            // CraftBukkit start
            if (isSync) {
                try {
                    this.minecraftServer.server.playerCommandState = true;
                    this.handleCommand(s);
                } finally {
                    this.minecraftServer.server.playerCommandState = false;
                }
            } else if (s.isEmpty()) {
                c.warn(this.player.getName() + " tried to send an empty message");
            } else if (getPlayer().isConversing()) {
                // Spigot start
                final String message = s;
                this.minecraftServer.postToMainThread(new Waitable<Object>() {
                    @Override
                    protected Object evaluate() {
                        getPlayer().acceptConversationInput(message);
                        return null;
                    }
                });
                // Spigot end
            } else if (this.player.getChatFlags() == EntityHuman.EnumChatVisibility.SYSTEM) { // Re-add "Command Only"
                // flag check
                ChatMessage chatmessage = new ChatMessage("chat.cannotSend");

                chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                this.sendPacket(new PacketPlayOutChat(chatmessage));
            } else {
                this.chat(s, true);
                // CraftBukkit end - the below is for reference. :)
            }

            // Spigot start - spam exclusions
            boolean counted = true;
            for (String exclude : org.spigotmc.SpigotConfig.spamExclusions) {
                if (exclude != null && s.startsWith(exclude)) {
                    counted = false;
                    break;
                }
            }
            // Spigot end
            // CraftBukkit start - replaced with thread safe throttle
            // this.chatThrottle += 20;
            if (counted && chatSpamField.addAndGet(this, 20) > 200 && !this.minecraftServer.getPlayerList().isOp(this.player.getProfile())) { // Spigot
                if (!isSync) {
                    Waitable<Object> waitable = new Waitable<Object>() {
                        @Override
                        protected Object evaluate() {
                            PlayerConnection.this.disconnect("disconnect.spam");
                            return null;
                        }
                    };

                    this.minecraftServer.postToMainThread(waitable);

                    try {
                        waitable.get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    this.disconnect("disconnect.spam");
                }
                // CraftBukkit end
            }

        }
    }

    // CraftBukkit start - add method
    public void chat(String s, boolean async) {
        if (s.isEmpty() || this.player.getChatFlags() == EntityHuman.EnumChatVisibility.HIDDEN) {
            return;
        }

        if (!async && s.startsWith("/")) {
            // PaperSpigot Start
            if (!org.bukkit.Bukkit.isPrimaryThread()) {
                final String fCommandLine = s;
                MinecraftServer.LOGGER.log(org.apache.logging.log4j.Level.ERROR,
                        "Command Dispatched Async: " + fCommandLine);
                MinecraftServer.LOGGER.log(org.apache.logging.log4j.Level.ERROR,
                        "Please notify author of plugin causing this execution to fix this bug! see: http://bit.ly/1oSiM6C",
                        new Throwable());
                Waitable<Object> wait = new Waitable<Object>() {
                    @Override
                    protected Object evaluate() {
                        chat(fCommandLine, false);
                        return null;
                    }
                };
                minecraftServer.postToMainThread(wait);
                try {
                    wait.get();
                    return;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // This is proper habit for java. If we aren't handling it, pass it on!
                } catch (Exception e) {
                    throw new RuntimeException("Exception processing chat command", e.getCause());
                }
            }
            // PaperSpigot End
            this.handleCommand(s);
        } else if (this.player.getChatFlags() != EntityHuman.EnumChatVisibility.SYSTEM) {
            Player player = this.getPlayer();
            AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, s, new LazyPlayerSet());
            this.server.getPluginManager().callEvent(event);

            getPlayer().getHandle().lastActivity = System.currentTimeMillis();

            if (event.isCancelled()) {
                return;
            }

            s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
            minecraftServer.console.sendMessage(s);
            if (((LazyPlayerSet) event.getRecipients()).isLazy()) {
                IChatBaseComponent[] msg = CraftChatMessage.fromString(s);
                for (EntityPlayer recipient : minecraftServer.getPlayerList().players) {
                    recipient.sendMessage(msg);
                }
            } else {
                for (Player recipient : event.getRecipients()) {
                    recipient.sendMessage(s);
                }
            }
        }
    }
    // CraftBukkit end

    private void handleCommand(String s) {
        // CraftBukkit start - whole method
        if (org.spigotmc.SpigotConfig.logCommands) // Spigot
            c.info(this.player.getName() + " issued server command: " + s);

        CraftPlayer player = this.getPlayer();

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, s, new LazyPlayerSet());
        this.server.getPluginManager().callEvent(event);

        getPlayer().getHandle().lastActivity = System.currentTimeMillis();

        if (event.isCancelled()) {
            return;
        }

        try {
            this.server.dispatchCommand(event.getPlayer(), event.getMessage().substring(1));
        } catch (org.bukkit.command.CommandException ex) {
            player.sendMessage(
                    org.bukkit.ChatColor.RED + "An internal error occurred while attempting to perform this command");
            java.util.logging.Logger.getLogger(PlayerConnection.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }
    }

    public void a(PacketPlayInArmAnimation packetplayinarmanimation) {
        if (this.player.dead)
            return;

        PlayerConnectionUtils.ensureMainThread(packetplayinarmanimation, this, this.player.u());
        this.player.resetIdleTimer();

        final float f1 = this.player.pitch;
        final float f2 = this.player.yaw;

        Vec3D vec3d = new Vec3D(player.locX, player.locY + this.player.getHeadHeight(), player.locZ);

        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        double d3 = player.playerInteractManager.getGameMode() == WorldSettings.EnumGamemode.CREATIVE ? 5.0D : 4.5D;

        MovingObjectPosition movingobjectposition = this.player.world.rayTrace(vec3d, vec3d.add(MathHelper.sin(-f2 * 0.017453292F - 3.1415927F) * f5 * d3, MathHelper.sin(-f1 * 0.017453292F) * d3, MathHelper.cos(-f2 * 0.017453292F - 3.1415927F) * f5 * d3), false);

        if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_AIR, this.player.inventory.getItemInHand());
        }

        // Arm swing animation
        PlayerAnimationEvent event = new PlayerAnimationEvent(this.getPlayer());
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;
        // CraftBukkit end
        this.player.bw();
    }

    public void a(PacketPlayInEntityAction packetplayinentityaction) {
        PlayerConnectionUtils.ensureMainThread(packetplayinentityaction, this, this.player.u());
        // CraftBukkit start
        if (this.player.dead)
            return;
        switch (packetplayinentityaction.b()) {
            case START_SNEAKING:
            case STOP_SNEAKING:
                PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING);
                this.server.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
                break;
            case START_SPRINTING:
            case STOP_SPRINTING:
                PlayerToggleSprintEvent e2 = new PlayerToggleSprintEvent(this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING);
                this.server.getPluginManager().callEvent(e2);

                if (e2.isCancelled()) {
                    return;
                }
                break;
        }
        // CraftBukkit end
        this.player.resetIdleTimer();
        switch (SyntheticClass_1.b[packetplayinentityaction.b().ordinal()]) {
            case 1:
                this.player.setSneaking(true);
                break;

            case 2:
                this.player.setSneaking(false);
                break;

            case 3:
                this.player.setSprinting(true);
                break;

            case 4:
                this.player.setSneaking(false);
                this.player.setSprinting(false);
                if (this.player.isBlocking()) {
                    this.player.bU(); // stopUsingItem
                }
                break;
            case 5:
                this.player.a(false, true, true);
                break;

            case 6:
                if (this.player.vehicle instanceof EntityHorse) {
                    ((EntityHorse) this.player.vehicle).v(packetplayinentityaction.c());
                }
                break;

            case 7:
                if (this.player.vehicle instanceof EntityHorse) {
                    ((EntityHorse) this.player.vehicle).g(this.player);
                }
                break;

            default:
                disconnect("Invalid client command!");
        }

    }

    public void a(PacketPlayInUseEntity packetplayinuseentity) {
        if (this.player.dead)
            return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayinuseentity, this, this.player.u());
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        Entity entity = packetplayinuseentity.a(worldserver);
        // Spigot Start
        if (entity == player && !player.isSpectator()) {
            disconnect("Cannot interact with self!");
            return;
        }
        // Spigot End
        this.player.resetIdleTimer();
        if (entity != null) {
            double d0 = 36.0D;

            if (!player.hasLineOfSight(entity)) {
                d0 = 9.0D;
            }

            if (this.player.h(entity) < d0) {
                ItemStack itemInHand = this.player.inventory.getItemInHand(); // CraftBukkit

                if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT
                        || packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                    // CraftBukkit start
                    boolean triggerLeashUpdate = itemInHand != null && itemInHand.getItem() == Items.LEAD
                            && entity instanceof EntityInsentient;
                    Item origItem = this.player.inventory.getItemInHand() == null ? null
                            : this.player.inventory.getItemInHand().getItem();
                    PlayerInteractEntityEvent event;
                    if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                        event = new PlayerInteractEntityEvent(this.getPlayer(), entity.getBukkitEntity());
                    } else {
                        Vec3D target = packetplayinuseentity.b();
                        event = new PlayerInteractAtEntityEvent(this.getPlayer(), entity.getBukkitEntity(),
                                new org.bukkit.util.Vector(target.a, target.b, target.c));
                    }
                    this.server.getPluginManager().callEvent(event);

                    if (triggerLeashUpdate && (event.isCancelled() || this.player.inventory.getItemInHand() == null
                            || this.player.inventory.getItemInHand().getItem() != Items.LEAD)) {
                        // Refresh the current leash state
                        this.sendPacket(
                                new PacketPlayOutAttachEntity(1, entity, ((EntityInsentient) entity).getLeashHolder()));
                    }

                    if (event.isCancelled() || this.player.inventory.getItemInHand() == null
                            || this.player.inventory.getItemInHand().getItem() != origItem) {
                        // Refresh the current entity metadata
                        this.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.datawatcher, true));
                    }

                    if (event.isCancelled()) {
                        return;
                    }
                    // CraftBukkit end
                }
                if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                    this.player.u(entity);

                    // CraftBukkit start
                    if (itemInHand != null && itemInHand.count <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                } else if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                    entity.a(this.player, packetplayinuseentity.b());

                    // CraftBukkit start
                    if (itemInHand != null && itemInHand.count <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                } else if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                    if (entity instanceof EntityItem || entity instanceof EntityExperienceOrb || entity instanceof EntityArrow || (entity == this.player && !player.isSpectator())) { // CraftBukkit
                        this.disconnect("Attempting to attack an invalid entity");
                        this.minecraftServer.warning("Player " + this.player.getName() + " tried to attack an invalid entity");
                        return;
                    }

                    this.player.attack(entity);

                    // CraftBukkit start
                    if (itemInHand != null && itemInHand.count <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                } else {
                    this.disconnect("Attempting to attack without action (Hacking?)"); // Vortex
                }
            }
        }

    }

    public void a(PacketPlayInClientCommand packetplayinclientcommand) {
        PlayerConnectionUtils.ensureMainThread(packetplayinclientcommand, this, this.player.u());
        this.player.resetIdleTimer();
        PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand = packetplayinclientcommand
                .a();

        switch (SyntheticClass_1.c[packetplayinclientcommand_enumclientcommand.ordinal()]) {
            case 1:
                if (this.player.viewingCredits) {
                    // CraftBukkit - reroute logic through custom
                    // portal management
                    this.minecraftServer.getPlayerList().changeDimension(this.player, 0, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                } else if (this.player.u().getWorldData().isHardcore()) {
                    GameProfileBanEntry gameprofilebanentry = new GameProfileBanEntry(this.player.getProfile(), null,
                            "(You just lost the game)", null, "Death in Hardcore");

                    this.minecraftServer.getPlayerList().getProfileBans().add(gameprofilebanentry);
                    this.player.playerConnection.disconnect("You have died. Game over, man, it's game over!");
                } else if (this.player.getHealth() <= 0.0F) {
                    this.player = this.minecraftServer.getPlayerList().moveToWorld(this.player, 0, false);
                }
                break;

            case 2:
                this.player.getStatisticManager().a(this.player);
                break;
        }

    }

    public void a(PacketPlayInCloseWindow packetplayinclosewindow) {
        if (this.player.dead)
            return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayinclosewindow, this, this.player.u());
        if (packetplayinclosewindow.id == player.activeContainer.windowId) {
            CraftEventFactory.handleInventoryCloseEvent(this.player);
            this.player.p();
            if (this.player.isAnimating()) {
                this.player.bU(); // stopUsingItem | Vortex - Fix for eating and blocking and etc while
                // running
            }
        }
    }

    public void a(PacketPlayInWindowClick packetplayinwindowclick) {
        if (player.dead)
            return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayinwindowclick, this, player.u());
        player.resetIdleTimer();
        if (player.activeContainer.windowId == packetplayinwindowclick.a() && player.activeContainer.c(player)) {
            int b = packetplayinwindowclick.b();
            boolean cancelled = player.isSpectator();

            if (b < -1 && b != -999) {
                return;
            }

            InventoryView inventory = player.activeContainer.getBukkitView();
            SlotType type = CraftInventoryView.getSlotType(inventory, b);

            InventoryClickEvent event;
            ClickType click = ClickType.UNKNOWN;
            InventoryAction action = InventoryAction.UNKNOWN;

            ItemStack itemstack = null;
            int c = packetplayinwindowclick.c();
            int f = packetplayinwindowclick.f();
            if (b == -1) {
                type = SlotType.OUTSIDE; // override
                click = c == 0 ? ClickType.WINDOW_BORDER_LEFT : ClickType.WINDOW_BORDER_RIGHT;
                action = InventoryAction.NOTHING;
            } else if (f == 0) {
                if (c == 0) {
                    click = ClickType.LEFT;
                } else if (c == 1) {
                    click = ClickType.RIGHT;
                }
                if (c == 0 || c == 1) {
                    action = InventoryAction.NOTHING; // Don't want to repeat ourselves
                    if (b == -999) {
                        if (player.inventory.getCarried() != null) {
                            action = c == 0 ? InventoryAction.DROP_ALL_CURSOR : InventoryAction.DROP_ONE_CURSOR;
                        }
                    } else {
                        Slot slot = this.player.activeContainer.getSlot(b);
                        if (slot != null) {
                            ItemStack clickedItem = slot.getItem();
                            ItemStack cursor = player.inventory.getCarried();
                            if (clickedItem == null) {
                                if (cursor != null) {
                                    action = c == 0 ? InventoryAction.PLACE_ALL : InventoryAction.PLACE_ONE;
                                }
                            } else if (slot.isAllowed(player)) {
                                if (cursor == null) {
                                    action = c == 0 ? InventoryAction.PICKUP_ALL : InventoryAction.PICKUP_HALF;
                                } else if (slot.isAllowed(cursor)) {
                                    if (clickedItem.doMaterialsMatch(cursor) && ItemStack.equals(clickedItem, cursor)) {
                                        int toPlace = c == 0 ? cursor.count : 1;
                                        toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.count);
                                        toPlace = Math.min(toPlace,
                                                slot.inventory.getMaxStackSize() - clickedItem.count);
                                        if (toPlace == 1) {
                                            action = InventoryAction.PLACE_ONE;
                                        } else if (toPlace == cursor.count) {
                                            action = InventoryAction.PLACE_ALL;
                                        } else if (toPlace < 0) {
                                            action = toPlace != -1 ? InventoryAction.PICKUP_SOME
                                                    : InventoryAction.PICKUP_ONE; // this happens with oversized stacks
                                        } else if (toPlace != 0) {
                                            action = InventoryAction.PLACE_SOME;
                                        }
                                    } else if (cursor.count <= slot.getMaxStackSize()) {
                                        action = InventoryAction.SWAP_WITH_CURSOR;
                                    }
                                } else if (cursor.getItem() == clickedItem.getItem()
                                        && (!cursor.usesData() || cursor.getData() == clickedItem.getData())
                                        && ItemStack.equals(cursor, clickedItem)) {
                                    if (clickedItem.count >= 0) {
                                        if (clickedItem.count + cursor.count <= cursor.getMaxStackSize()) {
                                            // As of 1.5, this is result slots only
                                            action = InventoryAction.PICKUP_ALL;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (f == 1) {
                if (c == 0) {
                    click = ClickType.SHIFT_LEFT;
                } else if (c == 1) {
                    click = ClickType.SHIFT_RIGHT;
                }
                if (c == 0 || c == 1) {
                    if (b < 0) {
                        action = InventoryAction.NOTHING;
                    } else {
                        Slot slot = this.player.activeContainer.getSlot(b);
                        if (slot != null && slot.isAllowed(this.player) && slot.hasItem()) {
                            action = InventoryAction.MOVE_TO_OTHER_INVENTORY;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                }
            } else if (f == 2) {
                if (c >= 0 && c < 9) {
                    click = ClickType.NUMBER_KEY;
                    Slot clickedSlot = this.player.activeContainer.getSlot(b);
                    if (clickedSlot.isAllowed(player)) {
                        ItemStack hotbar = this.player.inventory.getItem(c);
                        boolean canCleanSwap = hotbar == null || (clickedSlot.inventory == player.inventory && clickedSlot.isAllowed(hotbar));
                        if (clickedSlot.hasItem()) {
                            if (canCleanSwap) {
                                action = InventoryAction.HOTBAR_SWAP;
                            } else {
                                int firstEmptySlot = player.inventory.getFirstEmptySlotIndex();
                                if (firstEmptySlot > -1) {
                                    action = InventoryAction.HOTBAR_MOVE_AND_READD;
                                } else {
                                    action = InventoryAction.NOTHING; // This is not sane! Mojang: You should test for
                                    // other slots of same type
                                }
                            }
                        } else if (!clickedSlot.hasItem() && hotbar != null && clickedSlot.isAllowed(hotbar)) {
                            action = InventoryAction.HOTBAR_SWAP;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    } else {
                        action = InventoryAction.NOTHING;
                    }
                }
            } else if (f == 3) {
                if (c == 2) {
                    click = ClickType.MIDDLE;
                    if (b == -999) {
                        action = InventoryAction.NOTHING;
                    } else {
                        Slot slot = this.player.activeContainer.getSlot(b);
                        if (slot != null && slot.hasItem() && player.abilities.canInstantlyBuild
                                && player.inventory.getCarried() == null) {
                            action = InventoryAction.CLONE_STACK;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                }
            } else if (f == 4) {
                if (b >= 0) {
                    if (c == 0) {
                        click = ClickType.DROP;
                        Slot slot = this.player.activeContainer.getSlot(b);
                        if (slot != null && slot.hasItem() && slot.isAllowed(player) && slot.getItem() != null
                                && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                            action = InventoryAction.DROP_ONE_SLOT;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    } else if (c == 1) {
                        click = ClickType.CONTROL_DROP;
                        Slot slot = this.player.activeContainer.getSlot(b);
                        if (slot != null && slot.hasItem() && slot.isAllowed(player) && slot.getItem() != null
                                && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                            action = InventoryAction.DROP_ALL_SLOT;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                } else {
                    // Sane default (because this happens when they are holding nothing. Don't ask
                    // why.)
                    click = ClickType.LEFT;
                    if (c == 1) {
                        click = ClickType.RIGHT;
                    }
                    action = InventoryAction.NOTHING;
                }
            } else if (f == 5) {
                itemstack = this.player.activeContainer.clickItem(b, c, 5, this.player);
            } else if (f == 6) {
                click = ClickType.DOUBLE_CLICK;
                action = InventoryAction.NOTHING;
                if (packetplayinwindowclick.b() >= 0 && this.player.inventory.getCarried() != null) {
                    ItemStack cursor = this.player.inventory.getCarried();
                    // Quick check for if we have any of the item
                    if (inventory.getTopInventory()
                            .contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem())))
                            || inventory.getBottomInventory()
                            .contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem())))) {
                        action = InventoryAction.COLLECT_TO_CURSOR;
                    }
                }
            }

            if (f != 5) {
                if (click == ClickType.NUMBER_KEY) {
                    event = new InventoryClickEvent(inventory, type, b, click, action, c);
                } else {
                    event = new InventoryClickEvent(inventory, type, b, click, action);
                }

                org.bukkit.inventory.Inventory top = inventory.getTopInventory();
                if (packetplayinwindowclick.b() == 0 && top instanceof CraftingInventory) {
                    org.bukkit.inventory.Recipe recipe = ((CraftingInventory) top).getRecipe();
                    if (recipe != null) {
                        if (click == ClickType.NUMBER_KEY) {
                            event = new CraftItemEvent(recipe, inventory, type, b, click, action, c);
                        } else {
                            event = new CraftItemEvent(recipe, inventory, type, b, click, action);
                        }
                    }
                }

                event.setCancelled(cancelled);
                server.getPluginManager().callEvent(event);

                switch (event.getResult()) {
                    case ALLOW:
                    case DEFAULT:
                        itemstack = this.player.activeContainer.clickItem(b, c, f, this.player);
                        // PaperSpigot start - Stackable Buckets
                        if (itemstack != null && ((itemstack.getItem() == Items.LAVA_BUCKET && PaperSpigotConfig.stackableLavaBuckets) || (itemstack.getItem() == Items.WATER_BUCKET && PaperSpigotConfig.stackableWaterBuckets) || (itemstack.getItem() == Items.MILK_BUCKET && PaperSpigotConfig.stackableMilkBuckets))) {
                            if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                this.player.updateInventory(this.player.activeContainer);
                            } else {
                                sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                                sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, b, this.player.activeContainer.getSlot(b).getItem()));
                            }
                        }
                        // PaperSpigot end
                        break;
                    case DENY:
                        switch (action) {
                            // Modified other slots
                            case PICKUP_ALL:
                            case MOVE_TO_OTHER_INVENTORY:
                            case HOTBAR_MOVE_AND_READD:
                            case HOTBAR_SWAP:
                            case COLLECT_TO_CURSOR:
                            case UNKNOWN:
                                this.player.updateInventory(this.player.activeContainer);
                                break;
                            // Modified cursor and clicked
                            case PICKUP_SOME:
                            case PICKUP_HALF:
                            case PICKUP_ONE:
                            case PLACE_ALL:
                            case PLACE_SOME:
                            case PLACE_ONE:
                            case SWAP_WITH_CURSOR:
                                sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                                sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, b,
                                        this.player.activeContainer.getSlot(b).getItem()));
                                break;
                            // Modified clicked only
                            case DROP_ALL_SLOT:
                            case DROP_ONE_SLOT:
                                sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, b,
                                        this.player.activeContainer.getSlot(b).getItem()));
                                break;
                            // Modified cursor only
                            case DROP_ALL_CURSOR:
                            case DROP_ONE_CURSOR:
                            case CLONE_STACK:
                                sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                                break;
                            // Nothing
                            case NOTHING:
                                break;
                        }
                        return;
                }

                if (event instanceof CraftItemEvent) {
                    // Need to update the inventory on crafting to
                    // correctly support custom recipes
                    player.updateInventory(player.activeContainer);
                }
            }
            // CraftBukkit end

            if (ItemStack.matches(packetplayinwindowclick.e(), itemstack)) {
                sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), true));
                player.g = true;
                player.activeContainer.b();
                player.broadcastCarriedItem();
                player.g = false;
            } else {
                n.put(this.player.activeContainer.windowId, packetplayinwindowclick.d());
                sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), false));
                player.activeContainer.a(player, false);
                ArrayList<ItemStack> arraylist1 = Lists.newArrayList();

                for (int j = 0; j < player.activeContainer.c.size(); ++j) {
                    arraylist1.add(player.activeContainer.c.get(j).getItem());
                }

                player.a(player.activeContainer, arraylist1);
            }
        }

    }

    public void a(PacketPlayInEnchantItem packetplayinenchantitem) {
        PlayerConnectionUtils.ensureMainThread(packetplayinenchantitem, this, this.player.u());
        this.player.resetIdleTimer();
        if (this.player.activeContainer.windowId == packetplayinenchantitem.a()
                && this.player.activeContainer.c(this.player) && !this.player.isSpectator()) {
            this.player.activeContainer.a(this.player, packetplayinenchantitem.b());
            this.player.activeContainer.b();
        }

    }

    public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsetcreativeslot, this, this.player.u());
        if (this.player.playerInteractManager.isCreative()) {
            boolean flag = packetplayinsetcreativeslot.a() < 0;
            ItemStack itemstack = packetplayinsetcreativeslot.getItemStack();

            if (itemstack != null && itemstack.hasTag() && itemstack.getTag().hasKeyOfType("BlockEntityTag", 10)) {
                NBTTagCompound nbttagcompound = itemstack.getTag().getCompound("BlockEntityTag");

                if (nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z")) {
                    BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
                    TileEntity tileentity = this.player.world.getTileEntity(blockposition);

                    if (tileentity != null) {
                        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                        tileentity.b(nbttagcompound1);
                        nbttagcompound1.remove("x");
                        nbttagcompound1.remove("y");
                        nbttagcompound1.remove("z");
                        itemstack.a("BlockEntityTag", nbttagcompound1);
                    }
                }
            }

            boolean flag1 = packetplayinsetcreativeslot.a() >= 1 && packetplayinsetcreativeslot.a() < 36 + PlayerInventory.getHotbarSize();
            // CraftBukkit - Add invalidItems check
            boolean flag2 = itemstack == null || itemstack.getItem() != null && (!invalidItems.contains(Item.getId(itemstack.getItem())) || !org.spigotmc.SpigotConfig.filterCreativeItems); // Spigot
            boolean flag3 = itemstack == null || itemstack.getData() >= 0 && itemstack.count <= 64 && itemstack.count > 0;
            // CraftBukkit start - Call click event
            if (flag || (flag1 && !ItemStack.matches(this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem(), packetplayinsetcreativeslot.getItemStack()))) { // Insist on valid slot

                org.bukkit.entity.HumanEntity player = this.player.getBukkitEntity();
                InventoryView inventory = new CraftInventoryView(player, player.getInventory(), this.player.defaultContainer);
                org.bukkit.inventory.ItemStack item = CraftItemStack.asBukkitCopy(packetplayinsetcreativeslot.getItemStack());

                SlotType type = SlotType.QUICKBAR;
                if (flag) {
                    type = SlotType.OUTSIDE;
                } else if (packetplayinsetcreativeslot.a() < 36) {
                    if (packetplayinsetcreativeslot.a() >= 5 && packetplayinsetcreativeslot.a() < 9) {
                        type = SlotType.ARMOR;
                    } else {
                        type = SlotType.CONTAINER;
                    }
                }
                InventoryCreativeEvent event = new InventoryCreativeEvent(inventory, type, flag ? -999 : packetplayinsetcreativeslot.a(), item);
                server.getPluginManager().callEvent(event);

                itemstack = CraftItemStack.asNMSCopy(event.getCursor());

                switch (event.getResult()) {
                    case ALLOW:
                        // Plugin cleared the id / stacksize checks
                        flag2 = flag3 = true;
                        break;
                    case DEFAULT:
                        break;
                    case DENY:
                        // Reset the slot
                        if (packetplayinsetcreativeslot.a() >= 0) {
                            sendPacket(new PacketPlayOutSetSlot(this.player.defaultContainer.windowId, packetplayinsetcreativeslot.a(), this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem()));
                            sendPacket(new PacketPlayOutSetSlot(-1, -1, null));
                        }
                        return;
                }
            }
            // CraftBukkit end

            if (flag1 && flag2 && flag3) {
                this.player.defaultContainer.setItem(packetplayinsetcreativeslot.a(), itemstack);

                this.player.defaultContainer.a(this.player, true);
            } else if (flag && flag2 && flag3 && this.m < 200) {
                this.m += 20;
                EntityItem entityitem = this.player.drop(itemstack, true);

                if (entityitem != null) {
                    entityitem.j();
                }
            }
        }

    }

    public void a(PacketPlayInTransaction packetplayintransaction) {
        if (this.player.dead)
            return; // CraftBukkit
        PlayerConnectionUtils.ensureMainThread(packetplayintransaction, this, this.player.u());
        Short oshort = this.n.get(this.player.activeContainer.windowId);
        if (oshort != null && packetplayintransaction.b() == oshort && this.player.activeContainer.windowId == packetplayintransaction.a() && !this.player.activeContainer.c(this.player) && !this.player.isSpectator()) {
            this.player.activeContainer.a(this.player, true);
        }
    }

    public void a(PacketPlayInUpdateSign packetplayinupdatesign) {
        if (this.player.dead)
            return;
        PlayerConnectionUtils.ensureMainThread(packetplayinupdatesign, this, this.player.u());
        this.player.resetIdleTimer();
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        BlockPosition blockposition = packetplayinupdatesign.a();

        if (worldserver.isLoaded(blockposition)) {
            TileEntity tileentity = worldserver.getTileEntity(blockposition);

            if (!(tileentity instanceof TileEntitySign)) {
                return;
            }

            TileEntitySign tileentitysign = (TileEntitySign) tileentity;

            if (!tileentitysign.b() || tileentitysign.c() != this.player) {
                this.minecraftServer.warning("Player " + this.player.getName() + " just tried to change non-editable sign");
                this.sendPacket(new PacketPlayOutUpdateSign(tileentity.world, packetplayinupdatesign.a(), tileentitysign.lines)); // CraftBukkit
                return;
            }

            IChatBaseComponent[] aichatbasecomponent = packetplayinupdatesign.b();

            // CraftBukkit start
            Player player = this.server.getPlayer(this.player);
            String[] lines = new String[4];

            for (int i = 0; i < aichatbasecomponent.length; ++i) {
                lines[i] = EnumChatFormat.a(aichatbasecomponent[i].c());
            }
            final BlockPosition a = packetplayinupdatesign.a();
            SignChangeEvent event = new SignChangeEvent(player.getWorld().getBlockAt(a.getX(), a.getY(), a.getZ()), this.server.getPlayer(this.player), lines);
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                System.arraycopy(org.bukkit.craftbukkit.block.CraftSign.sanitizeLines(event.getLines()), 0, tileentitysign.lines, 0, 4);
                tileentitysign.isEditable = false;
            }
            // CraftBukkit end

            tileentitysign.update();
            worldserver.notify(blockposition);
        }

    }

    public void a(PacketPlayInKeepAlive packetplayinkeepalive) {
        if (packetplayinkeepalive.a() == this.i) {
            this.player.ping = (this.player.ping * 3 + (int) (this.d() - this.j)) / 4;
        }
    }

    private long d() {
        return System.nanoTime() / 1000000L;
    }

    public void a(PacketPlayInAbilities packetplayinabilities) {
        PlayerConnectionUtils.ensureMainThread(packetplayinabilities, this, this.player.u());
        // CraftBukkit start
        if (this.player.abilities.canFly && this.player.abilities.isFlying != packetplayinabilities.isFlying()) {
            PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(this.server.getPlayer(this.player), packetplayinabilities.isFlying());
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.player.abilities.isFlying = packetplayinabilities.isFlying(); // Actually set the player's flying status
            } else {
                this.player.updateAbilities(); // Tell the player their ability was reverted
            }
        }
        // CraftBukkit end
    }

    public void a(PacketPlayInTabComplete packetplayintabcomplete) {
        PlayerConnectionUtils.ensureMainThread(packetplayintabcomplete, this, this.player.u());
        // CraftBukkit start
        if (chatSpamField.addAndGet(this, 10) > 500 && !this.minecraftServer.getPlayerList().isOp(this.player.getProfile())) {
            this.disconnect("disconnect.spam");
            return;
        }
        // CraftBukkit end
        List<String> arraylist = minecraftServer.tabCompleteCommand(this.player, packetplayintabcomplete.a(), packetplayintabcomplete.b()); // Vortex - Don't copy the list
        sendPacket(new PacketPlayOutTabComplete(arraylist.toArray(new String[0])));
    }

    public void a(PacketPlayInSettings packetplayinsettings) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsettings, this, this.player.u());
        this.player.a(packetplayinsettings);
    }

    public void a(PacketPlayInCustomPayload packetplayincustompayload) {
        PlayerConnectionUtils.ensureMainThread(packetplayincustompayload, this, this.player.u());
        PacketDataSerializer pds = packetplayincustompayload.b();

        String tag = packetplayincustompayload.a();

        // Vortex start - Anticrash
        final int capacity = pds.capacity();
        if (capacity > 4800 || capacity < 1) {
            disconnect("Wrong capacity!");
            return;
        } else if (pds.refCnt() < 1) {
            disconnect("refCnt is lower than 1!");
            return;
        }
        // Vortex end

        // Vortex - use switch instead of "else if"
        switch (tag) {

            case "MC|BEdit": {
                PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.wrappedBuffer(pds));

                try {
                    ItemStack itemstack = packetdataserializer.i();
                    if (itemstack == null) {
                        return;
                    }

                    if (!ItemBookAndQuill.b(itemstack.getTag())) {
                        throw new IOException("Invalid book tag!");
                    }

                    ItemStack itemstack1 = this.player.inventory.getItemInHand();
                    if (itemstack1 == null) {
                        return;
                    }
                    if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack.getItem() == itemstack1.getItem()) {
                        ItemStack newBook = itemstack1.cloneItemStack();
                        if (!newBook.hasTag()) {
                            newBook.setTag(new NBTTagCompound());
                        }
                        newBook.getTag().set("pages", itemstack.getTag().getList("pages", 8));

                    }

                } catch (Exception exception) {
                    PlayerConnection.c.error("Couldn't handle book info", exception);
                    this.disconnect("Invalid book data!"); // CraftBukkit
                    return;
                }
                break;
            }

            case "MC|BSign": {
                PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.wrappedBuffer(pds));

                try {
                    ItemStack itemstack = packetdataserializer.i();
                    if (itemstack == null) {
                        return;
                    }
                    if (!ItemWrittenBook.b(itemstack.getTag())) {
                        throw new IOException("Invalid book tag!");
                    }

                    ItemStack itemstack1 = this.player.inventory.getItemInHand();
                    if (itemstack1 != null) {
                        if (itemstack.getItem() == Items.WRITTEN_BOOK && itemstack1.getItem() == Items.WRITABLE_BOOK) {
                            ItemStack newBook = itemstack1.cloneItemStack();
                            NBTTagCompound tag1;
                            if (!newBook.hasTag()) {
                                newBook.setTag((tag1 = new NBTTagCompound()));
                            } else {
                                tag1 = newBook.getTag();
                            }
                            tag1.set("author", new NBTTagString(this.player.getName()));
                            tag1.set("title", new NBTTagString(itemstack.getTag().getString("title")));
                            tag1.set("pages", itemstack.getTag().getList("pages", 8));
                            newBook.setItem(Items.WRITTEN_BOOK);
                            CraftEventFactory.handleEditBookEvent(player, newBook); // CraftBukkit
                        }
                    }
                } catch (Exception exception1) {
                    PlayerConnection.c.error("Couldn't sign book", exception1);
                    this.disconnect("Invalid book data!"); // CraftBukkit
                    return;
                }
                break;
            }

            case "MC|TrSel": {
                try {
                    Container container = this.player.activeContainer;
                    if (container instanceof ContainerMerchant) {
                        ((ContainerMerchant) container).d(pds.readInt());
                    }
                } catch (Exception exception2) {
                    PlayerConnection.c.error("Couldn't select trade", exception2);
                    this.disconnect("Invalid trade data!"); // CraftBukkit
                }
                break;
            }

            case "MC|AdvCdm": {
                if (!this.minecraftServer.getEnableCommandBlock()) {
                    this.player.sendMessage(new ChatMessage("advMode.notEnabled"));
                } else if (this.player.getBukkitEntity().isOp() && this.player.abilities.canInstantlyBuild) {
                    try {
                        byte b0 = pds.readByte();
                        CommandBlockListenerAbstract commandblocklistenerabstract = null;

                        if (b0 == 0) {
                            TileEntity tileentity = this.player.world.getTileEntity(new BlockPosition(pds.readInt(), pds.readInt(), pds.readInt()));

                            if (tileentity instanceof TileEntityCommand) {
                                commandblocklistenerabstract = ((TileEntityCommand) tileentity).getCommandBlock();
                            }
                        } else if (b0 == 1) {
                            Entity entity = this.player.world.a(pds.readInt());

                            if (entity instanceof EntityMinecartCommandBlock) {
                                commandblocklistenerabstract = ((EntityMinecartCommandBlock) entity).getCommandBlock();
                            }
                        }

                        String s = pds.c(pds.readableBytes());
                        boolean flag = pds.readBoolean();

                        if (commandblocklistenerabstract != null) {
                            commandblocklistenerabstract.setCommand(s);
                            commandblocklistenerabstract.a(flag);
                            if (!flag) {
                                commandblocklistenerabstract.b((IChatBaseComponent) null);
                            }

                            commandblocklistenerabstract.h();
                            this.player.sendMessage(new ChatMessage("advMode.setCommand.success", s));
                        }
                    } catch (Exception exception3) {
                        PlayerConnection.c.error("Couldn't set command block", exception3);
                        this.disconnect("Invalid CommandBlock data!"); // CraftBukkit
                    }
                } else {
                    this.player.sendMessage(new ChatMessage("advMode.notAllowed"));
                }

                break;
            }

            case "MC|Beacon": {

                if (this.player.activeContainer instanceof ContainerBeacon) {
                    try {
                        ContainerBeacon containerbeacon = (ContainerBeacon) this.player.activeContainer;
                        Slot slot = containerbeacon.getSlot(0);

                        if (slot.hasItem()) {
                            slot.a(1);
                            IInventory iinventory = containerbeacon.e();

                            iinventory.b(1, pds.readInt());
                            iinventory.b(2, pds.readInt());
                            iinventory.update();
                        }
                    } catch (Exception exception4) {
                        PlayerConnection.c.error("Couldn't set beacon", exception4);
                        this.disconnect("Invalid beacon data!"); // CraftBukkit
                    }
                }

                break;
            }

            case "MC|ItemName": {

                if (player.activeContainer instanceof ContainerAnvil) {
                    ContainerAnvil containeranvil = (ContainerAnvil) this.player.activeContainer;

                    if (pds.readableBytes() >= 1) {
                        String s1 = SharedConstants.a(pds.c(32767));

                        if (s1.length() <= 30) {
                            containeranvil.a(s1);
                        }
                    } else {
                        containeranvil.a("");
                    }
                }

                break;
            }

            case "REGISTER": {
                String[] channels = pds.toString(Charsets.UTF_8).split("\0");
                if (channels.length > 124) {
                    disconnect("Too many channels"); // Vortex - Anti channel register spam crash
                    return;
                }
                for (String channel : channels) {
                    getPlayer().addChannel(channel);
                }
                break;
            }

            case "UNREGISTER": {
                for (String channel : pds.toString(Charsets.UTF_8).split("\0")) {
                    getPlayer().removeChannel(channel);
                }
                break;
            }

            default: {
                byte[] data = new byte[pds.readableBytes()];
                pds.readBytes(data);
                server.getMessenger().dispatchIncomingMessage(player.getBukkitEntity(), tag, data);
                break;
            }

        }

        pds.release();
    }

    // CraftBukkit start - Add "isDisconnected" method
    public boolean isDisconnected() { // Spigot
        return !this.player.joining && !this.networkManager.channel.config().isAutoRead();
    }

    static class SyntheticClass_1 {

        static final int[] a;
        static final int[] b;
        static final int[] c = new int[PacketPlayInClientCommand.EnumClientCommand.values().length];

        static {
            try {
                SyntheticClass_1.c[PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN
                        .ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.c[PacketPlayInClientCommand.EnumClientCommand.REQUEST_STATS
                        .ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.c[PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT
                        .ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            b = new int[PacketPlayInEntityAction.EnumPlayerAction.values().length];

            try {
                SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING
                        .ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING
                        .ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING
                        .ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING
                        .ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.STOP_SLEEPING
                        .ordinal()] = 5;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.RIDING_JUMP
                        .ordinal()] = 6;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.b[PacketPlayInEntityAction.EnumPlayerAction.OPEN_INVENTORY
                        .ordinal()] = 7;
            } catch (NoSuchFieldError ignored) {

            }

            a = new int[PacketPlayInBlockDig.EnumPlayerDigType.values().length];

            try {
                SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.DROP_ITEM.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.DROP_ALL_ITEMS
                        .ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM
                        .ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK
                        .ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK
                        .ordinal()] = 5;
            } catch (NoSuchFieldError ignored) {

            }

            try {
                SyntheticClass_1.a[PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK
                        .ordinal()] = 6;
            } catch (NoSuchFieldError ignored) {

            }

        }
    }

}
