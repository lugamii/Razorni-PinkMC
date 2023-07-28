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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LootboxMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eLootbox Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new CrateMenu.GlassButton());
        buttons.put(1, new CrateMenu.GlassButton());
        buttons.put(6 + 1, new CrateMenu.GlassButton());
        buttons.put(8, new CrateMenu.GlassButton());
        buttons.put(9, new CrateMenu.GlassButton());
        buttons.put(16 + 1, new CrateMenu.GlassButton());
        buttons.put(18, new CrateMenu.GlassButton());
        buttons.put(19, new CrateMenu.GlassButton());
        buttons.put(25, new CrateMenu.GlassButton());
        buttons.put(26, new CrateMenu.GlassButton());

        buttons.put(22, new BackButton(new VirtualCategoryMenu()));
        buttons.put(12, new MarchButton());
        buttons.put(14, new tentythreButton());

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
    public static class MarchButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dMarch Lootbox");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 40");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 40) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootbox give " + player.getName() + " march 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 40"));
                profile.setCoins(profile.getCoins() - 40);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new LootboxMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class tentythreButton extends Button {

        public String getName(Player player) {
            return CC.translate("&d2023 Lootbox");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 45");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 45) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootbox give " + player.getName() + " 2023 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 45"));
                profile.setCoins(profile.getCoins() - 45);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new LootboxMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

}