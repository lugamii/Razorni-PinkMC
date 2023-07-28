package net.minecraft.server;

// CraftBukkit start

import com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftIconCache;
import org.bukkit.entity.Player;
import org.bukkit.util.CachedServerIcon;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.NoSuchElementException;

// CraftBukkit end

public class PacketStatusListener implements PacketStatusInListener {

    private static final IChatBaseComponent a = new ChatComponentText("Status request has been handled.");
    private final MinecraftServer minecraftServer;
    private final NetworkManager networkManager;
    private boolean d;

    public PacketStatusListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.minecraftServer = minecraftserver;
        this.networkManager = networkmanager;
    }

    public void a(IChatBaseComponent ichatbasecomponent) {}

    public void a(PacketStatusInStart packetstatusinstart) {
        if (this.d) {
            this.networkManager.close(PacketStatusListener.a);
            // CraftBukkit start - fire ping event
            return;
        }
        this.d = true;
        final Object[] players = (this.minecraftServer.getPlayerList()).players.toArray();
        class ServerListPingEvent extends org.bukkit.event.server.ServerListPingEvent {
            CraftIconCache icon = PacketStatusListener.this.minecraftServer.server.getServerIcon();

            ServerListPingEvent() {
                super(((InetSocketAddress)PacketStatusListener.this.networkManager.getSocketAddress()).getAddress(), PacketStatusListener.this.minecraftServer.getMotd(), PacketStatusListener.this.minecraftServer.getPlayerList().getMaxPlayers());
            }

            public void setServerIcon(CachedServerIcon icon) {
                if (!(icon instanceof CraftIconCache))
                    throw new IllegalArgumentException(icon + " was not created by " + CraftServer.class);
                this.icon = (CraftIconCache)icon;
            }

            public Iterator<Player> iterator() throws UnsupportedOperationException {
                return new Iterator<Player>() {
                    int i;

                    int ret = Integer.MIN_VALUE;

                    EntityPlayer player;

                    public boolean hasNext() {
                        if (this.player != null)
                            return true;
                        for (int length = players.length, i = this.i; i < length; i++) {
                            EntityPlayer player = (EntityPlayer) players[i];
                            if (player != null) {
                                this.i = i + 1;
                                this.player = player;
                                return true;
                            }
                        }
                        return false;
                    }

                    public Player next() {
                        if (!hasNext())
                            throw new NoSuchElementException();
                        EntityPlayer player = this.player;
                        this.player = null;
                        this.ret = this.i - 1;
                        return player.getBukkitEntity();
                    }

                    public void remove() {
                        int i = this.ret;
                        if (i < 0 || players[i] == null)
                            throw new IllegalStateException();
                        players[i] = null;
                    }
                };
            }
        }
        ServerListPingEvent event = new ServerListPingEvent();
        this.minecraftServer.server.getPluginManager().callEvent(event);
        java.util.List<GameProfile> profiles = new java.util.ArrayList<>(players.length);
        for (Object player : players) {
            if (player != null)
                profiles.add(((EntityPlayer)player).getProfile());
        }

        ServerPing.ServerPingPlayerSample playerSample = new ServerPing.ServerPingPlayerSample(event.getMaxPlayers(), profiles.size());
        // Spigot Start
        if ( !profiles.isEmpty() )
        {
            java.util.Collections.shuffle( profiles ); // This sucks, its inefficient but we have no simple way of doing it differently
            profiles = profiles.subList( 0, Math.min( profiles.size(), org.spigotmc.SpigotConfig.playerSample ) ); // Cap the sample to n (or less) displayed players, ie: Vanilla behaviour
        }
        // Spigot End
        playerSample.a(profiles.toArray(new GameProfile[0]));

        ServerPing ping = new ServerPing();
        ping.setFavicon(event.icon.value);
        ping.setMOTD(new ChatComponentText(event.getMotd()));
        ping.setPlayerSample(playerSample);
        ping.setServerInfo(new ServerPing.ServerData(minecraftServer.getServerModName() + " " + minecraftServer.getVersion(), 47)); // TODO: Update when protocol changes

        this.networkManager.handle(new PacketStatusOutServerInfo(ping));
        // CraftBukkit end
    }

    public void a(PacketStatusInPing packetstatusinping) {
        this.networkManager.handle(new PacketStatusOutPong(packetstatusinping.a()));
        this.networkManager.close(PacketStatusListener.a);
    }
}
