package dev.razorni.hcfactions.users.task;

import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.users.UserManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardsTask extends BukkitRunnable {
    private final UserManager manager;

    public LeaderboardsTask(UserManager manager) {
        this.manager = manager;
        this.start();
    }

    public void start() {
        this.runTaskTimerAsynchronously(this.manager.getInstance(), 0L, 600L);
    }

    public void run() {
        List<User> users = new ArrayList<>(this.manager.getUsers().values());
        users.sort(Comparator.comparingInt(User::getKills).reversed());
        if (!users.equals(this.manager.getTopKills())) {
            this.manager.getInstance().getNametagManager().update();
        }
        this.manager.getTopKills().clear();
        this.manager.getTopKills().addAll(users.stream().limit(20L).collect(Collectors.toList()));
        users.sort(Comparator.comparingInt(User::getDeaths).reversed());
        this.manager.getTopDeaths().clear();
        this.manager.getTopDeaths().addAll(users.stream().limit(20L).collect(Collectors.toList()));
        users.sort(Comparator.comparingInt(User::getBalance).reversed());
        this.manager.getTopBalance().clear();
        this.manager.getTopBalance().addAll(users.stream().limit(20L).collect(Collectors.toList()));
        users.sort(Comparator.comparingInt(User::getKillstreak).reversed());
        this.manager.getTopKillStreaks().clear();
        this.manager.getTopKillStreaks().addAll(users.stream().limit(20L).collect(Collectors.toList()));
        users.sort(Comparator.comparingDouble(User::getKDR).reversed());
        this.manager.getTopKDR().clear();
        this.manager.getTopKDR().addAll(users.stream().limit(20L).collect(Collectors.toList()));
        users.clear();
    }
}
