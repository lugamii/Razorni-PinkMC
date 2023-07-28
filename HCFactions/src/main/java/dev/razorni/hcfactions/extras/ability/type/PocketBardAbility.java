package dev.razorni.hcfactions.extras.ability.type;

import dev.razorni.hcfactions.extras.ability.Ability;
import dev.razorni.hcfactions.extras.ability.AbilityManager;
import dev.razorni.hcfactions.extras.ability.extra.AbilityUseType;
import dev.razorni.hcfactions.extras.framework.menu.Menu;
import dev.razorni.hcfactions.extras.framework.menu.MenuManager;
import dev.razorni.hcfactions.extras.framework.menu.button.Button;
import dev.razorni.hcfactions.pvpclass.PvPClassManager;
import dev.razorni.hcfactions.pvpclass.type.bard.BardEffect;
import dev.razorni.hcfactions.utils.ItemBuilder;
import dev.razorni.hcfactions.utils.ItemUtils;
import dev.razorni.hcfactions.utils.Serializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class PocketBardAbility extends Ability {
    private Map<String, PocketBard> pocketBards;

    public PocketBardAbility(AbilityManager manager) {
        super(manager, AbilityUseType.INTERACT, "PocketBard");
        this.pocketBards = new HashMap<>();
        this.load();
    }

    @Override
    public void onClick(Player player) {
        new PocketBardMenu(this.getInstance().getMenuManager(), this, player).open();
    }

    private void load() {
        for (String s : this.getAbilitiesConfig().getConfigurationSection("POCKETBARD.TYPES").getKeys(false)) {
            String type = "POCKETBARD.TYPES." + s + ".";
            ItemStack stack = new ItemBuilder(ItemUtils.getMat(this.getAbilitiesConfig().getString(type + "MATERIAL"))).setName(this.getAbilitiesConfig().getString(type + "NAME")).setLore(this.getAbilitiesConfig().getStringList(type + "LORE")).data(this.getManager(), (short) this.getAbilitiesConfig().getInt(type + "DATA")).toItemStack();
            stack.setAmount(this.getAbilitiesConfig().getInt(type + "AMOUNT"));
            this.pocketBards.put(s, new PocketBard(this.getInstance().getClassManager(), Serializer.getEffect(this.getAbilitiesConfig().getString(type + "EFFECT")), stack));
        }
        for (String s : this.getAbilitiesConfig().getConfigurationSection("POCKETBARD.POCKETBARD_MENU.ITEMS").getKeys(false)) {
            String item = "POCKETBARD.POCKETBARD_MENU.ITEMS." + s + ".";
            PocketBard pocket = this.pocketBards.get(this.getAbilitiesConfig().getString(item + "POCKET_BARD"));
            ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(this.getAbilitiesConfig().getString(item + "MATERIAL"))).setName(this.getAbilitiesConfig().getString(item + "NAME")).setLore(this.getAbilitiesConfig().getStringList(item + "LORE")).data(this.getManager(), (short) this.getAbilitiesConfig().getInt(item + "DATA"));
            pocket.setMenuItem(builder.toItemStack());
            pocket.setSlot(this.getAbilitiesConfig().getInt(item + "SLOT"));
        }
    }

    public PocketBard getPocketBardInHand(Player player) {
        ItemStack stack = this.getManager().getItemInHand(player);
        if (stack == null) {
            return null;
        }
        for (PocketBard pocket : this.pocketBards.values()) {
            if (!pocket.getItem().isSimilar(stack)) {
                continue;
            }
            return pocket;
        }
        return null;
    }

    public Map<String, PocketBard> getPocketBards() {
        return this.pocketBards;
    }

    @EventHandler
    public void onInteractPocket(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }
        Player player = event.getPlayer();
        PocketBard pocket = this.getPocketBardInHand(player);
        if (pocket != null) {
            if (this.cannotUse(player)) {
                return;
            }
            if (this.hasCooldown(player)) {
                return;
            }
            pocket.getEffect().applyEffect(player);
            this.takeItem(player);
            this.applyCooldown(player);
        }
    }

    private static class PocketBardMenu extends Menu {
        private PocketBardAbility ability;

        public PocketBardMenu(MenuManager menu, PocketBardAbility ability, Player player) {
            super(menu, player, menu.getAbilitiesConfig().getString("POCKETBARD.POCKETBARD_MENU.TITLE"), menu.getAbilitiesConfig().getInt("POCKETBARD.POCKETBARD_MENU.SIZE"), false);
            this.ability = ability;
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttons = new HashMap<>();
            for (PocketBard pocket : this.ability.getPocketBards().values()) {
                buttons.put(pocket.getSlot(), new Button() {
                    @Override
                    public ItemStack getItemStack() {
                        return pocket.getMenuItem();
                    }

                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (!ability.hasAbilityInHand(player)) {
                            event.setCancelled(true);
                            player.closeInventory();
                            return;
                        }
                        event.setCancelled(true);
                        PocketBardMenu.this.getManager().takeItemInHand(player, 1);
                        ItemUtils.giveItem(player, pocket.getItem(), player.getLocation());
                        if (!ability.hasAbilityInHand(player)) {
                            player.closeInventory();
                        }
                    }
                });
            }
            return buttons;
        }
    }

    private static class PocketBard {
        private BardEffect effect;
        private ItemStack menuItem;
        private ItemStack item;
        private int slot;

        public PocketBard(PvPClassManager manager, PotionEffect effect, ItemStack item) {
            this.effect = new BardEffect(manager, true, effect);
            this.item = item;
            this.menuItem = null;
            this.slot = 0;
        }

        public ItemStack getMenuItem() {
            return this.menuItem;
        }

        public void setMenuItem(ItemStack menuItem) {
            this.menuItem = menuItem;
        }

        public int getSlot() {
            return this.slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public BardEffect getEffect() {
            return this.effect;
        }

        public void setEffect(BardEffect effect) {
            this.effect = effect;
        }

        public ItemStack getItem() {
            return this.item;
        }

        public void setItem(ItemStack item) {
            this.item = item;
        }
    }
}
