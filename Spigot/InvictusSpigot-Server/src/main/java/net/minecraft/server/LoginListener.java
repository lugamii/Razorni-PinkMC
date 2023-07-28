package net.minecraft.server;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.properties.Property;
import eu.vortexdev.invictusspigot.InvictusSpigot;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class LoginListener implements PacketLoginInListener, IUpdatePlayerListBox {

    private static final Logger c = LogManager.getLogger();
    private static final Random random = new Random();

    public final NetworkManager networkManager;
    private final byte[] e = new byte[4];
    private final MinecraftServer server;
    private final String j;
    public String hostname = "";
    private EnumProtocolState g;
    private int h;
    private GameProfile i;
    private SecretKey loginKey;
    private Future<?> future;

    public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.g = EnumProtocolState.HELLO;
        this.j = "";
        this.server = minecraftserver;
        this.networkManager = networkmanager;
        random.nextBytes(this.e);
    }

    public void c() {
        if (h++ == 600) {
            future.cancel(true);
            disconnect("Took too long to log in");
        }
    }

    public void disconnect(String s) {
        try {
            if (InvictusConfig.connectionLogs)
                c.info("Disconnecting " + d() + ": " + s);
            ChatComponentText chatcomponenttext = new ChatComponentText(s);
            this.networkManager.handle(new PacketLoginOutDisconnect(chatcomponenttext));
            this.networkManager.close(chatcomponenttext);
        } catch (Exception exception) {
            c.error("Error whilst disconnecting player", exception);
        }
    }

    public void initUUID() {
        UUID uuid;
        if (this.networkManager.spoofedUUID != null) {
            uuid = this.networkManager.spoofedUUID;
        } else {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.i.getName()).getBytes(Charsets.UTF_8));
        }
        this.i = new GameProfile(uuid, this.i.getName());
        if (this.networkManager.spoofedProfile != null)
            for (Property property : this.networkManager.spoofedProfile)
                this.i.getProperties().put(property.getName(), property);
    }

    public void b() {
        MinecraftServer.getServer().postToMainThread(() -> {
            EntityPlayer s = this.server.getPlayerList().attemptLogin(this, this.i, this.hostname);
            if (s != null) {
                this.g = EnumProtocolState.ACCEPTED;
                int compressionThreshold = server.aK();
                if (compressionThreshold >= 0 && !networkManager.c()) {
                    networkManager.a(new PacketLoginOutSetCompression(compressionThreshold), (ChannelFutureListener) future -> networkManager.a(compressionThreshold));
                }

                this.networkManager.handle(new PacketLoginOutSuccess(this.i));

                try {
                    server.getPlayerList().a(networkManager, server.getPlayerList().processLogin(i, s)); // CraftBukkit - add player reference
                } catch (NullPointerException e) {
                    s.getBukkitEntity().kickPlayer("Failed to process your login request.");
                }
            }
        });
    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        if (InvictusConfig.connectionLogs)
            c.info(d() + " lost connection: " + ichatbasecomponent.c());
    }

    public String d() {
        return (this.i != null) ? (this.i + " (" + this.networkManager.getSocketAddress().toString() + ")") : String.valueOf(this.networkManager.getSocketAddress());
    }

    public void a(PacketLoginInStart packetlogininstart) {
        Validate.validState((this.g == EnumProtocolState.HELLO), "Unexpected hello packet");
        this.i = packetlogininstart.a();
        if (this.server.getOnlineMode() && !this.networkManager.c()) {
            this.g = EnumProtocolState.KEY;
            this.networkManager.handle(new PacketLoginOutEncryptionBegin(this.j, this.server.Q().getPublic(), this.e));
        } else {
            future = InvictusSpigot.INSTANCE.getThreadingManager().getLoginPool().submit(() -> {
                try {
                    initUUID();
                    new LoginHandler().fireEvents();
                } catch (Exception ex) {
                    LoginListener.this.disconnect("Failed to verify username!");
                    LoginListener.this.server.server.getLogger().log(Level.WARNING, "Exception verifying " + LoginListener.this.i.getName(), ex);
                }
            });
        }
    }

    public void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
        Validate.validState((this.g == EnumProtocolState.KEY), "Unexpected key packet");
        PrivateKey privatekey = this.server.Q().getPrivate();
        if (!Arrays.equals(this.e, packetlogininencryptionbegin.b(privatekey)))
            throw new IllegalStateException("Invalid nonce!");
        loginKey = packetlogininencryptionbegin.a(privatekey);
        g = EnumProtocolState.AUTHENTICATING;
        networkManager.a(loginKey);
        future = InvictusSpigot.INSTANCE.getThreadingManager().getLoginPool().submit(() -> {
            GameProfile gameprofile = i;
            try {
                String s = (new BigInteger(Objects.requireNonNull(MinecraftEncryption.a(j, server.Q().getPublic(), loginKey)))).toString(16);
                i = server.aD().hasJoinedServer(new GameProfile(null, gameprofile.getName()), s);
                if (i != null) {
                    if (!networkManager.g())
                        return;
                    (new LoginListener.LoginHandler()).fireEvents();
                } else {
                    disconnect("Failed to verify username!");
                    c.error("Username '" + gameprofile.getName() + "' tried to join with an invalid session");
                }
            } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                disconnect("Authentication servers are down. Please try again later, sorry!");
                c.error("Couldn't verify username because servers are unavailable");
            } catch (Exception exception) {
                disconnect("Failed to verify username!");
                server.server.getLogger().log(Level.WARNING, "Exception verifying " + gameprofile.getName(), exception);
            }
        });
    }

    protected GameProfile a(GameProfile gameprofile) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameprofile.getName()).getBytes(Charsets.UTF_8));
        return new GameProfile(uuid, gameprofile.getName());
    }

    enum EnumProtocolState {
        HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, e, ACCEPTED
    }

    public class LoginHandler {
        public void fireEvents() {
            AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(i.getName(), ((InetSocketAddress) networkManager.getSocketAddress()).getAddress(), i.getId());
            server.server.getPluginManager().callEvent(event);
            if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                disconnect(event.getKickMessage());
                return;
            }
            if (InvictusConfig.connectionLogs)
                c.info("UUID of player " + i.getName() + " is " + i.getId());
            g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
            b();
        }
    }
}
