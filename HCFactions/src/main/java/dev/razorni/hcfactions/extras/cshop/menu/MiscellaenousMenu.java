package dev.razorni.hcfactions.extras.cshop.menu;

import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class MiscellaenousMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eMiscellaenous Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(22, new BackButton(new VirtualCategoryMenu()));
        buttons.put(8, new GlassButton());
        buttons.put(12, new WebButton());
        buttons.put(13, new AirdropButton());
        buttons.put(14, new FishingButton());
        buttons.put(9, new GlassButton());
        buttons.put(16 + 1, new GlassButton());
        buttons.put(18, new GlassButton());
        buttons.put(19, new GlassButton());
        buttons.put(25, new GlassButton());
        buttons.put(26, new GlassButton());

        setAutoUpdate(true);


        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9 * 3;
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
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class WebButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dWeb");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.WEB, 16);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 5");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 5) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getName() + " web 16");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x16 &efor &d⛁ 5"));
                profile.setCoins(profile.getCoins() - 5);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new MiscellaenousMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class FishingButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dFishing Rod");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.FISHING_ROD, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 10");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 10) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                ItemStack fishingrod = new ItemStack(Material.FISHING_ROD, 1);
                ItemMeta meta = fishingrod.getItemMeta();
                meta.addEnchant(Enchantment.DURABILITY, 5, true);
                fishingrod.setItemMeta(meta);
                player.getInventory().addItem(fishingrod);
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 10"));
                profile.setCoins(profile.getCoins() - 10);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new MiscellaenousMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class AirdropButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dAirdrop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.DISPENSER, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 15");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 15) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "airdrops give " + player.getName() + " 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 15"));
                profile.setCoins(profile.getCoins() - 15);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new MiscellaenousMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }


}
