package dev.razorni.hcfactions.deathban;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.deathban.listener.DeathbanListener;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.Logger;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class DeathbanManager extends Manager {

    private Location deathLocationSign;
    private Map<String, Long> deathbanTimes;
    private Location arenaSpawn;
    private Location killedBySign;

    public DeathbanManager(HCF plugin) {
        super(plugin);
        this.deathbanTimes = new HashMap<>();
        this.load();
        new DeathbanListener(this);
    }

    private void load() {
        for (String strings : this.getConfig().getStringList("DEATHBANS.TIMES")) {
            String[] deathbans = strings.split(", ");
            this.deathbanTimes.put("azurite.deathban." + deathbans[0].toLowerCase(), Integer.parseInt(deathbans[1]) * 60000L);
        }
        this.arenaSpawn = Serializer.deserializeLoc(this.getConfig().getString("LOCATIONS.DEATHBAN_ARENA.ARENA_SPAWN"));
        this.killedBySign = Serializer.deserializeLoc(this.getConfig().getString("LOCATIONS.DEATHBAN_ARENA.KILLED_BY_SIGN"));
        this.deathLocationSign = Serializer.deserializeLoc(this.getConfig().getString("LOCATIONS.DEATHBAN_ARENA.DEATH_LOC_SIGN"));
    }

    public void removeDeathban(Player player) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setDeathban(null);
        user.save();
        this.getInstance().getTimerManager().getCombatTimer().removeTimer(player);
        player.sendMessage(this.getLanguageConfig().getString("DEATHBAN_LISTENER.REVIVED"));
        Tasks.execute(this, () -> {
            player.teleport(new Location(Bukkit.getWorld("world"), 0.491, 70, 0.643));
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        });
    }

    public void save() {
        this.getConfig().set("LOCATIONS.DEATHBAN_ARENA.ARENA_SPAWN", Serializer.serializeLoc(this.arenaSpawn));
        this.getConfig().set("LOCATIONS.DEATHBAN_ARENA.KILLED_BY_SIGN", Serializer.serializeLoc(this.killedBySign));
        this.getConfig().set("LOCATIONS.DEATHBAN_ARENA.DEATH_LOC_SIGN", Serializer.serializeLoc(this.deathLocationSign));
        this.getConfig().save();
    }

    public void applyDeathban(Player player) {
        if (player.hasPermission("azurite.deathban.bypass")) {
            return;
        }
        EntityDamageEvent event = player.getLastDamageCause();
        String string = (player.getKiller() != null) ? player.getKiller().getName() : ((event == null) ? "Unknown" : event.getCause().toString());
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        user.setDeathban(new Deathban(this, player.getUniqueId(), this.getDeathbanTime(player), string, player.getLocation()));
    }

    public Deathban getDeathban(Player player) {
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        return user.getDeathban();
    }

    public void sendSignInfo(Player player) {
        Block deathLocation = this.deathLocationSign.getBlock();
        Block signLocation = this.killedBySign.getBlock();
        Deathban deathban = this.getDeathban(player);
        if (!(deathLocation.getState() instanceof Sign)) {
            Logger.error("Death location sign incorrect for deathban arena!");
            return;
        }
        if (!(signLocation.getState() instanceof Sign)) {
            Logger.error("Killed by sign incorrect for deathban arena!");
            return;
        }
        List<String> killedByLines = this.getConfig().getStringList("DEATHBANS.SIGNS_CONFIG.KILLED_BY");
        List<String> deathLocationLines = this.getConfig().getStringList("DEATHBANS.SIGNS_CONFIG.DEATH_LOCATION");
        killedByLines.replaceAll(s -> s.replaceAll("%reason%", deathban.getReason()));
        deathLocationLines.replaceAll(s -> s.replaceAll("%location%", Utils.formatLocation(deathban.getLocation())));
        player.sendSignChange(this.deathLocationSign, deathLocationLines.toArray(new String[4]));
        player.sendSignChange(this.killedBySign, killedByLines.toArray(new String[4]));
    }

    public boolean isDeathbanned(Player player) {
        return this.getDeathban(player) != null;
    }

    private long getDeathbanTime(Player player) {
        long deathbantime = this.getConfig().getInt("DEATHBANS.DEFAULT_TIME") * 60000L;
        for (Map.Entry<String, Long> death : this.deathbanTimes.entrySet()) {
            String deathbantimer = death.getKey();
            Long deathban = death.getValue();
            if (player.hasPermission(deathbantimer) && deathban < deathbantime) {
                deathbantime = deathban;
            }
        }
        return deathbantime;
    }
}
