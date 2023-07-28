package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeWarpAbility extends Ability {
    private Map<UUID, TimeWarpData> timeWarps;
    private int delay;
    private int seconds;

    public TimeWarpAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Time Warp");
        this.timeWarps = new HashMap<>();
        this.seconds = this.getAbilitiesConfig().getInt("TIME_WARP.PEARL_DELAY");
        this.delay = this.getAbilitiesConfig().getInt("TIME_WARP.DELAY");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }
        Player player = Utils.getDamager(event.getEntity());
        if (player != null) {
            this.timeWarps.put(player.getUniqueId(), new TimeWarpData(player.getLocation(), this.seconds));
        }
    }

    @Override
    public void onClick(Player player) {
        if (this.hasCooldown(player)) {
            return;
        }
        if (this.cannotUse(player)) {
            return;
        }
        TimeWarpData data = this.timeWarps.remove(player.getUniqueId());
        if (data == null || data.getValidTill() < System.currentTimeMillis()) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.TIMEWARP.INVALID_PEARL").replaceAll("%seconds%", String.valueOf(this.seconds)));
            return;
        }
        this.takeItem(player);
        this.applyCooldown(player);
        Tasks.executeLater(this.getManager(), 20 * this.delay, () -> player.teleport(data.getLocation()));
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.TIMEWARP.USED")) {
            player.sendMessage(s.replaceAll("%seconds%", String.valueOf(this.delay)));
        }
    }

    private static class TimeWarpData {
        private long validTill;
        private Location location;

        public TimeWarpData(Location location, int validTill) {
            this.location = location;
            this.validTill = System.currentTimeMillis() + validTill * 1000L;
        }

        public Location getLocation() {
            return this.location;
        }

        public long getValidTill() {
            return this.validTill;
        }
    }
}
