package dev.razorni.hcfactions.teams;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.enums.TeamType;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Team extends Module<TeamManager> {
    protected TeamType type;
    protected UUID leader;
    protected boolean deathban;
    protected Location hq;
    protected List<Claim> claims;
    protected String name;
    protected UUID uniqueID;

    public Team(TeamManager manager, String name, UUID leader, boolean deathban, TeamType type) {
        super(manager);
        this.name = name;
        this.deathban = deathban;
        this.claims = new ArrayList<>();
        this.type = type;
        this.hq = null;
        this.uniqueID = UUID.randomUUID();
        this.leader = leader;
        if (type != TeamType.PLAYER && type != TeamType.WARZONE && type != TeamType.WILDERNESS) {
            this.getManager().getSystemTeams().put(this.uniqueID, this);
        }
    }

    public Team(TeamManager manager, Map<String, Object> map, boolean deathban, TeamType type) {
        super(manager);
        this.claims = Utils.createList(map.get("claims"), String.class).stream().map(Serializer::deserializeClaim).collect(Collectors.toList());
        this.name = (String) map.get("name");
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.leader = UUID.fromString((String) map.get("leader"));
        this.deathban = deathban;
        this.type = type;
        if (map.containsKey("hq")) {
            this.hq = Serializer.deserializeLoc((String) map.get("hq"));
        }
        if (type != TeamType.PLAYER && type != TeamType.WARZONE && type != TeamType.WILDERNESS) {
            this.getManager().getSystemTeams().put(this.uniqueID, this);
        }
    }

    public void delete() {
        this.getManager().getTeams().remove(this.uniqueID);
        this.getManager().getStringTeams().remove(this.name);
        this.getInstance().getStorageManager().getStorage().deleteTeam(this);
    }

    public void save() {
        this.getManager().getTeams().put(this.uniqueID, this);
        this.getManager().getStringTeams().put(this.name, this);
        this.getInstance().getStorageManager().getStorage().saveTeam(this, true);
    }

    public String getDisplayName(Player player) {
        return CC.t(this.name);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", this.name);
        map.put("claims", this.claims.stream().map(Serializer::serializeClaim).collect(Collectors.toList()));
        map.put("teamType", this.type.toString());
        map.put("uniqueID", this.uniqueID.toString());
        map.put("leader", this.leader.toString());
        if (this.hq != null) {
            map.put("hq", Serializer.serializeLoc(this.hq));
        }
        return map;
    }

    public List<String> getTeamInfo(CommandSender sender) {
        return Arrays.asList(CC.LINE, ((sender instanceof Player) ? this.getDisplayName((Player) sender) : this.name) + " &3- &eHQ: &f" + this.getHQFormatted(), "&eThis is a system team.", CC.LINE);
    }

    public String getHQFormatted() {
        if (this.hq == null) {
            return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INFO.HQ_FORMAT.NONE");
        }
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INFO.HQ_FORMAT.SET").replaceAll("%world%", this.hq.getWorld().getName()).replaceAll("%x%", String.valueOf(Math.abs(this.hq.getBlockX()))).replaceAll("%y%", String.valueOf(Math.abs(this.hq.getBlockY()))).replaceAll("%z%", String.valueOf(Math.abs(this.hq.getBlockZ())));
    }
}