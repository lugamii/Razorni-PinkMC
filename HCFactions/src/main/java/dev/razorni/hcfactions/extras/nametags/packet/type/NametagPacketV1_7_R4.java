package dev.razorni.hcfactions.extras.nametags.packet.type;

import dev.razorni.hcfactions.extras.nametags.NametagManager;
import dev.razorni.hcfactions.extras.nametags.extra.NameInfo;
import dev.razorni.hcfactions.extras.nametags.extra.NameVisibility;
import dev.razorni.hcfactions.extras.nametags.packet.NametagPacket;
import dev.razorni.hcfactions.utils.ReflectionUtils;
import lombok.SneakyThrows;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagPacketV1_7_R4 extends NametagPacket {
    private final Map<String, NameInfo> teams;
    private final Map<String, String> teamsByPlayer;

    public NametagPacketV1_7_R4(NametagManager manager, Player player) {
        super(manager, player);
        this.teams = new ConcurrentHashMap<>();
        this.teamsByPlayer = new ConcurrentHashMap<>();
    }

    @Override
    public void addToTeam(Player player, String team) {
        String name = this.teamsByPlayer.get(player.getName());
        NameInfo info = this.teams.get(team);
        if (name != null && name.equals(team)) {
            return;
        }
        if (info == null) {
            return;
        }
        this.teamsByPlayer.put(player.getName(), team);
        this.sendPacket(new ScoreboardPacket(info, 3, Collections.singletonList(player.getName())).toPacket());
    }

    @Override
    public void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibility) {
        NameInfo info = this.teams.get(name);
        if (info != null) {
            if (!info.getColor().equals(color) || !info.getPrefix().equals(prefix) || !info.getSuffix().equals(suffix)) {
                NameInfo infoT = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
                this.teams.put(name, infoT);
                this.sendPacket(new ScoreboardPacket(infoT, 2).toPacket());
            }
            return;
        }
        NameInfo info2 = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
        this.teams.put(name, info2);
        this.sendPacket(new ScoreboardPacket(info2, 0).toPacket());
    }

    private void sendPacket(Packet packet) {
        PlayerConnection connection = ((CraftPlayer) this.player).getHandle().playerConnection;
        if (connection != null) {
            connection.sendPacket(packet);
        }
    }

    private static class ScoreboardPacket {
        private static final Field c;
        private static final Field g;
        private static final Field a;
        private static final Field b;
        private static final Field f;
        private static final Field d;
        private static final Field e;

        static {
            a = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "a");
            b = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "b");
            c = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "c");
            d = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "d");
            e = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "e");
            f = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "f");
            g = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "g");
        }

        private final int action;
        private final List<String> players;
        private final NameInfo info;

        public ScoreboardPacket(NameInfo info, int action, List<String> players) {
            this.info = info;
            this.action = action;
            this.players = players;
        }

        public ScoreboardPacket(NameInfo info, int action) {
            this.info = info;
            this.action = action;
            this.players = Collections.emptyList();
        }

        @SneakyThrows
        public PacketPlayOutScoreboardTeam toPacket() {
            PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam();
            a.set(team, ScoreboardPacket.this.info.getName());
            f.set(team, ScoreboardPacket.this.action);
            if (ScoreboardPacket.this.action == 0 || ScoreboardPacket.this.action == 2) {
                b.set(team, ScoreboardPacket.this.info.getName());
                c.set(team, ScoreboardPacket.this.info.getPrefix() + ScoreboardPacket.this.info.getColor());
                d.set(team, ScoreboardPacket.this.info.getSuffix());
                g.set(team, ScoreboardPacket.this.info.isFriendlyInvis() ? 3 : 0);
            }
            if (ScoreboardPacket.this.action == 3 || ScoreboardPacket.this.action == 4) {
                e.set(team, ScoreboardPacket.this.players);
            }
            return team;
        }

        public int getAction() {
            return this.action;
        }

        public List<String> getPlayers() {
            return this.players;
        }

        public NameInfo getInfo() {
            return this.info;
        }
    }
}
