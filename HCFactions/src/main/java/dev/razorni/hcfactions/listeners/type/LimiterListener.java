package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.utils.ReflectionUtils;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LimiterListener extends Module<ListenerManager> {
    private static Method GET_POTION_DATA;

    static {
        LimiterListener.GET_POTION_DATA = null;
    }

    private final Map<Enchantment, Integer> enchantmentLimits;
    private final Map<PotionEffectType, PotionLimit> potionLimits;

    public LimiterListener(ListenerManager manager) {
        super(manager);
        this.potionLimits = new HashMap<>();
        this.enchantmentLimits = new HashMap<>();
        this.load();
    }

    private void cancelBookEnchant(EnchantmentStorageMeta storageMeta) {
        for (Map.Entry<Enchantment, Integer> entry : storageMeta.getStoredEnchants().entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer limit = this.enchantmentLimits.get(enchantment);
            Integer value = entry.getValue();
            if (limit != null && limit != -1) {
                if (limit == 0) {
                    storageMeta.removeStoredEnchant(enchantment);
                } else {
                    if (value <= limit) {
                        continue;
                    }
                    storageMeta.addStoredEnchant(enchantment, limit, true);
                }
            }
        }
    }

    private void cancelItemEnchant(ItemStack stack) {
        for (Map.Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer value = entry.getValue();
            Integer limit = this.enchantmentLimits.get(enchantment);
            if (limit != null && limit != -1) {
                if (limit == 0) {
                    stack.removeEnchantment(enchantment);
                } else {
                    if (value <= limit) {
                        continue;
                    }
                    stack.addUnsafeEnchantment(enchantment, limit);
                }
            }
        }
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || inventory.getType() != InventoryType.ANVIL) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        ItemStack stack = event.getCurrentItem();
        if (stack.hasItemMeta() && stack.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) stack.getItemMeta();
            this.cancelBookEnchant(storageMeta);
            stack.setItemMeta(storageMeta);
            return;
        }
        if (stack.hasItemMeta() && stack.getItemMeta().hasEnchants()) {
            this.cancelItemEnchant(stack);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Item)) {
            return;
        }
        ItemStack stack = ((Item) event.getCaught()).getItemStack();
        if (stack.hasItemMeta() && stack.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) stack.getItemMeta();
            this.cancelBookEnchant(storageMeta);
            stack.setItemMeta(storageMeta);
            return;
        }
        this.cancelItemEnchant(stack);
    }

    private void load() {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment == null) continue;
            int limit = this.getLimitersConfig().getInt("ENCHANTMENTS." + enchantment.getName());
            this.enchantmentLimits.put(enchantment, limit);
        }
        for (PotionEffectType type : PotionEffectType.values()) {
            if (type == null) continue;
            boolean enabled = this.getLimitersConfig().getBoolean("POTIONS." + type.getName() + ".ENABLED");
            boolean upgradeable = this.getLimitersConfig().getBoolean("POTIONS." + type.getName() + ".UPGRADABLE");
            boolean extend = this.getLimitersConfig().getBoolean("POTIONS." + type.getName() + ".EXTENDED");
            this.potionLimits.put(type, new PotionLimit(enabled, upgradeable, extend));
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> enchants = event.getEnchantsToAdd();
        Iterator<Map.Entry<Enchantment, Integer>> iterator = enchants.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Enchantment, Integer> entry = iterator.next();
            Enchantment enchantment = entry.getKey();
            Integer value = entry.getValue();
            Integer limit = this.enchantmentLimits.get(entry.getKey());
            if (limit != null && limit != -1) {
                if (limit == 0) {
                    iterator.remove();
                } else {
                    if (value <= limit) {
                        continue;
                    }
                    enchants.put(enchantment, limit);
                }
            }
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inventory = event.getContents();
        Tasks.execute(this.getManager(), () -> {
            for (int i = 0; i < 3; ++i) {
                ItemStack stack = inventory.getItem(i);
                if (stack == null || !this.cancelPotion(stack)) continue;
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        });
    }

    @SneakyThrows
    public boolean cancelPotion(ItemStack stack) {
        if (!stack.getType().name().contains("POTION")) {
            return false;
        }
        if (this.getInstance().getVersionManager().isVer16()) {
            PotionMeta meta;
            PotionData data;
            PotionLimit limit;
            if (!stack.hasItemMeta() || !(stack.getItemMeta() instanceof PotionMeta)) {
                return false;
            }
            if (GET_POTION_DATA == null) {
                GET_POTION_DATA = ReflectionUtils.accessMethod(PotionMeta.class, "getBasePotionData");
            }
            if ((limit = this.potionLimits.get((data = (PotionData) GET_POTION_DATA.invoke(stack.getItemMeta(), new Object[0])).getType().getEffectType())) == null) {
                return false;
            }
            if (!limit.isEnabled()) {
                return true;
            }
            if (!limit.isUpgradable() && data.isUpgraded()) {
                return true;
            }
            return !limit.isExtended() && data.isExtended();
        }
        Potion potion = Potion.fromItemStack(stack);
        for (PotionEffect effect : potion.getEffects()) {
            PotionLimit limit = this.potionLimits.get(effect.getType());
            if (limit == null) continue;
            if (!limit.isEnabled()) {
                return true;
            }
            if (!limit.isUpgradable() && effect.getAmplifier() >= 1) {
                return true;
            }
            return !limit.isExtended() && potion.hasExtendedDuration();
        }
        return false;
    }

    @EventHandler
    public void onEffect(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        Player player = Utils.getDamager(potion);
        ItemStack stack = potion.getItem();
        if (player == null) {
            return;
        }
        if (stack == null) {
            return;
        }
        if (this.cancelPotion(potion.getItem())) {
            event.setCancelled(true);
            player.sendMessage(this.getLanguageConfig().getString("LIMITER_LISTENER.DENIED_POTION"));
        }
    }

    @Data
    private static class PotionLimit {
        private final boolean enabled;
        private final boolean extended;
        private final boolean upgradable;
    }
}
