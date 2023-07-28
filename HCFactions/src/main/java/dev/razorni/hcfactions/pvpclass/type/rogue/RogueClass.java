package dev.razorni.hcfactions.pvpclass.type.rogue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.pvpclass.cooldown.ClassBuff;
import dev.razorni.hcfactions.pvpclass.cooldown.CustomCooldown;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Serializer;
import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RogueClass extends PvPClass {
    private final CustomCooldown backstabCooldown;
    private final Table<Material, Short, ClassBuff> buffs;
    private final List<PotionEffect> backstabEffects;
    private final Material backstabItem;

    public RogueClass(PvPClassManager manager) {
        super(manager, "Rogue");
        this.buffs = HashBasedTable.create();
        this.backstabEffects = new ArrayList<>();
        this.backstabCooldown = new CustomCooldown(this, this.getScoreboardConfig().getString("ROGUE_CLASS.BACKSTAB"));
        this.backstabItem = ItemUtils.getMat(this.getClassesConfig().getString("ROGUE_CLASS.BACKSTAB_ITEM"));
        this.load();
    }

    @EventHandler
    public void onBackstab(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        if (this.manager.getItemInHand(damager) == null || this.manager.getItemInHand(damager).getType() != this.backstabItem) {
            return;
        }
        if (!this.players.contains(damager.getUniqueId())) {
            return;
        }
        if (!this.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            return;
        }
        Vector damagerVector = damager.getLocation().getDirection().setY(0);
        Vector damagedVector = damaged.getLocation().getDirection().setY(0);
        double angle = damagerVector.angle(damagedVector);
        double configAngle = this.getClassesConfig().getDouble("ROGUE_CLASS.BACKSTAB_DAMAGE") * 2.0;
        if (Math.abs(angle) < 1.4) {
            if (this.backstabCooldown.hasCooldown(damager)) {
                damager.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ROGUE_CLASS.BACKSTAB_COOLDOWN").replaceAll("%seconds%", this.backstabCooldown.getRemaining(damager)));
                return;
            }
            this.backstabCooldown.applyCooldown(damager, this.getClassesConfig().getInt("ROGUE_CLASS.BACKSTAB_COOLDOWN"));
            this.getManager().setItemInHand(damager, new ItemStack(Material.AIR));
            damaged.getWorld().playSound(damaged.getLocation(), Sound.valueOf(this.getClassesConfig().getString("ROGUE_CLASS.BACKSTAB_SOUND")), 1.0f, 1.0f);
            this.getInstance().getVersionManager().getVersion().playEffect(damaged.getLocation().add(0.0, 1.0, 0.0), Effect.STEP_SOUND.name(), ItemUtils.getMat(this.getClassesConfig().getString("ROGUE_CLASS.BACKSTAB_EFFECT")));
            damaged.setLastDamageCause(new RogueBackstabEvent(damaged, damager, EntityDamageEvent.DamageCause.CUSTOM, configAngle));
            damaged.setHealth(Math.max(damaged.getHealth() - configAngle, 0.0));
            event.setDamage(0.0);
            if (damaged.isDead()) {
                event.setCancelled(true);
            }
            for (PotionEffect effect : this.backstabEffects) {
                damager.addPotionEffect(effect);
            }
        } else {
            damager.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ROGUE_CLASS.BACKSTAB_FAILED"));
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack stack = this.manager.getItemInHand(player);
        if (stack == null) {
            return;
        }
        if (!this.players.contains(player.getUniqueId())) {
            return;
        }
        if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
            return;
        }
        ClassBuff classBuff = this.buffs.get(stack.getType(), (short) this.getManager().getData(stack));
        if (classBuff != null) {
            if (classBuff.hasCooldown(player)) {
                player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ROGUE_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", classBuff.getRemaining(player)));
                return;
            }
            classBuff.applyCooldown(player, classBuff.getCooldown());
            this.getManager().addEffect(player, classBuff.getEffect());
            this.getManager().takeItemInHand(player, 1);
        }
    }

    @Override
    public void handleEquip(Player player) {
    }

    @Override
    public void handleUnequip(Player player) {
    }

    @Override
    public void load() {
        this.backstabEffects.addAll(this.getClassesConfig().getStringList("ROGUE_CLASS.BACKSTAB_EFFECTS").stream().map(Serializer::getEffect).collect(Collectors.toList()));
        for (String s : this.getClassesConfig().getConfigurationSection("ROGUE_CLASS.ROGUE_BUFFS").getKeys(false)) {
            String path = "ROGUE_CLASS.ROGUE_BUFFS." + s + ".";
            String material = this.getClassesConfig().getString(path + "MATERIAL");
            String displayName = this.getClassesConfig().getString(path + "DISPLAY_NAME");
            PotionEffect effect = Serializer.getEffect(this.getClassesConfig().getString(path + "EFFECT"));
            int data = this.getClassesConfig().getInt(path + "DATA");
            int cooldown = this.getClassesConfig().getInt(path + "COOLDOWN");
            this.buffs.put(ItemUtils.getMat(material), (short) data, new ClassBuff(this, displayName, effect, cooldown));
        }
    }
}
