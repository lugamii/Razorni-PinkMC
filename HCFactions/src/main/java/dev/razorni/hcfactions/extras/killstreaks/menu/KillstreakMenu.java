package dev.razorni.hcfactions.extras.killstreaks.menu;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.HCF;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KillstreakMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eKillstreak Rewards");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(8, new GlassButton());
        buttons.put(9, new GlassButton());
        buttons.put(11, new FirstButton());
        buttons.put(12, new SecondButton());
        buttons.put(13, new ThirdButton());
        buttons.put(14, new FourButton());
        buttons.put(15, new FiveButton());
        buttons.put(21, new SixButton());
        buttons.put(22, new SevenButton());
        buttons.put(23, new EightButton());
        buttons.put(31, new StatsButton());
        buttons.put(16 + 1, new GlassButton());
        buttons.put(26 + 1, new GlassButton());
        buttons.put(35, new GlassButton());
        buttons.put(36, new GlassButton());
        buttons.put(36 + 1, new GlassButton());
        buttons.put(43, new GlassButton());
        buttons.put(44, new GlassButton());

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
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class StatsButton extends Button {

        public String getName(Player player) {
            return ChatColor.GOLD + "Your Killstreak";
        }

        public String getLore(Player player) {
            return CC.translate("&6&l┃ &fKillstreak: &6" + HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getKillstreak());
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM).durability(3).skull(player.getName()).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class FirstButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6x5 Golden Apples");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLDEN_APPLE, 5);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &65");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class SecondButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6x35 Coins");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLD_NUGGET, 35);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &610");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class ThirdButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6Debuffs");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.POTION, 1, (short) 16426);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &615");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class FourButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6x3 Reward Keys");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.TRIPWIRE_HOOK, 3);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &620");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class FiveButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6Strenght");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.POTION, 1, (short) 16393);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &625");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class SixButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6Invis");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.POTION, 1, (short) 16430);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &630");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class SevenButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6God Apple");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &635");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class EightButton extends Button {


        public String getName(Player player) {
            return CC.translate("&6Permament Speed II");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.SUGAR, 1);
        }

        public String getLore() {
            return CC.translate("&6&l┃ &fRequired Kills: &640");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore()).name(getName(player)).build();
        }
    }

}