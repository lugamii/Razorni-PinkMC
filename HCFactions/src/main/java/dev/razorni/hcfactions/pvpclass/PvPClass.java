package dev.razorni.hcfactions.pvpclass;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.pvpclass.cooldown.CustomCooldown;
import dev.razorni.hcfactions.timers.listeners.playertimers.WarmupTimer;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Serializer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public abstract class PvPClass extends Module<PvPClassManager> {
    protected List<Material> armor;
    protected List<PotionEffect> effects;
    protected String name;
    protected List<UUID> players;
    protected List<CustomCooldown> customCooldowns;

    public PvPClass(PvPClassManager manager, String name) {
        super(manager);
        this.name = name;
        this.players = new ArrayList<>();
        this.customCooldowns = new ArrayList<>();
        this.armor = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.loadEffectsArmor();
        manager.getClasses().put(name, this);
    }

    public void checkArmor(Player player) {
        WarmupTimer timer = this.getInstance().getTimerManager().getWarmupTimer();
        if (this.hasArmor(player) && !this.players.contains(player.getUniqueId())) {
            timer.putTimerWithClass(player, this);
        } else if (this.players.contains(player.getUniqueId())) {
            this.unEquip(player);
        } else if (timer.hasTimer(player) && timer.getWarmups().get(player.getUniqueId()) == this) {
            timer.removeTimer(player);
        }
    }

    public abstract void handleEquip(Player p0);

    public void equip(Player player) {
        player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.EQUIPPED").replaceAll("%class%", this.name));
        this.players.add(player.getUniqueId());
        this.handleEquip(player);
        this.addEffects(player);
        this.getManager().getActiveClasses().put(player.getUniqueId(), this);
    }

    public void addEffects(Player player) {
        for (PotionEffect effect : this.effects) {
            player.addPotionEffect(effect, true);
        }
    }

    private void loadEffectsArmor() {
        this.armor.addAll(Arrays.asList(ItemUtils.getMat(this.getClassesConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.HELMET")), ItemUtils.getMat(this.getClassesConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.CHESTPLATE")), ItemUtils.getMat(this.getClassesConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.LEGGINGS")), ItemUtils.getMat(this.getClassesConfig().getString(this.name.toUpperCase() + "_CLASS.ARMOR.BOOTS"))));
        this.effects.addAll(this.getClassesConfig().getStringList(this.name.toUpperCase() + "_CLASS.PASSIVE_EFFECTS").stream().map(Serializer::getEffect).collect(Collectors.toList()));
    }

    public void removeEffects(Player player) {
        for (PotionEffect effect : this.effects) {
            if (!player.hasPotionEffect(effect.getType())) {
                continue;
            }
            player.removePotionEffect(effect.getType());
        }
    }

    public abstract void handleUnequip(Player p0);

    public abstract void load();

    private boolean hasArmor(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        return helmet != null && chestplate != null && leggings != null && boots != null && helmet.getType() == this.armor.get(0) && chestplate.getType() == this.armor.get(1) && leggings.getType() == this.armor.get(2) && boots.getType() == this.armor.get(3);
    }

    public void unEquip(Player player) {
        player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.UNEQUIPPED").replaceAll("%class%", this.name));
        this.players.remove(player.getUniqueId());
        this.handleUnequip(player);
        this.removeEffects(player);
        this.getManager().getRestores().rowKeySet().remove(player.getUniqueId());
        this.getManager().getActiveClasses().remove(player.getUniqueId());
    }
}
