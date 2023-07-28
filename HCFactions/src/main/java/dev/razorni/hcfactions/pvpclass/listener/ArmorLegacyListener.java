package dev.razorni.hcfactions.pvpclass.listener;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ArmorLegacyListener extends Module<PvPClassManager> {
    public ArmorLegacyListener(PvPClassManager manager) {
        super(manager);
    }

    @EventHandler
    public void onExpire(PotionEffectExpireEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        PotionEffect effect = this.getManager().getRestores().remove(player.getUniqueId(), event.getEffect().getType());
        if (effect != null) {
            Tasks.execute(this.getManager(), () -> player.addPotionEffect(effect));
        }
    }

    @EventHandler
    public void onEquip(EquipmentSetEvent event) {
        if (event.getNewItem() != null && event.getPreviousItem() != null && event.getNewItem().getType() == event.getPreviousItem().getType()) {
            return;
        }
        this.getManager().checkArmor((Player) event.getHumanEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEquipFix(EquipmentSetEvent event) {
        Player player = (Player) event.getHumanEntity();
        ItemStack stack = event.getPreviousItem();
        if (stack == null) {
            return;
        }
        this.getManager().getRestores().rowKeySet().remove(player.getUniqueId());
    }
}
