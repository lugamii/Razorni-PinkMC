package dev.razorni.hcfactions.teams.type;

import cc.invictusgames.ilib.hologram.updating.UpdatingHologram;
import com.google.common.collect.Maps;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.workload.TeamWorkload;
import dev.razorni.hcfactions.extras.workload.type.TeamWorkdLoadType;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.TeamManager;
import dev.razorni.hcfactions.teams.enums.TeamType;
import dev.razorni.hcfactions.teams.player.Member;
import dev.razorni.hcfactions.teams.player.Role;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.Formatter;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerTeam extends Team {
    private List<UUID> allyRequests;
    private List<UUID> allies;
    private List<UUID> players;
    private boolean minuteRegen;
    private int balance;
    private Location rallyPoint;
    private Map<UUID, Map<String, Double>> teamViewer;
    private boolean open;
    private UUID focus;
    private int kills;
    @Setter
    @Getter
    private UpdatingHologram regenBaseHologram;
    @Getter
    private Map<TeamWorkdLoadType, TeamWorkload> workloadRunnables = Maps.newHashMap();
    @Getter @Setter private boolean useBase = false;
    private double dtr;
    private int deaths;
    private int kothCaptures;
    private int points;
    private List<Member> members;
    private List<UUID> invitedPlayers;

    public PlayerTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, true, TeamType.PLAYER);
        this.allyRequests = new ArrayList<>();
        this.teamViewer = new HashMap<>();
        this.invitedPlayers = new ArrayList<>();
        this.players = Utils.createList(map.get("players"), String.class).stream().map(UUID::fromString).collect(Collectors.toList());
        this.allies = Utils.createList(map.get("allies"), String.class).stream().map(UUID::fromString).collect(Collectors.toList());
        this.members = Utils.createList(map.get("members"), String.class).stream().map(Serializer::deserializeMember).collect(Collectors.toList());
        this.balance = Integer.parseInt((String) map.get("balance"));
        this.kothCaptures = Integer.parseInt((String) map.get("kothCaptures"));
        this.points = Integer.parseInt((String) map.get("points"));
        this.kills = Integer.parseInt((String) map.get("kills"));
        this.deaths = Integer.parseInt((String) map.get("deaths"));
        this.dtr = Double.parseDouble((String) map.get("dtr"));
        this.minuteRegen = Boolean.parseBoolean((String) map.get("minuteRegen"));
        this.open = Boolean.parseBoolean((String) map.get("open"));
        if (Long.parseLong((String) map.get("regen")) > 0L) {
            this.getInstance().getTimerManager().getTeamRegenTimer().applyTimer(this, Long.parseLong((String) map.get("regen")));
        }
        this.getInstance().getTimerManager().getTeamRegenTimer().startMinuteRegen(this);
        if (map.containsKey("focus")) {
            this.focus = UUID.fromString((String) map.get("focus"));
        }
        if (map.containsKey("rallyPoint")) {
            this.rallyPoint = Serializer.deserializeLoc((String) map.get("rallyPoint"));
        }
        for (UUID player : this.getPlayers()) {
            this.getManager().getPlayerTeams().put(player, this);
        }
    }

    public PlayerTeam(TeamManager manager, String name, UUID uuid) {
        super(manager, name, uuid, true, TeamType.PLAYER);
        this.players = new ArrayList<>();
        this.invitedPlayers = new ArrayList<>();
        this.allyRequests = new ArrayList<>();
        this.allies = new ArrayList<>();
        this.members = new ArrayList<>();
        this.balance = 0;
        this.kothCaptures = 0;
        this.points = 0;
        this.kills = 0;
        this.deaths = 0;
        this.dtr = 1.1;
        this.focus = null;
        this.rallyPoint = null;
        this.minuteRegen = false;
        this.open = false;
    }

    public boolean isRaidable() {
        return this.getDtr() < 0.0;
    }

    @Override
    public void delete() {
        super.delete();
        this.getManager().getTeamSorting().remove(this);
        for (UUID allie : this.allies) {
            this.getManager().getPlayerTeam(allie).getAllies().remove(this.uniqueID);
        }
        for (UUID player : this.players) {
            this.getManager().getPlayerTeams().remove(player);
        }
    }

    public List<Member> getOnlineMembers() {
        List<Member> members = new ArrayList<>();
        for (Player member : this.getOnlinePlayers()) {
            members.add(this.getMember(member.getUniqueId()));
        }
        return members;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("players", this.players.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("members", this.members.stream().map(Serializer::serializeMember).collect(Collectors.toList()));
        map.put("allies", this.allies.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("balance", this.balance + "");
        map.put("kothCaptures", this.kothCaptures + "");
        map.put("points", this.points + "");
        map.put("kills", this.kills + "");
        map.put("deaths", this.deaths + "");
        map.put("dtr", this.dtr + "");
        map.put("regen", this.getRegen() + "");
        map.put("minuteRegen", this.minuteRegen + "");
        if (this.focus != null) {
            map.put("focus", this.focus.toString());
        }
        if (this.rallyPoint != null) {
            map.put("rallyPoint", Serializer.serializeLoc(this.rallyPoint));
        }
        return map;
    }

    private String makeAllyNice(UUID uuid) {
        PlayerTeam team = this.getManager().getPlayerTeam(uuid);
        return this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INFO.ALLY_FORMAT").replaceAll("%team%", team.getName()).replaceAll("%online%", String.valueOf(team.getOnlinePlayers().size())).replaceAll("%max-online%", String.valueOf(team.getPlayers().size()));
    }

    public int getOnlinePlayersSize() {
        return this.getOnlinePlayers().size();
    }

    public void broadcast(String... input) {
        for (Player online : this.getOnlinePlayers()) {
            for (String s : input) {
                online.sendMessage(CC.t(s));
            }
        }
    }

    public String getDtrColor() {
        if (this.isRaidable()) {
            return this.getTeamConfig().getString("TEAM_DTR.COLOR.RAIDABLE");
        }
        if (this.dtr <= this.getTeamConfig().getDouble("TEAM_DTR.LOW_DTR")) {
            return this.getTeamConfig().getString("TEAM_DTR.COLOR.LOW_DTR");
        }
        return this.getTeamConfig().getString("TEAM_DTR.COLOR.NORMAL");
    }


    public PlayerTeam getFocusedTeam() {
        return this.getManager().getPlayerTeam(this.focus);
    }

    @Override
    public List<String> getTeamInfo(CommandSender sender) {
        Player player;
        List<String> coLeaders = this.members.stream().filter(m -> m.getRole() == Role.CO_LEADER).map(m -> this.makeMemberNice(m.getUniqueID())).collect(Collectors.toList());
        List<String> captains = this.members.stream().filter(m -> m.getRole() == Role.CAPTAIN).map(m -> this.makeMemberNice(m.getUniqueID())).collect(Collectors.toList());
        List<String> members = this.members.stream().filter(m -> m.getRole() == Role.MEMBER).map(m -> this.makeMemberNice(m.getUniqueID())).collect(Collectors.toList());
        List<String> allies = this.allies.stream().map(this::makeAllyNice).collect(Collectors.toList());
        if (sender instanceof Player && (this.isAlly(player = (Player) sender) || this.players.contains(player.getUniqueId()))) {
            List<String> lines = this.getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_INFO.FRIENDLIES_FORMAT");
            lines.removeIf(s -> {
                if (s.contains("%co-leaders%") && coLeaders.isEmpty()) {
                    return true;
                }
                if (s.contains("%captains%") && captains.isEmpty()) {
                    return true;
                }
                if (s.contains("%members%") && members.isEmpty()) {
                    return true;
                }
                if (s.contains("%allies%") && allies.isEmpty()) {
                    return true;
                }
                return s.contains("%regen%") && !this.hasRegen();
            });
            lines.replaceAll(s -> s.replaceAll("%balance%", String.valueOf(this.balance)).replaceAll("%name%", this.name).replaceAll("%online%", String.valueOf(this.getOnlinePlayers().size())).replaceAll("%max-online%", String.valueOf(this.getPlayers().size())).replaceAll("%hq%", this.getHQFormatted()).replaceAll("%leader%", this.makeMemberNice(this.leader)).replaceAll("%allies%", StringUtils.join(allies, ", ")).replaceAll("%co-leaders%", StringUtils.join(coLeaders, ", ")).replaceAll("%captains%", StringUtils.join(captains, ", ")).replaceAll("%members%", StringUtils.join(members, ", ")).replaceAll("%points%", String.valueOf(this.points)).replaceAll("%kothCaptures%", String.valueOf(this.kothCaptures)).replaceAll("%balance%", String.valueOf(this.balance)).replaceAll("%dtr%", this.getDtrString()).replaceAll("%dtr-color%", this.getDtrColor()).replaceAll("%dtr-symbol%", this.getDtrSymbol()).replaceAll("%regen%", Formatter.formatDetailed(this.getRegen())));
            return lines;
        }
        List<String> lines = this.getLanguageConfig().getStringList("TEAM_COMMAND.TEAM_INFO.FORMAT");
        lines.removeIf(s -> {
            if (s.contains("%co-leaders%") && coLeaders.isEmpty()) {
                return true;
            }
            if (s.contains("%captains%") && captains.isEmpty()) {
                return true;
            }
            if (s.contains("%members%") && members.isEmpty()) {
                return true;
            }
            if (s.contains("%allies%") && allies.isEmpty()) {
                return true;
            }
            return s.contains("%regen%") && !this.hasRegen();
        });
        lines.replaceAll(s -> s.replaceAll("%balance%", String.valueOf(this.balance)).replaceAll("%name%", this.name).replaceAll("%online%", String.valueOf(this.getOnlinePlayers().size())).replaceAll("%max-online%", String.valueOf(this.getPlayers().size())).replaceAll("%hq%", this.getHQFormatted()).replaceAll("%leader%", this.makeMemberNice(this.leader)).replaceAll("%allies%", StringUtils.join(allies, ", ")).replaceAll("%co-leaders%", StringUtils.join(coLeaders, ", ")).replaceAll("%captains%", StringUtils.join(captains, ", ")).replaceAll("%members%", StringUtils.join(members, ", ")).replaceAll("%points%", String.valueOf(this.points)).replaceAll("%kothCaptures%", String.valueOf(this.kothCaptures)).replaceAll("%balance%", String.valueOf(this.balance)).replaceAll("%dtr%", this.getDtrString()).replaceAll("%dtr-color%", this.getDtrColor()).replaceAll("%dtr-symbol%", this.getDtrSymbol()).replaceAll("%regen%", Formatter.formatDetailed(this.getRegen())));
        return lines;
    }

    public boolean isFocused(Player player) {
        PlayerTeam team = this.getManager().getByPlayer(player.getUniqueId());
        return this.focus != null && team != null && team.getUniqueID() == this.focus;
    }

    public String getTeamPosition() {
        if (!this.getLunarConfig().getBoolean("LUNAR_PREFIXES.ENABLED")) {
            return null;
        }
        int pos = this.getManager().getTeamSorting().getTeamTop().stream().limit(3L).collect(Collectors.toList()).indexOf(this);
        switch (pos) {
            case 0: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.ONE");
            }
            case 1: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.TWO");
            }
            case 2: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.THREE");
            }
            default: {
                return null;
            }
        }
    }

    public String getTeamPosBracketsLeft() {
        if (!this.getLunarConfig().getBoolean("LUNAR_PREFIXES.ENABLED")) {
            return null;
        }
        int pos = this.getManager().getTeamSorting().getTeamTop().stream().limit(3L).collect(Collectors.toList()).indexOf(this);
        switch (pos) {
            case 0: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.BRACKETS-ONE.LEFT");
            }
            case 1: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.BRACKETS-TWO.LEFT");
            }
            case 2: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.BRACKETS-THREE.LEFT");
            }
            default: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.DEFAULT-BRACKETS.LEFT");
            }
        }
    }

    public String getTeamPosBracketsRight() {
        if (!this.getLunarConfig().getBoolean("LUNAR_PREFIXES.ENABLED")) {
            return null;
        }
        int pos = this.getManager().getTeamSorting().getTeamTop().stream().limit(3L).collect(Collectors.toList()).indexOf(this);
        switch (pos) {
            case 0: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.BRACKETS-ONE.RIGHT");
            }
            case 1: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.BRACKETS-TWO.RIGHT");
            }
            case 2: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.BRACKETS-THREE.RIGHT");
            }
            default: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.TEAMS.DEFAULT-BRACKETS.RIGHT");
            }
        }
    }

    @Override
    public String getDisplayName(Player player) {
        return this.getDisplayColor(player) + super.getDisplayName(player);
    }

    public String getDtrString() {
        return Formatter.formatDtr(this.dtr);
    }

    @Override
    public void save() {
        super.save();
        for (UUID player : this.players) {
            this.getManager().getPlayerTeams().put(player, this);
        }
    }

    public void broadcastAlly(String... input) {
        if (this.allies.isEmpty()) {
            return;
        }
        for (UUID allie : this.allies) {
            PlayerTeam team = this.getManager().getPlayerTeam(allie);
            if (team.getOnlinePlayers().isEmpty()) continue;
            for (Player teamOnline : team.getOnlinePlayers()) {
                for (String s : input) {
                    teamOnline.sendMessage(CC.t(s));
                }
            }
        }
    }

    public boolean hasRegen() {
        return this.getInstance().getTimerManager().getTeamRegenTimer().hasTimer(this);
    }

    public void setDtr(double dtr) {
        if (dtr > this.getMaxDtr()) {
            this.dtr = this.getMaxDtr();
        } else {
            this.dtr = Math.max(dtr, -0.9);
        }
        this.getInstance().getNametagManager().update();
    }

    public boolean checkRole(Player player, Role role) {
        return this.getMember(player.getUniqueId()).getRole().ordinal() >= role.ordinal();
    }

    public boolean isAlly(Player player) {
        PlayerTeam team = this.getManager().getByPlayer(player.getUniqueId());
        return team != null && this.allies.contains(team.getUniqueID());
    }

    public boolean isMember(final UUID check) {
        return this.members.contains(check);
    }

    public long getRegen() {
        return this.getInstance().getTimerManager().getTeamRegenTimer().getRemaining(this);
    }

    public Member getMember(UUID uuid) {
        for (Member member : this.members) {
            if (member.getUniqueID().equals(uuid)) {
                return member;
            }
        }
        return null;
    }

    public double getMaxDtr() {
        if (this.getPlayers().size() == 1) {
            return this.getTeamConfig().getDouble("TEAM_DTR.SOLO_DTR");
        }
        double dtrPerPlayer = this.players.size() * this.getTeamConfig().getDouble("TEAM_DTR.PER_PLAYER");
        double maxDtr = this.getTeamConfig().getDouble("TEAM_DTR.MAX_DTR");
        return Math.min(dtrPerPlayer, maxDtr);
    }

    public List<Player> getOnlinePlayers() {
        List<Player> toReturn = new ArrayList<>();
        for (UUID uuid : this.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                toReturn.add(player);
            }
        }
        return toReturn;
    }

    private String makeMemberNice(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        User user = this.getInstance().getUserManager().getByUUID(uuid);
        return player.isOnline() ? this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INFO.MEMBER_FORMAT.ONLINE").replaceAll("%player%", player.getName()).replaceAll("%kills%", String.valueOf(user.getKills())) : this.getLanguageConfig().getString("TEAM_COMMAND.TEAM_INFO.MEMBER_FORMAT.OFFLINE").replaceAll("%player%", player.getName()).replaceAll("%kills%", String.valueOf(user.getKills()));
    }

    public String getDtrSymbol() {
        if (this.minuteRegen) {
            return this.getTeamConfig().getString("TEAM_DTR.SYMBOL.REGENERATING");
        }
        if (this.hasRegen()) {
            return this.getTeamConfig().getString("TEAM_DTR.SYMBOL.FREEZE");
        }
        return this.getTeamConfig().getString("TEAM_DTR.SYMBOL.NORMAL");
    }

    public String getDisplayColor(Player player) {
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (this.isAlly(player)) {
            return Config.RELATION_ALLIED;
        }
        if (team != null && team.getFocus() == this.uniqueID) {
            return Config.RELATION_FOCUSED;
        }
        return this.getPlayers().contains(player.getUniqueId()) ? Config.RELATION_TEAMMATE : Config.RELATION_ENEMY;
    }
}