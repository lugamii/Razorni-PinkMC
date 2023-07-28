package dev.razorni.hcfactions.pvpclass.type.bard;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.pvpclass.cooldown.CustomCooldown;
import dev.razorni.hcfactions.pvpclass.cooldown.EnergyCooldown;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Tasks;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class BardClass extends PvPClass {
    private final Map<UUID, EnergyCooldown> bardEnergy;
    private final int knockbackItemEnergy;
    private final int bardCooldown;
    private final boolean knockbackItemEnabled;
    private final int knockbackItemRadius;
    private final Table<Material, Short, BardEffect> clickableEffects;
    private final int knockbackItemData;
    private final Material knockbackItem;
    private final Table<Material, Short, BardEffect> holdableEffects;
    private final int maxBardEnergy;
    private final CustomCooldown bardEffectCooldown;

    public BardClass(PvPClassManager manager) {
        super(manager, "Bard");
        this.bardEnergy = new HashMap<>();
        this.holdableEffects = HashBasedTable.create();
        this.clickableEffects = HashBasedTable.create();
        this.bardEffectCooldown = new CustomCooldown(this, this.getScoreboardConfig().getString("BARD_CLASS.BARD_EFFECT"));
        this.knockbackItem = ItemUtils.getMat(this.getClassesConfig().getString("BARD_CLASS.KNOCKBACK_ITEM.MATERIAL"));
        this.knockbackItemEnabled = this.getClassesConfig().getBoolean("BARD_CLASS.KNOCKBACK_ITEM.ENABLED");
        this.knockbackItemRadius = this.getClassesConfig().getInt("BARD_CLASS.KNOCKBACK_ITEM.RADIUS");
        this.knockbackItemData = this.getClassesConfig().getInt("BARD_CLASS.KNOCKBACK_ITEM.DATA");
        this.knockbackItemEnergy = this.getClassesConfig().getInt("BARD_CLASS.KNOCKBACK_ITEM.ENERGY_REQUIRED");
        this.maxBardEnergy = this.getClassesConfig().getInt("BARD_CLASS.MAX_ENERGY");
        this.bardCooldown = this.getClassesConfig().getInt("BARD_CLASS.BARD_COOLDOWN");
        this.load();
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getNewSlot());
        if (stack != null && this.players.contains(player.getUniqueId())) {
            short data = (short) this.getManager().getData(stack);
            BardEffect effect = this.holdableEffects.get(stack.getType(), data);
            if (effect == null) {
                return;
            }
            if (this.checkBard(player)) {
                return;
            }
            effect.applyEffect(player);
        }
    }

    private void checkBardPlayers() {
        if (this.players.isEmpty()) {
            return;
        }
        for (UUID uuid : this.players) {
            Player player = Bukkit.getPlayer(uuid);
            ItemStack stack = this.getManager().getItemInHand(player);
            if (stack == null) {
                continue;
            }
            short data = (short) this.getManager().getData(stack);
            BardEffect effect = this.holdableEffects.get(stack.getType(), data);
            if (effect == null) {
                continue;
            }
            if (this.checkBard(player)) {
                continue;
            }
            effect.applyEffect(player);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (stack != null && this.players.contains(player.getUniqueId())) {
            EnergyCooldown cooldown = this.getEnergyCooldown(player);
            if (this.knockbackItemEnabled && stack.getType() == this.knockbackItem && this.getManager().getData(stack) == this.knockbackItemData) {
                if (this.bardEffectCooldown.hasCooldown(player)) {
                    player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", this.bardEffectCooldown.getRemaining(player)));
                    return;
                }
                if (cooldown.checkEnergy(this.knockbackItemEnergy)) {
                    player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.INSUFFICIENT_ENERGY").replaceAll("%energy%", String.valueOf(this.knockbackItemEnergy)));
                    return;
                }
                if (this.checkBard(player)) {
                    return;
                }
                cooldown.takeEnergy(this.knockbackItemEnergy);
                this.bardEffectCooldown.applyCooldown(player, this.bardCooldown);
                this.getInstance().getTimerManager().getCombatTimer().applyTimer(player);
                this.getManager().takeItemInHand(player, 1);
                for (Entity entity : player.getNearbyEntities(this.knockbackItemRadius, this.knockbackItemRadius, this.knockbackItemRadius)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }
                    if (entity == player) {
                        continue;
                    }
                    Player target = (Player) entity;
                    Location playerLocation = player.getEyeLocation();
                    Location targetLocation = target.getEyeLocation();
                    double x = targetLocation.getX() - playerLocation.getX();
                    double y = targetLocation.getY() - playerLocation.getY();
                    double z = targetLocation.getZ() - playerLocation.getZ();
                    Vector vector = new Vector(x, y, z);
                    target.setVelocity(vector.normalize().multiply(1.0).setY(0.4));
                }
            } else {
                short data = (short) this.getManager().getData(stack);
                BardEffect effect = this.clickableEffects.get(stack.getType(), data);
                if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
                    return;
                }
                if (effect == null) {
                    return;
                }
                if (this.bardEffectCooldown.hasCooldown(player)) {
                    player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", this.bardEffectCooldown.getRemaining(player)));
                    return;
                }
                if (cooldown.checkEnergy(effect.getEnergyRequired())) {
                    player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.INSUFFICIENT_ENERGY").replaceAll("%energy%", String.valueOf(effect.getEnergyRequired())));
                    return;
                }
                if (this.checkBard(player)) {
                    return;
                }
                cooldown.takeEnergy(effect.getEnergyRequired());
                this.getInstance().getTimerManager().getCombatTimer().applyTimer(player);
                this.getManager().takeItemInHand(player, 1);
                this.bardEffectCooldown.applyCooldown(player, this.bardCooldown);
                effect.applyEffect(player);
            }
        }
    }

    @Override
    public void load() {
        for (String s : this.getClassesConfig().getConfigurationSection("BARD_CLASS.CLICKABLE_EFFECTS").getKeys(false)) {
            String effect = "BARD_CLASS.CLICKABLE_EFFECTS." + s;
            String material = this.getClassesConfig().getString(effect + ".MATERIAL");
            Map<String, Object> values = this.getClassesConfig().getConfigurationSection(effect).getValues(false);
            this.clickableEffects.put(ItemUtils.getMat(material), (short) this.getClassesConfig().getInt(effect + ".DATA"), new BardEffect(this.getManager(), values, true));
        }
        for (String s : this.getClassesConfig().getConfigurationSection("BARD_CLASS.HOLDABLE_EFFECTS").getKeys(false)) {
            String holdeable = "BARD_CLASS.HOLDABLE_EFFECTS." + s;
            String material = this.getClassesConfig().getString(holdeable + ".MATERIAL");
            Map<String, Object> values = this.getClassesConfig().getConfigurationSection(holdeable).getValues(false);
            this.holdableEffects.put(ItemUtils.getMat(material), (short) this.getClassesConfig().getInt(holdeable + ".DATA"), new BardEffect(this.getManager(), values, false));
        }
        Tasks.executeScheduled(this.getManager(), 20, this::checkBardPlayers);
    }

    public boolean checkBard(Player player) {
        if (this.getInstance().getTimerManager().getPvpTimer().hasTimer(player) || this.getInstance().getTimerManager().getInvincibilityTimer().hasTimer(player)) {
            player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.CANNOT_BARD_PVPTIMER"));
            return true;
        }
        if (this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation()) instanceof SafezoneTeam) {
            player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.BARD_CLASS.CANNOT_BARD_SAFEZONE"));
            return true;
        }
        return false;
    }

    public EnergyCooldown getEnergyCooldown(Player player) {
        return this.bardEnergy.get(player.getUniqueId());
    }

    @Override
    public void handleEquip(Player player) {
        this.bardEnergy.put(player.getUniqueId(), new EnergyCooldown(player.getUniqueId(), this.maxBardEnergy));
    }

    @Override
    public void handleUnequip(Player player) {
        this.bardEnergy.remove(player.getUniqueId());
    }
}