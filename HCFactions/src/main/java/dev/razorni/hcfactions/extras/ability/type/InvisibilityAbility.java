package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.utils.Serializer;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InvisibilityAbility extends Ability {
    private Set<UUID> invisible;
    private PotionEffect invisEffect;

    public InvisibilityAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "Invisibility");
        this.invisible = new HashSet<>();
        this.invisEffect = Serializer.getEffect(this.getAbilitiesConfig().getString("INVISIBILITY.INVIS_EFFECT"));
        this.load();
    }

    @Override
    public void onClick(Player player) {
        if (this.cannotUse(player)) {
            return;
        }
        if (this.hasCooldown(player)) {
            return;
        }
        this.takeItem(player);
        this.applyCooldown(player);
        this.hideArmor(player);
        this.invisible.add(player.getUniqueId());
        this.getInstance().getClassManager().addEffect(player, this.invisEffect);
        for (String s : this.getLanguageConfig().getStringList("ABILITIES.INVISIBILITY.USED")) {
            player.sendMessage(s);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Player damager = Utils.getDamager(event.getDamager());
        if (damager == null) {
            return;
        }
        if (this.cannotHit(damager, player)) {
            return;
        }
        if (this.invisible.contains(player.getUniqueId())) {
            this.showArmor(player);
            this.invisible.remove(player.getUniqueId());
            player.sendMessage(this.getLanguageConfig().getString("ABILITIES.INVISIBILITY.DAMAGED"));
        }
    }

    private void load() {
        if (this.getInstance().getVersionManager().isVer16()) {
            this.getManager().registerListener(new Listener() {
                @EventHandler
                public void onExpire(EntityPotionEffectEvent event) {
                    if (!(event.getEntity() instanceof Player)) {
                        return;
                    }
                    if (event.getAction() != EntityPotionEffectEvent.Action.REMOVED) {
                        return;
                    }
                    if (event.getCause() != EntityPotionEffectEvent.Cause.EXPIRATION) {
                        return;
                    }
                    if (event.getOldEffect() == null || event.getOldEffect().getType() != PotionEffectType.INVISIBILITY) {
                        return;
                    }
                    Player player = (Player) event.getEntity();
                    if (InvisibilityAbility.this.invisible.remove(player.getUniqueId())) {
                        InvisibilityAbility.this.showArmor(player);
                        player.sendMessage(InvisibilityAbility.this.getLanguageConfig().getString("ABILITIES.INVISIBILITY.EXPIRED"));
                    }
                }
            });
        } else {
            this.manager.registerListener(new Listener() {
                @EventHandler
                public void onEquip(EquipmentSetEvent event) {
                    if (event.getNewItem() != null && event.getPreviousItem() != null && event.getNewItem().getType() == event.getPreviousItem().getType()) {
                        return;
                    }
                    Player player = (Player) event.getHumanEntity();
                    if (InvisibilityAbility.this.invisible.contains(player.getUniqueId())) {
                        Tasks.execute(getManager(), () -> InvisibilityAbility.this.hideArmor(player));
                    }
                }

                @EventHandler
                public void onExpire(PotionEffectExpireEvent event) {
                    if (!(event.getEntity() instanceof Player)) {
                        return;
                    }
                    if (event.getEffect().getType() != PotionEffectType.INVISIBILITY) {
                        return;
                    }
                    Player player = (Player) event.getEntity();
                    if (InvisibilityAbility.this.invisible.remove(player.getUniqueId())) {
                        InvisibilityAbility.this.showArmor(player);
                        player.sendMessage(InvisibilityAbility.this.getLanguageConfig().getString("ABILITIES.INVISIBILITY.EXPIRED"));
                    }
                }
            });
        }
    }

    private void hideArmor(Player player) {
        this.getInstance().getVersionManager().getVersion().hideArmor(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.invisible.contains(player.getUniqueId())) {
            this.getInstance().getVersionManager().getVersion().hideArmor(player);
        }
    }

    private void showArmor(Player player) {
        this.getInstance().getVersionManager().getVersion().showArmor(player);
    }
}
