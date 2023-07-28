package dev.razorni.hcfactions.pvpclass.type.archer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.pvpclass.PvPClass;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.pvpclass.cooldown.ClassBuff;
import dev.razorni.hcfactions.timers.event.TimerExpireEvent;
import dev.razorni.hcfactions.timers.listeners.playertimers.ArcherTagTimer;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ArcherClass extends PvPClass {
    private final Map<UUID, Float> arrowForce;
    private final ArcherTagTimer archerTag;
    private final Table<Material, Short, ClassBuff> buffs;

    public ArcherClass(PvPClassManager manager) {
        super(manager, "Archer");
        this.buffs = HashBasedTable.create();
        this.arrowForce = new HashMap<>();
        this.archerTag = this.getInstance().getTimerManager().getArcherTagTimer();
        this.load();
    }

    @Override
    public void load() {
        for (String s : this.getClassesConfig().getConfigurationSection("ARCHER_CLASS.ARCHER_BUFFS").getKeys(false)) {
            String name = "ARCHER_CLASS.ARCHER_BUFFS." + s + ".";
            String material = this.getClassesConfig().getString(name + "MATERIAL");
            String displayName = this.getClassesConfig().getString(name + "DISPLAY_NAME");
            PotionEffect effect = Serializer.getEffect(this.getClassesConfig().getString(name + "EFFECT"));
            int data = this.getClassesConfig().getInt(name + "DATA");
            int cooldown = this.getClassesConfig().getInt(name + "COOLDOWN");
            this.buffs.put(ItemUtils.getMat(material), (short) data, new ClassBuff(this, displayName, effect, cooldown));
        }
    }

    @EventHandler
    public void onExpire(TimerExpireEvent event) {
        if (!(event.getTimer() instanceof ArcherTagTimer)) {
            return;
        }
        this.getInstance().getNametagManager().update();
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
                player.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ARCHER_CLASS.BUFF_COOLDOWN").replaceAll("%seconds%", classBuff.getRemaining(player)));
                return;
            }
            classBuff.applyCooldown(player, classBuff.getCooldown());
            this.getManager().addEffect(player, classBuff.getEffect());
            this.getManager().takeItemInHand(player, 1);
        }
    }

    @EventHandler
    public void onArrow(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = Utils.getDamager(event.getDamager());
        if (player == null) {
            return;
        }
        if (!this.players.contains(player.getUniqueId())) {
            return;
        }
        this.archerTag(event);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getProjectile() instanceof Arrow)) {
            return;
        }
        this.arrowForce.put(event.getProjectile().getUniqueId(), event.getForce());
    }

    @Override
    public void handleUnequip(Player player) {
    }

    @Override
    public void handleEquip(Player player) {
    }


    public void archerTag(EntityDamageByEntityEvent event) {
        Arrow arrow = (Arrow) event.getDamager();
        Player damaged = (Player) event.getEntity();
        Player damager = Utils.getDamager(event.getDamager());
        if (!this.arrowForce.containsKey(arrow.getUniqueId())) {
            return;
        }
        if (!this.getInstance().getTeamManager().canHit(damager, damaged, false)) {
            return;
        }
        if (this.players.contains(damaged.getUniqueId())) {
            damager.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ARCHER_CLASS.CANNOT_MARK"));
            return;
        }
        double tagDamage = this.archerTag.hasTimer(damaged) ? Config.ARCHER_TAGGED_DAMAGE : Config.ARCHER_TAG_DAMAGE;
        float force = this.arrowForce.remove(arrow.getUniqueId());
        int distance = (int) damaged.getLocation().distance(damager.getLocation());
        if (force < 0.5f) {
            tagDamage = Config.ARCHER_HALF_FORCE_DAMAGE;
        }
        double damage = tagDamage / 2.0;
        damaged.setHealth(Math.max(damaged.getHealth() - tagDamage, 0.0));
        event.setDamage(0.0);
        if (damaged.isDead()) {
            damaged.setLastDamageCause(new EntityDamageByEntityEvent(damager, damaged, EntityDamageEvent.DamageCause.PROJECTILE, tagDamage));
            event.setCancelled(true);
        }
        if (force >= 0.5f) {
            damager.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ARCHER_CLASS.MARKED_PLAYER").replaceAll("%distance%", String.valueOf(distance)).replaceAll("%seconds%", String.valueOf(this.archerTag.getSeconds())).replaceAll("%damage%", damage + " heart" + ((damage > 1.0) ? "s" : "")));
            if (!this.archerTag.hasTimer(damaged)) {
                damaged.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ARCHER_CLASS.PLAYER_MARKED").replaceAll("%seconds%", String.valueOf(this.archerTag.getSeconds())));
                this.archerTag.applyTimer(damaged);
                this.getInstance().getNametagManager().update();
                return;
            }
            this.archerTag.applyTimer(damaged);
        } else {
            damager.sendMessage(this.getLanguageConfig().getString("PVP_CLASSES.ARCHER_CLASS.NOT_DRAWN_BACK").replaceAll("%distance%", String.valueOf(distance)).replaceAll("%damage%", damage + " heart" + ((damage > 1.0) ? "s" : "")));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (this.archerTag.hasTimer(player)) {
            event.setDamage(event.getDamage() * Config.ARCHER_TAGGED_MULTIPLIER);
        }
    }
}
