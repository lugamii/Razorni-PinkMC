package dev.razorni.hcfactions.events.eotw;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.utils.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class EotwHandler {
    public static final int BORDER_DECREASE_MINIMUM = 100;
    public static final int BORDER_DECREASE_AMOUNT = 200;

    public static final long BORDER_DECREASE_TIME_MILLIS = TimeUnit.MINUTES.toMillis(15L);
    public static final int BORDER_DECREASE_TIME_SECONDS = (int) TimeUnit.MILLISECONDS.toSeconds(BORDER_DECREASE_TIME_MILLIS);
    public static final int BORDER_DECREASE_TIME_SECONDS_HALVED = BORDER_DECREASE_TIME_SECONDS / 2;
    public static final String BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS, true, true);
    public static final String BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS / 2, true, true);

    public static final long EOTW_WARMUP_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(HCF.getPlugin().getConfig().getInt("EOTW.START-TIME"));
    public static final int EOTW_WARMUP_WAIT_SECONDS = (int) (TimeUnit.MILLISECONDS.toSeconds(EOTW_WARMUP_WAIT_MILLIS));

    private static final long EOTW_CAPPABLE_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(HCF.getPlugin().getConfig().getInt("EOTW.CAP-TIME"));
    private static final int WITHER_INTERVAL_SECONDS = 10;

    private EotwRunnable runnable;
    private final HCF plugin;

    public EotwHandler(HCF plugin) {
        this.plugin = plugin;
    }

    public EotwRunnable getRunnable() {
        return runnable;
    }


    /**
     * Checks if the map is currently in 'End of the World' mode.
     *
     * @return true if the map is the end of world
     */
    public boolean isEndOfTheWorld() {
        return isEndOfTheWorld(true);
    }

    /**
     * Checks if the map is currently in 'End of the World' mode.
     *
     * @param ignoreWarmup if the warmup stage is ignored
     * @return true if the map is the end of world
     */
    public boolean isEndOfTheWorld(boolean ignoreWarmup) {
        return runnable != null && (!ignoreWarmup || runnable.getElapsedMilliseconds() > 0);
    }

    /**
     * Sets if the server is currently in 'End of the World' mode.
     *
     * @param yes the value to set
     */
    public void setEndOfTheWorld(boolean yes) {
        // Don't unnecessary edit task.
        if (yes == isEndOfTheWorld(false)) {
            return;
        }

        if (yes) {
            runnable = new EotwRunnable();
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth start EOTW");
                    }
            }.runTaskLater(plugin, 40 * 60);
            runnable.runTaskTimer(plugin, 20L, 20L);
        } else {
            if (runnable != null) {
                runnable.cancel();
                runnable = null;
            }
        }
    }

    public static class EotwRunnable extends BukkitRunnable {


        // The set of players that should be given the Wither potion effect because they are outside of the border.

        private final long startStamp;
        private int elapsedSeconds;

        public EotwRunnable() {
            this.startStamp = System.currentTimeMillis() + EOTW_WARMUP_WAIT_MILLIS;
            this.elapsedSeconds = -EOTW_WARMUP_WAIT_SECONDS;
        }


        public long getMillisUntilStarting() {
            long difference = System.currentTimeMillis() - startStamp;
            return difference > 0L ? -1L : Math.abs(difference);
        }

        public long getMillisUntilCappable() {
            return EOTW_CAPPABLE_WAIT_MILLIS - getElapsedMilliseconds();
        }

        public long getElapsedMilliseconds() {
            return System.currentTimeMillis() - startStamp;
        }

        @Override
        public void run() {
            elapsedSeconds++;

            if (elapsedSeconds == 0) {
                for (PlayerTeam faction : HCF.getPlugin().getTeamManager().getPlayerTeams().values()) {
                    if (faction != null) {
                        faction.setDtr(-99999999);
                        HCF.getPlugin().getTimerManager().getTeamRegenTimer().applyTimer(faction, TimeUnit.DAYS.toMillis(1L));
                        faction.getClaims().clear();
                    }
                }

                Bukkit.broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + " " + ChatColor.DARK_RED + ChatColor.BOLD + "EOTW");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + " " + ChatColor.RED + ChatColor.BOLD + "EOTW has commenced.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588\u2588" + " " + ChatColor.RED + "Spawn has been flagged as Deathban.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + " " + ChatColor.RED + "The border has been shrunken to 500.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + " " + ChatColor.RED + "All factions are now raidable.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player p : HCF.getPlugin().getServer().getOnlinePlayers()) {
                            if (p.getLocation().distance(new Location(Bukkit.getWorld("world"), 0.491, 70, 0.643)) > 500) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 4, 1));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 4, 1));
                            }
                        }
                    }
                }.runTaskTimer(HCF.getPlugin(), 20, 20);


                for (Player p : HCF.getPlugin().getServer().getOnlinePlayers()) {
                    if (p.getWorld().getEnvironment() == World.Environment.NETHER || p.getWorld().getEnvironment() == World.Environment.THE_END) {
                        p.teleport(new Location(Bukkit.getWorld("world"), 0.491, 70, 0.643));
                        p.sendMessage(CC.chat("&cYou have been teleported to spawn due to being in nether or end whilst EOTW."));
                    }
                }


            } else if (elapsedSeconds >= EOTW_CAPPABLE_WAIT_MILLIS) {

            }

        }
    }

}