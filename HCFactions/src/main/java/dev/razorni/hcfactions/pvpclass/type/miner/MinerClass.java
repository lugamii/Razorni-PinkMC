package dev.razorni.hcfactions.pvpclass.type.miner;

import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.utils.Serializer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter
public class MinerClass extends PvPClass {
    private final List<UUID> invisible;
    private final Map<Integer, PotionEffect> minerEffects;
    private final int minerInvisibilityLevel;

    public MinerClass(PvPClassManager manager) {
        super(manager, "Miner");
        this.minerEffects = new HashMap<>();
        this.invisible = new ArrayList<>();
        this.minerInvisibilityLevel = this.getClassesConfig().getInt("MINER_CLASS.MINER_INVISIBILITY");
        this.load();
    }

    private void checkInvis(Player player) {
        int y = player.getLocation().getBlockY();
        if (!this.invisible.contains(player.getUniqueId()) && y <= this.minerInvisibilityLevel) {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
            player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.MINER_CLASS.INVIS_ENABLED"));
            this.invisible.add(player.getUniqueId());
        } else if (this.invisible.contains(player.getUniqueId()) && y > this.minerInvisibilityLevel) {
            if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                return;
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.MINER_CLASS.INVIS_DISABLED"));
            this.invisible.remove(player.getUniqueId());
        }
    }

    @Override
    public void load() {
        this.getClassesConfig().getConfigurationSection("MINER_CLASS.MINER_EFFECTS").getKeys(false).forEach(s -> {
            Integer i = Integer.valueOf(s);
            String effect = this.getClassesConfig().getString("MINER_CLASS.MINER_EFFECTS." + s);
            this.minerEffects.put(i, Serializer.getEffect(effect));
        });
    }

    @Override
    public void handleUnequip(Player player) {
        if (this.invisible.contains(player.getUniqueId())) {
            this.invisible.remove(player.getUniqueId());
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    @Override
    public void handleEquip(Player player) {
        this.checkInvis(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getBlockY() == event.getTo().getBlockY()) {
            return;
        }
        if (!this.players.contains(player.getUniqueId())) {
            return;
        }
        this.checkInvis(player);
    }

    @Override
    public void removeEffects(Player player) {
        super.removeEffects(player);
        int diamonds = this.getInstance().getUserManager().getByUUID(player.getUniqueId()).getDiamonds();
        for (Integer i : this.minerEffects.keySet()) {
            if (i > diamonds) {
                continue;
            }
            player.removePotionEffect(this.minerEffects.get(i).getType());
        }
    }

    @Override
    public void addEffects(Player player) {
        super.addEffects(player);
        int diamonds = this.getInstance().getUserManager().getByUUID(player.getUniqueId()).getDiamonds();
        for (Integer i : this.minerEffects.keySet()) {
            if (i > diamonds) {
                continue;
            }
            this.getManager().addEffect(player, this.minerEffects.get(i));
        }
    }
}
