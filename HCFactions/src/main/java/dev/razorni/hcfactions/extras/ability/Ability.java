package dev.razorni.hcfactions.extras.ability;

import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.CitadelTeam;
import dev.razorni.hcfactions.teams.type.EventTeam;
import dev.razorni.hcfactions.timers.listeners.playertimers.AbilityTimer;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@Setter
public abstract class Ability extends Module<AbilityManager> {
    protected ItemStack item;
    protected AbilityTimer abilityCooldown;
    protected boolean enabled;
    protected String nameConfig;
    protected String displayName;
    protected AbilityUseType useType;
    protected String name;

    public Ability(AbilityManager manager, AbilityUseType userType, String name) {
        super(manager);
        this.name = name;
        this.useType = userType;
        this.nameConfig = name.replace(" ", "_").toUpperCase();
        this.displayName = this.getAbilitiesConfig().getString(this.nameConfig + ".DISPLAY_NAME");
        this.abilityCooldown = new AbilityTimer(this.getInstance().getTimerManager(), this, "PLAYER_TIMERS.ABILITIES");
        this.item = this.loadItem();
        this.enabled = this.getAbilitiesConfig().getBoolean(this.nameConfig + ".ENABLED");
        manager.getAbilities().put(name.toUpperCase().replace(" ", ""), this);
    }

    public ItemStack item() {
        return new ItemBuilder(this.getItem()).data1((short) this.getAbilitiesConfig().getInt(this.nameConfig + ".DATA")).setName(CC.translate(this.getDisplayName())).setLore(CC.translate(this.getAbilitiesConfig().getStringList(this.nameConfig + ".LORE"))).toItemStack();
    }

    public boolean hasAbilityInHand(Player player) {
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack == null) {
            return false;
        }
        if (!stack.hasItemMeta()) {
            return false;
        }
        if (!stack.getItemMeta().hasDisplayName()) {
            return false;
        }
        if (!stack.getItemMeta().hasLore()) {
            return false;
        }
        ItemMeta stackMeta = stack.getItemMeta();
        ItemMeta itemMeta = this.item.getItemMeta();
        return stackMeta.getDisplayName().equals(itemMeta.getDisplayName()) && stackMeta.getLore().equals(itemMeta.getLore());
    }

    public void applyCooldown(Player player) {
        if (this.getAbilitiesConfig().getBoolean("GLOBAL_ABILITY.ENABLED")) {
            this.getManager().getGlobalCooldown().applyCooldown(player, this.getAbilitiesConfig().getInt("GLOBAL_ABILITY.COOLDOWN"));
        }
        this.abilityCooldown.applyTimer(player);
    }

    public void onClick(Player player) {
    }

    private ItemStack loadItem() {
        Material material = ItemUtils.getMat(this.getAbilitiesConfig().getString(this.nameConfig + ".MATERIAL"));
        ItemBuilder builder = new ItemBuilder(material);
        builder.setName(this.getAbilitiesConfig().getString(this.nameConfig + ".DISPLAY_NAME"));
        builder.setLore(this.getAbilitiesConfig().getStringList(this.nameConfig + ".LORE"));
        builder.data(this.getManager(), (short) this.getAbilitiesConfig().getInt(this.nameConfig + ".DATA"));
        if (this.getAbilitiesConfig().contains(this.nameConfig + ".DURABILITY")) {
            short data = (short) this.getAbilitiesConfig().getInt(this.nameConfig + ".DURABILITY");
            builder.setDurability(this.getManager(), (short) (material.getMaxDurability() - data));
        }
        if (this.getAbilitiesConfig().contains(this.nameConfig + ".ENCHANTS")) {
            for (String s : this.getAbilitiesConfig().getStringList(this.nameConfig + ".ENCHANTS")) {
                String[] enchants = s.split(", ");
                builder.addUnsafeEnchantment(Enchantment.getByName(enchants[0]), Integer.parseInt(enchants[1]));
            }
        }
        return builder.toItemStack();
    }

    public boolean cannotHit(Player player, Player target) {
        return this.getInstance().getGlitchListener().getHitCooldown().hasCooldown(player) || !this.getInstance().getTeamManager().canHit(player, target, false);
    }

    public void onHit(Player player, Player target) {
    }

    public boolean hasCooldown(Player player) {
        if (this.getManager().getGlobalCooldown().hasCooldown(player)) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.GLOBAL_COOLDOWN").replaceAll("%time%", this.getManager().getGlobalCooldown().getRemaining(player)));
            return true;
        }
        if (this.abilityCooldown.hasTimer(player)) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.COOLDOWN").replaceAll("%ability%", this.displayName).replaceAll("%time%", this.abilityCooldown.getRemainingString(player)));
            return true;
        }
        return false;
    }

    public boolean cannotUse(Player player) {
        if (!this.enabled) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.DISABLED"));
            return true;
        }
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (this.getAbilitiesConfig().getBoolean("GLOBAL_ABILITY.DISABLE_IN_CITADEL") && team instanceof CitadelTeam) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.DISABLED_CITADEL"));
            return true;
        }
        if (this.getAbilitiesConfig().getBoolean("GLOBAL_ABILITY.DISABLE_IN_EVENTS") && team instanceof EventTeam) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.DISABLED_EVENT"));
            return true;
        }
        return false;
    }

    public void takeItem(Player player) {
        if (this.getAbilitiesConfig().getBoolean(this.nameConfig + ".TAKE_ITEM")) {
            this.getManager().takeItemInHand(player, 1);
        }
    }

    public boolean cannotUse(Player player, Player target) {
        if (!this.enabled) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.DISABLED"));
            return true;
        }
        Team team = this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation());
        if (this.getAbilitiesConfig().getBoolean("GLOBAL_ABILITY.DISABLE_IN_CITADEL") && team instanceof CitadelTeam) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.DISABLED_CITADEL"));
            return true;
        }
        if (this.getAbilitiesConfig().getBoolean("GLOBAL_ABILITY.DISABLE_IN_EVENTS") && team instanceof EventTeam) {
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.DISABLED_EVENT"));
            return true;
        }
        return this.cannotHit(player, target);
    }

}
