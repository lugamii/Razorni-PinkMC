package dev.razorni.hcfactions.utils.tablist.packet.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.utils.tablist.Tablist;
import dev.razorni.hcfactions.utils.tablist.TablistManager;
import dev.razorni.hcfactions.utils.tablist.extra.TablistEntry;
import dev.razorni.hcfactions.utils.tablist.extra.TablistSkin;
import dev.razorni.hcfactions.utils.tablist.packet.TablistPacket;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

import java.util.UUID;

public class TablistPacketV1_7_R4 extends TablistPacket {

    private static final Table<Integer, Integer, EntityPlayer> FAKE_PLAYERS;
    private static boolean LOADED;

    static {
        FAKE_PLAYERS = HashBasedTable.create();
        TablistPacketV1_7_R4.LOADED = false;
    }

    private final int maxColumns;
    private String footer;
    private String header;

    public TablistPacketV1_7_R4(TablistManager manager, Player player) {
        super(manager, player);
        this.header = "";
        this.footer = "";
        this.maxColumns = ((((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47) ? 4 : 3);
        this.loadFakes();
        this.init();
    }

    public void loadFakes() {
        if (!TablistPacketV1_7_R4.LOADED) {
            TablistPacketV1_7_R4.LOADED = true;
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            WorldServer worldServer = minecraftServer.getWorldServer(0);
            for (int i = 0; i < 20; ++i) {
                for (int t = 0; t < 4; ++t) {
                    String part = (t == 0) ? "LEFT" : ((t == 1) ? "MIDDLE" : ((t == 2) ? "RIGHT" : "FAR_RIGHT"));
                    String line = this.getTablistConfig().getStringList(part).get(i).split(";")[0];
                    GameProfile profile = new GameProfile(UUID.randomUUID(), this.getName(t, i));
                    EntityPlayer entityPlayer = new EntityPlayer(minecraftServer, worldServer, profile, new PlayerInteractManager(worldServer));
                    TablistSkin skin = this.getManager().getSkins().get(line);
                    profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
                    TablistPacketV1_7_R4.FAKE_PLAYERS.put(t, i, entityPlayer);
                }
            }
        }
    }

    @Override
    public void update() {
        this.sendHeaderFooter();
        Tablist tablist = this.getManager().getAdapter().getInfo(this.player);
        for (int i = 0; i < 20; ++i) {
            for (int f = 0; f < this.maxColumns; ++f) {
                TablistEntry entry = tablist.getEntries(f, i);
                EntityPlayer player = TablistPacketV1_7_R4.FAKE_PLAYERS.get(f, i);
                if (player.ping != entry.getPing()) {
                    player.ping = entry.getPing();
                    this.sendPacket(PacketPlayOutPlayerInfo.updatePing(player));
                }
                this.handleTeams(player.getBukkitEntity(), entry.getText(), this.calcSlot(f, i));
            }
        }
    }

    private void sendHeaderFooter() {
        if (this.maxColumns == 3) {
            return;
        }
        String header = String.join("\n", this.getManager().getAdapter().getHeader(this.player));
        String footer = String.join("\n", this.getManager().getAdapter().getFooter(this.player));
        if (this.footer.equals(footer) && this.header.equals(header)) {
            return;
        }
        this.header = header;
        this.footer = footer;
        this.sendPacket(new ProtocolInjector.PacketTabHeader(ChatSerializer.a("{\"text\":\"" + this.header + "\"}"), ChatSerializer.a("{\"text\":\"" + this.footer + "\"}")));
    }

    private void sendPacket(Packet packet) {
        PlayerConnection playerConnection = ((CraftPlayer) this.player).getHandle().playerConnection;
        if (playerConnection != null) {
            playerConnection.sendPacket(packet);
        }
    }

    public void init() {
        for (int i = 0; i < 20; ++i) {
            for (int f = 0; f < this.maxColumns; ++f) {
                EntityPlayer player = TablistPacketV1_7_R4.FAKE_PLAYERS.get(f, i);
                this.sendPacket(PacketPlayOutPlayerInfo.addPlayer(player));
            }
        }
    }
}
