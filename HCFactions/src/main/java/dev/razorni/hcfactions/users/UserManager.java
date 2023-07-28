package dev.razorni.hcfactions.users;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.users.listener.UserListener;
import dev.razorni.hcfactions.users.task.LeaderboardsTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UserManager extends Manager {

    private final List<User> topDeaths;
    private final List<User> topKillStreaks;
    private final List<User> topKills;
    private final List<User> topBalance;
    private final Map<UUID, User> users;
    private final List<User> topKDR;
    @Setter
    private boolean loaded;

    public UserManager(HCF plugin) {
        super(plugin);
        this.loaded = false;
        this.users = new ConcurrentHashMap<>();
        this.topKills = new ArrayList<>();
        this.topBalance = new ArrayList<>();
        this.topDeaths = new ArrayList<>();
        this.topKDR = new ArrayList<>();
        this.topKillStreaks = new ArrayList<>();
        new UserListener(this);
        new LeaderboardsTask(this);
    }

    public User getByUUID(UUID player) {
        return this.users.get(player);
    }

    public String getPrefix(Player player) {
        if (!this.getLunarConfig().getBoolean("LUNAR_PREFIXES.ENABLED")) {
            return null;
        }
        int kills = this.topKills.indexOf(this.getByUUID(player.getUniqueId()));
        switch (kills) {
            case 0: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.PLAYER.ONE");
            }
            case 1: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.PLAYER.TWO");
            }
            case 2: {
                return this.getLunarConfig().getString("LUNAR_PREFIXES.PLAYER.THREE");
            }
            default: {
                return null;
            }
        }
    }
}