package dev.razorni.hcfactions.events.koth;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.events.EventType;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.staff.StaffManager;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.cuboid.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
public class Koth extends Module<KothManager> {
    private long remaining;
    private Cuboid captureZone;
    private String color;
    private int pointsReward;
    private long minutes;
    private boolean active;
    private Player capturing;
    private String name;
    private List<Player> onCap;
    private EventType eventType;

    public Koth(KothManager manager, String name, String color, int pointsReward, long minutes) {
        super(manager);
        this.name = name;
        this.color = color;
        this.onCap = new ArrayList<Player>();
        this.eventType = EventType.KOTH;
        this.captureZone = null;
        this.capturing = null;
        this.pointsReward = pointsReward;
        this.active = false;
        this.minutes = 60000L * minutes;
        this.remaining = 0L;
    }

    public Koth(KothManager manager, Map<String, Object> map) {
        super(manager);
        this.name = (String) map.get("name");
        this.color = (String) map.get("color");
        this.pointsReward = Integer.parseInt((String) map.get("pointsReward"));
        this.active = Boolean.parseBoolean((String) map.get("active"));
        this.minutes = Long.parseLong((String) map.get("minutes"));
        this.onCap = new ArrayList<Player>();
        this.eventType = EventType.KOTH;
        this.captureZone = null;
        this.capturing = null;
        this.remaining = 0L;
        if (map.containsKey("captureZone")) {
            this.captureZone = Serializer.deserializeCuboid((String) map.get("captureZone"));
        }
    }

    public long getRemaining() {
        if (this.capturing == null) {
            this.remaining = System.currentTimeMillis() + this.minutes;
            return this.minutes;
        }
        long time = this.remaining - System.currentTimeMillis();
        if (time < 0L) {
            this.handleCapture();
            return 0L;
        }
        return time;
    }

    public void end() {
        this.capturing = null;
        this.active = false;
        this.remaining = 0L;
        this.onCap.clear();
        if (this.captureZone != null) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                this.getInstance().getWaypointManager().getKothWaypoint().remove(online, this.captureZone.getCenter(), lllllllllllllllllIIIlIlllllIIlll -> lllllllllllllllllIIIlIlllllIIlll.replaceAll("%name%", this.name));
            }
        }
    }

    public void checkZone(boolean b) {
        if (this.captureZone == null) {
            return;
        }
        for (int i = this.captureZone.getMinimumX(); i <= this.captureZone.getMaximumX(); ++i) {
            for (int f = this.captureZone.getMinimumZ(); f <= this.captureZone.getMaximumZ(); ++f) {
                if (b) {
                    this.getManager().getCaptureZones().remove(this.captureZone.getWorldName(), this.getManager().toLong(i, f));
                } else if (!this.getManager().getCaptureZones().contains(this.captureZone.getWorldName(), this.getManager().toLong(i, f))) {
                    this.getManager().getCaptureZones().put(this.captureZone.getWorldName(), this.getManager().toLong(i, f), this);
                }
            }
        }
    }

    private void handleCapture() {
        if (this.capturing == null) {
            this.remaining = System.currentTimeMillis() + this.minutes;
            return;
        }
        this.reward();
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(this.capturing.getUniqueId());
        List<String> lines = this.getLanguageConfig().getStringList("KOTH_EVENTS.BROADCAST_END");
        for (String s : lines) {
            Bukkit.getConsoleSender().sendMessage(s.replaceAll("%koth%", this.name).replaceAll("%team%", (team == null) ? "None" : team.getName()).replaceAll("%player%", this.capturing.getName()));
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            String teamName = (team == null) ? "None" : team.getDisplayName(online);
            String displayName = (team == null) ? this.capturing.getName() : team.getDisplayColor(online) + this.capturing.getName();
            for (String s : lines) {
                online.sendMessage(s.replaceAll("%koth%", this.name).replaceAll("%team%", teamName).replaceAll("%player%", displayName));
            }
        }
        this.end();
        this.save();
    }

    public void tick() {
        long i1 = this.getConfig().getInt("KOTHS_CONFIG.SEND_CAPTURING_MESSAGE");
        long i2 = this.getConfig().getInt("KOTHS_CONFIG.TEAM_SEND_CAPTURING_MESSAGE");
        long reaming = this.getRemaining() / 1000L;
        if (this.capturing == null && !this.onCap.isEmpty()) {
            Collections.shuffle(this.onCap);
            this.capturing = this.onCap.get(0);
        }
        if (this.capturing != null) {
            StaffManager manager = this.getInstance().getStaffManager();
            if (!this.capturing.isOnline() || this.capturing.isDead()) {
                this.onCap.remove(this.capturing);
                this.capturing = null;
            } else if (manager.isStaffEnabled(this.capturing) || manager.isVanished(this.capturing)) {
                this.onCap.remove(this.capturing);
                this.capturing = null;
            }
        }
        if (this.capturing != null && reaming % i2 == 0L && reaming > 5L) {
            PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(this.capturing.getUniqueId());
            if (team != null) {
                for (String s : this.getLanguageConfig().getStringList("KOTH_EVENTS.TEAM_CONTROLLING")) {
                    team.broadcast(s.replaceAll("%koth%", this.name));
                }
                return;
            }
        }
        if (this.capturing != null && reaming % i1 == 0L && reaming > 5L) {
            for (String s : this.getLanguageConfig().getStringList("KOTH_EVENTS.PLAYER_CONTROLLING")) {
                this.capturing.sendMessage(s.replaceAll("%koth%", this.name));
            }
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", this.name);
        map.put("color", this.color);
        map.put("pointsReward", String.valueOf(this.pointsReward));
        map.put("active", String.valueOf(this.active));
        map.put("minutes", String.valueOf(this.minutes));
        map.put("type", this.eventType.toString());
        if (this.captureZone != null) {
            map.put("captureZone", Serializer.serializeCuboid(this.captureZone));
        }
        return map;
    }

    public void save() {
        this.checkZone(false);
        this.getManager().getKoths().put(this.name, this);
        this.getEventsData().getValues().put(this.name, this.serialize());
        this.getEventsData().save();
    }

    public void start() {
        this.capturing = null;
        this.active = true;
        this.remaining = System.currentTimeMillis() + this.minutes;
        this.onCap.clear();
        if (this.captureZone != null) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                this.getInstance().getWaypointManager().getKothWaypoint().send(online, this.captureZone.getCenter(), lllllllllllllllllIIIlIllllIlllll -> lllllllllllllllllIIIlIllllIlllll.replaceAll("%name%", this.name));
            }
        }
    }

    public void delete() {
        this.checkZone(true);
        this.getManager().getKoths().remove(this.name);
        this.getEventsData().getValues().remove(this.name);
        this.getEventsData().save();
    }

    public void reward() {
        if (this.capturing == null) {
            return;
        }
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(this.capturing.getUniqueId());
        if (team != null) {
            team.setKothCaptures(team.getKothCaptures() + 1);
            team.setPoints(team.getPoints() + this.pointsReward);
            team.save();
            for (String s : this.getLanguageConfig().getStringList("KOTH_EVENTS.TEAM_RECEIVED_POINTS")) {
                team.broadcast(s.replaceAll("%points%", String.valueOf(this.pointsReward)));
            }
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HCF.getPlugin().getConfig().getString("KOTHS_CONFIG.COMMAND_ON_CAPTURE").replace("%player%", this.capturing.getName()));
    }
}
