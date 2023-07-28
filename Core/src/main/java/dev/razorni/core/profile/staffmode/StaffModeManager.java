package dev.razorni.core.profile.staffmode;

import com.lunarclient.bukkitapi.LunarClientAPI;
import dev.razorni.core.Core;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.builder.ActionBarBuilder;
import dev.razorni.core.util.builder.TitleBuilder;
import dev.razorni.core.util.bungee.BungeeListener;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class StaffModeManager implements Listener {

    private final Set<UUID> staffMode = new HashSet<>();
    private final static Map<UUID, Integer> bars = new HashMap<>();

    public StaffModeManager(Core plugin) {
    }

    public boolean isStaffMode(Player player) {
        return this.staffMode.contains(player.getUniqueId());
    }

    public void enableStaffMode(Player player) {
        this.staffMode.add(player.getUniqueId());
        bars.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                int count = BungeeListener.PLAYER_COUNT;
                AtomicInteger staff = new AtomicInteger(0);
                for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
                    Profile profile = Profile.getByUuid(onlineplayer.getUniqueId());
                    if (profile != null && profile.getActiveRank().hasPermission("core.staff")) {
                        staff.getAndIncrement();
                    }
                }
                double t = Math.min(Math.round(MinecraftServer.getServer().recentTps[1] * 10.0) / 10.0, 20.0);
                ChatColor c = (t >= 18.0) ? ChatColor.GREEN : ((t >= 13.0) ? ChatColor.YELLOW : ChatColor.RED);
                String tps = CC.translate(" &cTPS: &a*" + c + t);
                Profile profile = Profile.getByUuid(player.getUniqueId());
                if(profile == null) {
                    tps = "";
                } else {
                    if(!player.isOp()) {
                        tps = "";
                    }
                }
                ActionBarBuilder.sendActionBarMessage(player,
                        "&cPlayers: &f" + count + " &7┃ " +
                                "&cStaff: &f" + staff + " &7┃ " +
                                tps
                );
            }
        }.runTaskTimer(Core.getInstance(), 0L, 10L).getTaskId());
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.CREATIVE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        TitleBuilder title = new TitleBuilder("&c&lSTAFF", "&fEnabled", 20, 60, 20);
        title.send(player);
        LunarClientAPI.getInstance().giveAllStaffModules(player);
    }

    public void disableStaffMode(Player player) {
        this.staffMode.remove(player.getUniqueId());
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        Bukkit.getScheduler().cancelTask(bars.remove(player.getUniqueId()));
        ActionBarBuilder.sendRawActionBarMessage(player, "");
        LunarClientAPI.getInstance().disableAllStaffModules(player);
        TitleBuilder title = new TitleBuilder("&c&lSTAFF", "&fDisabled", 20, 60, 20);
        title.send(player);
    }

}
