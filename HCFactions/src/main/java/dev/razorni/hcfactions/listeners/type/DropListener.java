package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DropListener extends Module<ListenerManager> {
    public DropListener(ListenerManager manager) {
        super(manager);
    }

    private ItemStack smelt(Block block) {
        Material material = block.getType();
        if (material == Material.IRON_ORE) {
            return new ItemStack(Material.IRON_INGOT);
        }
        if (material == Material.GOLD_ORE) {
            return new ItemStack(Material.GOLD_INGOT);
        }
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDieEXP(EntityDeathEvent event) {
        if (!this.getConfig().getBoolean("GIVE_EXP_KILL")) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            return;
        }
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            killer.giveExp(event.getDroppedExp());
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !this.getConfig().getBoolean("SMELT_ON_MINE")) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack smelt = this.smelt(block);
        if (!player.hasPermission("azurite.autosmelt")) {
            return;
        }
        if (smelt == null) {
            return;
        }
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack.hasItemMeta() && stack.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
            return;
        }
        block.setType(Material.AIR);
        ItemUtils.giveItem(player, smelt, block.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakEXP(BlockBreakEvent event) {
        if (event.isCancelled() || !this.getConfig().getBoolean("GIVE_EXP_MINE")) {
            return;
        }
        event.getPlayer().giveExp(event.getExpToDrop());
        event.setExpToDrop(0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDieDrop(EntityDeathEvent event) {
        if (!this.getConfig().getBoolean("GIVE_ITEM_ON_KILL")) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            return;
        }
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            List<ItemStack> drops = event.getDrops();
            if (drops == null) {
                return;
            }
            for (ItemStack stack : drops) {
                if (stack == null) {
                    continue;
                }
                ItemUtils.giveItem(killer, stack, event.getEntity().getLocation());
            }
            drops.clear();
        }
    }
}
