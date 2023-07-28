package dev.razorni.hcfactions.pvpclass.type.mage;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.pvpclass.cooldown.CustomCooldown;
import dev.razorni.hcfactions.pvpclass.cooldown.EnergyCooldown;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import dev.razorni.hcfactions.timers.TimerManager;
import dev.razorni.hcfactions.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MageClass extends PvPClass {
    private final CustomCooldown mageEffectCooldown;
    private final Map<UUID, EnergyCooldown> mageEnergy;
    private final int maxMageEnergy;
    private final int mageCooldown;
    private final Table<Material, Short, MageEffect> clickableEffects;

    public MageClass(PvPClassManager manager) {
        super(manager, "Mage");
        this.mageEnergy = new HashMap<>();
        this.clickableEffects = HashBasedTable.create();
        this.mageEffectCooldown = new CustomCooldown(this, this.getScoreboardConfig().getString("MAGE_CLASS.MAGE_EFFECT"));
        this.maxMageEnergy = this.getClassesConfig().getInt("MAGE_CLASS.MAX_ENERGY");
        this.mageCooldown = this.getClassesConfig().getInt("MAGE_CLASS.MAGE_COOLDOWN");
        this.load();
    }

    @Override
    public void handleEquip(Player player) {
        this.mageEnergy.put(player.getUniqueId(), new EnergyCooldown(player.getUniqueId(), this.maxMageEnergy));
    }

    @Override
    public void handleUnequip(Player player) {
        this.mageEnergy.remove(player.getUniqueId());
    }

    public EnergyCooldown getEnergyCooldown(Player player) {
        return this.mageEnergy.get(player.getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (stack != null && this.players.contains(player.getUniqueId())) {
            short data = (short) this.getManager().getData(stack);
            MageEffect effect = this.clickableEffects.get(stack.getType(), data);
            if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
                return;
            }
            if (effect == null) {
                return;
            }
            if (this.mageEffectCooldown.hasCooldown(player)) {
                player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", this.mageEffectCooldown.getRemaining(player)));
                return;
            }
            if (this.getEnergyCooldown(player).checkEnergy(effect.getEnergyRequired())) {
                player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.INSUFFICIENT_ENERGY").replaceAll("%energy%", String.valueOf(effect.getEnergyRequired())));
                return;
            }
            if (this.checkMage(player)) {
                return;
            }
            this.getInstance().getTimerManager().getCombatTimer().applyTimer(player);
            this.getEnergyCooldown(player).takeEnergy(effect.getEnergyRequired());
            this.getManager().takeItemInHand(player, 1);
            this.mageEffectCooldown.applyCooldown(player, this.mageCooldown);
            effect.applyEffect(player);
        }
    }

    @Override
    public void load() {
        this.getClassesConfig().getConfigurationSection("MAGE_CLASS.CLICKABLE_EFFECTS").getKeys(false).forEach(llllllllllllllllIllIlIIlIIllIIII -> {
            String effect = "MAGE_CLASS.CLICKABLE_EFFECTS." + llllllllllllllllIllIlIIlIIllIIII;
            String material = this.getClassesConfig().getString(effect + ".MATERIAL");
            Map<String, Object> map = this.getClassesConfig().getConfigurationSection(effect).getValues(false);
            this.clickableEffects.put(ItemUtils.getMat(material), (short) this.getClassesConfig().getInt(effect + ".DATA"), new MageEffect(this.getManager(), map));
        });
    }

    private boolean checkMage(Player player) {
        TimerManager manager = this.getInstance().getTimerManager();
        if (manager.getPvpTimer().hasTimer(player) || manager.getInvincibilityTimer().hasTimer(player)) {
            player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.CANNOT_MAGE_PVPTIMER"));
            return true;
        }
        if (this.getInstance().getTeamManager().getClaimManager().getTeam(player.getLocation()) instanceof SafezoneTeam) {
            player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.MAGE_CLASS.CANNOT_MAGE_SAFEZONE"));
            return true;
        }
        return false;
    }
}