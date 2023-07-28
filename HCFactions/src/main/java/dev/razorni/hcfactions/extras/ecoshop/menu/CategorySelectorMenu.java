package dev.razorni.hcfactions.extras.ecoshop.menu;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CategorySelectorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&dItems Shop &7┃ &f$" + HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(8, new GlassButton());
        buttons.put(9, new GlassButton());
        buttons.put(16 + 1, new GlassButton());
        buttons.put(26 + 1, new GlassButton());
        buttons.put(35, new GlassButton());
        buttons.put(36, new GlassButton());
        buttons.put(36 + 1, new GlassButton());
        buttons.put(43, new GlassButton());
        buttons.put(44, new GlassButton());


        buttons.put(4, new HeadButton());
        buttons.put(22, new PotionButton());
        buttons.put(20, new SellButton());
        buttons.put(24, new ItemsButton());

        setAutoUpdate(true);

        setAutoUpdate(true);


        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9 * 5;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public static class GlassButton extends Button {


        public String getName(Player player) {
            return CC.translate(" ");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class HeadButton extends Button {


        public String getName(Player player) {
            return CC.PINK + player.getName() + "'s Balance";
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.SKULL_ITEM, 1);
        }

        public String getLore(Player player) {
            return CC.translate("&fYour Balance: &d$" + HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance());
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).durability(3).skull(player.getName()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class ECButton extends Button {


        public String getName(Player player) {
            return CC.GOLD + "Enderchest";
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore(Player player) {
            return CC.translate("&6&l┃ &fStore your items in enderchest for more space.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.openInventory(player.getEnderChest());
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class PotionButton extends Button {


        public String getName() {
            return CC.PINK + ("Spawners Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.MOB_SPAWNER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new PotionMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName()).lore(CC.translate("&fBuy spawners with ur money.")).build();
        }
    }

    @AllArgsConstructor
    public static class ItemsButton extends Button {


        public String getName() {
            return CC.PINK + ("Items Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.RED_ROSE, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new ItemsMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName()).lore(CC.translate("&fBuy items with ur money.")).build();
        }
    }

    @AllArgsConstructor
    public static class SellButton extends Button {


        public String getName() {
            return CC.PINK + ("Sell Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.EMERALD, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new SellMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName()).lore(CC.translate("&fSell valuables for money.")).build();
        }
    }


}
