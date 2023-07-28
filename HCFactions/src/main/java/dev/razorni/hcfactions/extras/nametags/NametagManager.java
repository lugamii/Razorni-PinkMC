package dev.razorni.hcfactions.extras.nametags;

import com.lunarclient.bukkitapi.LunarClientAPI;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.extras.nametags.listener.NametagListener;
import dev.razorni.hcfactions.extras.nametags.packet.NametagPacket;
import dev.razorni.hcfactions.providers.rTags;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.extra.FastReplaceString;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class NametagManager extends Manager {
    private final List<String> StaffLines;
    private final NametagAdapter adapter;
    private final Map<UUID, Nametag> nametags;
    private final ExecutorService executor;

    public NametagManager(HCF plugin) {
        super(plugin);
        this.nametags = new ConcurrentHashMap<>();
        this.adapter = new rTags(this);
        this.executor = Executors.newSingleThreadExecutor();
        this.StaffLines = this.getLunarConfig().getStringList("NAMETAGS.STAFF-MODE");
        new NametagListener(this);
    }

    private void updateLunarTags(Player from, Player to, String update) {
        if (this.getInstance().getWaypointManager().isLunarMissing()) {
            return;
        }
        List<String> lines = new ArrayList<>();
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(to.getUniqueId());
        String prefix = this.getInstance().getUserManager().getPrefix(to);
        boolean isStaff = this.getInstance().getStaffManager().isStaffEnabled(to);
        if (isStaff) {
            boolean isVanished = this.getInstance().getStaffManager().isVanished(to);
            lines.add(new FastReplaceString(this.getLunarConfig().getString("NAMETAGS.STAFF-MODE")).replaceAll("%vanished%", String.valueOf(isVanished)).endResult());
        }

        if (!isStaff && team != null) {
            String teamPosition = team.getTeamPosition();
            String bracketsleft = team.getTeamPosBracketsLeft();
            String bracketsright = team.getTeamPosBracketsRight();
            if (teamPosition != null) {
                lines.add(new FastReplaceString(this.getLunarConfig().getString("NAMETAGS.TEAM_TOP")).replaceAll("%bracketsright%", bracketsright ).replaceAll("%bracketsleft%", bracketsleft).replaceAll("%pos%", teamPosition).replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol()).endResult());
            } else {
                lines.add(new FastReplaceString(this.getLunarConfig().getString("NAMETAGS.NORMAL")).replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol()).endResult());
            }
        }
        if (this.getInstance().getVersionManager().isVer16() || Utils.isVer16(from)) {
            Tasks.execute(this, () -> LunarClientAPI.getInstance().overrideNametag(to, lines, from));
            return;
        }
        lines.add(((prefix != null) ? prefix + " " : "") + update + to.getName());
        LunarClientAPI.getInstance().overrideNametag(to, lines, from);
    }

    @Override
    public void disable() {
        this.executor.shutdown();
    }

    public void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                this.executor.execute(() -> {
                    String update = this.adapter.getAndUpdate(player, target);
                    this.updateLunarTags(player, target, update);
                });
            }
        }
    }

    @SneakyThrows
    public NametagPacket createPacket(Player player) {
        String version = "dev.razorni.hcfactions.extras.nametags.packet.type.NametagPacketV" + Utils.getNMSVer();
        return (NametagPacket) Class.forName(version).getConstructor(NametagManager.class, Player.class).newInstance(this, player);
    }

}