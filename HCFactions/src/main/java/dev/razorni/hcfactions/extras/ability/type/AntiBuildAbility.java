package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class AntiBuildAbility extends Ability {
    private List<PotionEffect> effects;
    private int antiBuildTime;
    private List<Material> deniedInteract;
    private Map<UUID, Integer> hits;
    private int maxHits;
    private Cooldown antiBuild;

    public AntiBuildAbility(AbilityManager manager) {
        super(manager, AbilityUseType.HIT_PLAYER, "Anti Build");
        this.hits = new HashMap<>();
        this.deniedInteract = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.antiBuild = new Cooldown(manager);
        this.maxHits = this.getAbilitiesConfig().getInt(this.nameConfig + ".HITS_REQUIRED");
        this.antiBuildTime = this.getAbilitiesConfig().getInt(this.nameConfig + ".ANTI_BUILD_TIME");
        this.load();
    }

    private void load() {
        for (String s : this.getAbilitiesConfig().getStringList(String.valueOf(new StringBuilder().append(this.nameConfig).append(".DISABLED_INTERACT")))) {
            this.deniedInteract.add(ItemUtils.getMat(s));
        }
        for (String s : this.getAbilitiesConfig().getStringList(String.valueOf(new StringBuilder().append(this.nameConfig).append(".EFFECTS_DAMAGER")))) {
            this.effects.add(Serializer.getEffect(s));
        }
    }

    @EventHandler
    public void onBreak(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.antiBuild.hasCooldown(player) && this.getInstance().getTeamManager().canBuild(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.ANTI_BUILD.DENIED_BUILD").replaceAll("%seconds%", this.antiBuild.getRemaining(player)));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!this.antiBuild.hasCooldown(player)) {
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.deniedInteract.contains(block.getType())) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.ANTI_BUILD.DENIED_BUILD").replaceAll("%seconds%", this.antiBuild.getRemaining(player)));
        }
    }

    @EventHandler
    public void onBuild(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.antiBuild.hasCooldown(player) && this.getInstance().getTeamManager().canBuild(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.ANTI_BUILD.DENIED_BUILD").replaceAll("%seconds%", this.antiBuild.getRemaining(player)));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.hits.remove(player.getUniqueId());
        this.antiBuild.removeCooldown(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.hits.remove(player.getUniqueId());
    }

    @Override
    public void onHit(Player player, Player target) {
        UUID targetUUID = player.getUniqueId();
        if (this.cannotUse(player, target)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        if (!this.hits.containsKey(targetUUID)) {
            this.hits.put(targetUUID, 0);
        }
        int hit = this.hits.get(targetUUID) + 1;
        this.hits.put(targetUUID, hit);
        if (hit == this.maxHits) {
            this.hits.remove(player.getUniqueId());
            this.antiBuild.applyCooldown(target, this.antiBuildTime);
            this.takeItem(player);
            this.applyCooldown(player);
            for (PotionEffect effect : this.effects) {
                this.getInstance().getClassManager().addEffect(player, effect);
            }
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.ANTI_BUILD.USED")) {
                player.sendMessage(s.replaceAll("%player%", target.getName()).replaceAll("%seconds%", String.valueOf(this.antiBuildTime)));
            }
            for (String s : this.getLanguageConfig().getStringList("ABILITIES.ANTI_BUILD.BEEN_HIT")) {
                target.sendMessage(s.replaceAll("%player%", player.getName()).replaceAll("%seconds%", String.valueOf(this.antiBuildTime)));
            }
        }
    }
}
