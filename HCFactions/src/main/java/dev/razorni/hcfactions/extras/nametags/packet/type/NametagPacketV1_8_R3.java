package dev.razorni.hcfactions.extras.nametags.packet.type;

import dev.razorni.hcfactions.extras.nametags.NametagManager;
import dev.razorni.hcfactions.extras.nametags.extra.NameInfo;
import dev.razorni.hcfactions.extras.nametags.extra.NameVisibility;
import dev.razorni.hcfactions.extras.nametags.packet.NametagPacket;
import dev.razorni.hcfactions.utils.ReflectionUtils;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagPacketV1_8_R3 extends NametagPacket {
    private final Map<String, String> teamsByPlayer;
    private final Map<String, NameInfo> teams;

    public NametagPacketV1_8_R3(NametagManager manager, Player player) {
        super(manager, player);
        this.teams = new ConcurrentHashMap<>();
        this.teamsByPlayer = new ConcurrentHashMap<>();
    }

    @Override
    public void addToTeam(Player player, String team) {
        String playerTeam = this.teamsByPlayer.get(player.getName());
        NameInfo info = this.teams.get(team);
        if (playerTeam != null && playerTeam.equals(team)) {
            return;
        }
        if (info == null) {
            return;
        }
        this.teamsByPlayer.put(player.getName(), team);
        this.sendPacket(new ScoreboardPacket(info, 3, Collections.singletonList(player.getName())).toPacket());
    }

    private void sendPacket(Packet<?> packet) {
        PlayerConnection connection = ((CraftPlayer) this.player).getHandle().playerConnection;
        if (connection != null) {
            connection.sendPacket(packet);
        }
    }

    @Override
    public void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibility) {
        NameInfo info1 = this.teams.get(name);
        if (info1 != null) {
            if (!info1.getColor().equals(color) || !info1.getPrefix().equals(prefix) || !info1.getSuffix().equals(suffix)) {
                NameInfo info2 = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
                this.teams.put(name, info2);
                this.sendPacket(new ScoreboardPacket(info2, 2).toPacket());
            }
            return;
        }
        NameInfo info3 = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
        this.teams.put(name, info3);
        this.sendPacket(new ScoreboardPacket(info3, 0).toPacket());
    }

    private static class ScoreboardPacket {
        private static final Field b;
        private static final Field c;
        private static final Field g;
        private static final Field d;
        private static final Field i;
        private static final Field e;
        private static final Field a;
        private static final Field h;

        static {
            a = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "a");
            b = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "b");
            c = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "c");
            d = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "d");
            i = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "i");
            h = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "h");
            g = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "g");
            e = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "e");
        }

        private final NameInfo info;
        private final int action;
        private final List<String> players;

        public ScoreboardPacket(NameInfo info, int action) {
            this.info = info;
            this.action = action;
            this.players = Collections.emptyList();
        }

        public ScoreboardPacket(NameInfo info, int action, List<String> players) {
            this.info = info;
            this.action = action;
            this.players = players;
        }

        @SneakyThrows
        public PacketPlayOutScoreboardTeam toPacket() {
            PacketPlayOutScoreboardTeam scoreboardTeam = new PacketPlayOutScoreboardTeam();
            ScoreboardPacket.a.set(scoreboardTeam, this.info.getName());
            ScoreboardPacket.h.set(scoreboardTeam, this.action);
            if (this.action == 0 || this.action == 2) {
                ScoreboardPacket.b.set(scoreboardTeam, this.info.getName());
                ScoreboardPacket.c.set(scoreboardTeam, (this.info.getPrefix().isEmpty() && this.info.getColor().isEmpty()) ? "" : this.info.getPrefix() + this.info.getColor());
                ScoreboardPacket.d.set(scoreboardTeam, this.info.getSuffix());
                ScoreboardPacket.i.set(scoreboardTeam, this.info.isFriendlyInvis() ? 3 : 0);
                ScoreboardPacket.e.set(scoreboardTeam, this.info.getVisibility().getName());
            }
            if (this.action == 3 || this.action == 4) {
                ScoreboardPacket.g.set(scoreboardTeam, this.players);
            }
            return scoreboardTeam;
        }
    }
}
