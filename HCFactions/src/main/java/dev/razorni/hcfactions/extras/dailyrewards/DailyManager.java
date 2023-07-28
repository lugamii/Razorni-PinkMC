package dev.razorni.hcfactions.extras.dailyrewards;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DailyManager implements Listener {

    private final List<DailyData> dailyData;
    private FileConfiguration data;
    private File file;

    public DailyManager() {
        this.dailyData = new ArrayList<>();
        this.file = new File(HCF.getPlugin().getDataFolder(), "daily-settings.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        this.loadDailyData();
    }

    public void disable() {
        this.dailyData.clear();
    }

    private void loadDailyData() {
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        ConfigurationSection section = this.data.getConfigurationSection("daily");
        section.getKeys(false).forEach(key -> {
            DailyData daily = new DailyData();

            daily.setPermission(section.getString(key + ".permission"));
            daily.setReward(section.getStringList(key + ".reward"));

            this.dailyData.add(daily);
        });
    }

    private boolean hasCommand(String command) {
        return this.dailyData.stream().anyMatch(reclaim -> command.equalsIgnoreCase(reclaim.getCommand()));
    }

    private boolean hasDailyPermission(Player player) {
        return this.dailyData.stream().anyMatch(reclaim -> player.hasPermission(reclaim.getPermission()));
    }

    private DailyData getDaily(Player player) {
        return this.dailyData.stream().filter(reclaim -> player.hasPermission(reclaim.getPermission()))
                .findFirst().orElse(null);
    }

    public void performCommand(Player player) {
        if (!this.hasDailyPermission(player)) {
            return;
        }

        User user = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                reward(player);
            }
        }.runTask(HCF.getPlugin());
        user.setDailyTime(86400000L);
        user.save();
        player.updateInventory();
    }

    private void reward(Player player) {
        DailyData reclaim = this.getDaily(player);
        reclaim.getReward().forEach(reward -> Bukkit.dispatchCommand(Bukkit
                .getConsoleSender(), reward
                .replace("%color%", String.valueOf(HCF.getPlugin().getRankManager().getRankColor(player)))
                .replace("%player%", player.getName())));
    }


    @Getter
    @Setter
    private static class DailyData {

        private String command;
        private String permission;
        private List<String> reward;
    }
}
