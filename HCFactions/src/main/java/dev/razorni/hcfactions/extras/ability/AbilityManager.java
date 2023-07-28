package dev.razorni.hcfactions.extras.ability;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.ability.listener.AbilityListener;
import dev.razorni.hcfactions.extras.ability.type.*;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.extra.Cooldown;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AbilityManager extends Manager {
    private Cooldown globalCooldown;
    private Map<String, Ability> abilities;

    public AbilityManager(HCF plugin) {
        super(plugin);
        this.abilities = new HashMap<>();
        this.globalCooldown = new Cooldown(this);
        new AbilityListener(this);
        new SwitcherAbility(this);
        new AntiBuildAbility(this);
        new TimeWarpAbility(this);
        new PocketBardAbility(this);
        new InvisibilityAbility(this);
        new PortableArcherAbility(this);
        new LightningAbility(this);
        new RageBallAbility(this);
        new CraftingChaosAbility(this);
        new ComboAbility(this);
        new FocusModeAbility(this);
        new LuckyModeAbility(this);
        new NinjaAbility(this);
    }

    public Ability getAbility(String ability) {
        return this.abilities.get(ability.toUpperCase());
    }

    public String getStatus(Player player) {
        if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("10.")) {
            return ChatColor.RED + "▊▊▊▊▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("9.")) {
            return ChatColor.GREEN + "▊" + ChatColor.RED + "▊▊▊▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("8.")) {
            return ChatColor.GREEN + "▊" + ChatColor.RED + "▊▊▊▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("7.")) {
            return ChatColor.GREEN + "▊▊" + ChatColor.RED + "▊▊▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("6.")) {
            return ChatColor.GREEN + "▊▊" + ChatColor.RED + "▊▊▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("5.")) {
            return ChatColor.GREEN + "▊▊▊" + ChatColor.RED + "▊▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("4.")) {
            return ChatColor.GREEN + "▊▊▊▊" + ChatColor.RED + "▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("3.")) {
            return ChatColor.GREEN + "▊▊▊▊" + ChatColor.RED + "▊▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("2.")) {
            return ChatColor.GREEN + "▊▊▊▊▊" + ChatColor.RED + "▊▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("1.")) {
            return ChatColor.GREEN + "▊▊▊▊▊▊" + ChatColor.RED + "▊";
        } else if (HCF.getPlugin().getAbilityManager().getGlobalCooldown().getRemaining(player).contains("0.")) {
            return ChatColor.GREEN + "▊▊▊▊▊▊▊";
        }
        return null;
    }

}