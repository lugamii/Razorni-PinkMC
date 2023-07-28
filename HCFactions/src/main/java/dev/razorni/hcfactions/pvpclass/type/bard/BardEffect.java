package dev.razorni.hcfactions.pvpclass.type.bard;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import dev.razorni.hcfactions.utils.Serializer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

@Getter
@Setter
public class BardEffect extends Module<PvPClassManager> {
    private PotionEffect effect;
    private int energyRequired;
    private boolean effectFriendlies;
    private boolean effectEnemies;
    private boolean clickable;
    private int bardDistance;
    private boolean effectSelf;

    public BardEffect(PvPClassManager manager, Map<String, Object> map, boolean clickeable) {
        super(manager);
        this.effect = Serializer.getEffect((String) map.get("EFFECT"));
        this.bardDistance = this.getClassesConfig().getInt("BARD_CLASS.BARD_DISTANCE");
        this.effectFriendlies = (boolean) map.get("EFFECT_FRIENDLIES");
        this.effectSelf = (boolean) map.get("EFFECT_SELF");
        this.effectEnemies = (boolean) map.get("EFFECT_ENEMIES");
        this.clickable = clickeable;
        if (clickeable) {
            this.energyRequired = (int) map.get("ENERGY_REQUIRED");
        }
    }

    public BardEffect(PvPClassManager manager, boolean clickeable, PotionEffect effect) {
        super(manager);
        this.clickable = clickeable;
        this.effect = effect;
        this.bardDistance = this.getClassesConfig().getInt("BARD_CLASS.BARD_DISTANCE");
        this.energyRequired = 0;
        this.effectFriendlies = true;
        this.effectSelf = true;
        this.effectEnemies = false;
    }

    public void applyEffect(Player player) {
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        if (this.clickable) {
            if (this.energyRequired == 0) {
                player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.USED_EFFECT_NO_ENERGY").replaceAll("%effect%", this.effect.getType().getName()));
            } else {
                player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.USED_EFFECT").replaceAll("%effect%", this.effect.getType().getName()).replaceAll("%energy%", String.valueOf(this.energyRequired)));
            }
        }
        if (this.effectSelf) {
            this.getManager().addEffect(player, this.effect);
        }
        int distance = this.bardDistance / 2;
        for (Entity entity : player.getNearbyEntities(this.bardDistance, distance, this.bardDistance)) {
            if (!(entity instanceof Player)) {
                continue;
            }
            if (entity == player) {
                continue;
            }
            Player target = (Player) entity;
            Team targetTeam = this.getInstance().getTeamManager().getClaimManager().getTeam(target.getLocation());
            if (targetTeam instanceof SafezoneTeam) {
                continue;
            }
            if (this.effectFriendlies && team != null && team.getPlayers().contains(target.getUniqueId())) {
                this.getManager().addEffect(target, this.effect);
                if (this.clickable) {
                    target.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.TEAM_EFFECT").replaceAll("%player%", player.getName()).replaceAll("%effect%", this.effect.getType().getName()));
                }
            }
            if (!this.effectEnemies) {
                continue;
            }
            if (team != null) {
                if (team.isAlly(target)) {
                    continue;
                }
                if (team.getPlayers().contains(target.getUniqueId())) {
                    continue;
                }
            }
            this.getManager().addEffect(target, this.effect);
        }
    }
}