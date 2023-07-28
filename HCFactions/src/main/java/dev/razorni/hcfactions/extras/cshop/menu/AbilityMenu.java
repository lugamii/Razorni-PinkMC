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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eAbility Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new SwitcherButton());
        buttons.put(12, new ComboButton());
        buttons.put(13, new InvisibilityButton());
        buttons.put(14, new ThunderButton());
        buttons.put(15, new PocketBardButton());
        buttons.put(20, new AntiBuildButton());
        buttons.put(24, new NinjaButton());
        buttons.put(19, new FocusButton());
        buttons.put(25, new RageButton());
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

        buttons.put(22, new PackageButton());
        buttons.put(30, new Bundl1Button());
        buttons.put(31, new Bundle2Button());
        buttons.put(32, new BundleButton());
        buttons.put(40, new BackButton(new VirtualCategoryMenu()));
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
    public static class Bac2Button extends Button {


        public String getName(Player player) {
            return CC.translate("&c&lGo Back");
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(dev.razorni.core.util.CC.translate("&cClick here to return to"));
            list.add(dev.razorni.core.util.CC.translate("&cthe previous menu."));
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat("/coinshop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.REDSTONE, 1);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class SwitcherButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dSwitcher");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.SNOW_BALL, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " Switcher 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class AntiBuildButton extends Button {

        public String getName(Player player) {
            return CC.translate("&aAnti Build");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.BONE, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " antibuild 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class InvisibilityButton extends Button {

        public String getName(Player player) {
            return CC.translate("&bInvisibility");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " invisibility 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class ComboButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dCombo Ability");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GLOWSTONE_DUST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " comboability 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class PocketBardButton extends Button {

        public String getName(Player player) {
            return CC.translate("&6Pocket Bard");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1, (short) 14);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " pocketbard 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class RageButton extends Button {

        public String getName(Player player) {
            return CC.translate("&eRage Ball");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.EGG, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " rageball 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class FocusButton extends Button {

        public String getName(Player player) {
            return CC.translate("&5Focus Mode");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLD_NUGGET, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " focusmode 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class NinjaButton extends Button {

        public String getName(Player player) {
            return CC.translate("&fNinja Ability");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.NETHER_STAR, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " ninjaability 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class ThunderButton extends Button {

        public String getName(Player player) {
            return CC.translate("&4Thunderbolt");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLD_INGOT, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 7");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 7) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " LIGHTNING 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 7"));
                profile.setCoins(profile.getCoins() - 7);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class PackageButton extends Button {

        public String getName(Player player) {
            return CC.translate("&cAbility Package");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 25");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 25) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " abilitypackage 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 25"));
                profile.setCoins(profile.getCoins() - 25);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class BundleButton extends Button {

        public String getName(Player player) {
            return CC.translate("&cAbility Bundle");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 30");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 30) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " abilitybundle 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 30"));
                profile.setCoins(profile.getCoins() - 30);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }


    @AllArgsConstructor
    public static class Bundl1Button extends Button {

        public String getName(Player player) {
            return CC.translate("&cOP Ability Bundle");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 35");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 35) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " opabilitybundle 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 35"));
                profile.setCoins(profile.getCoins() - 35);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class Bundle2Button extends Button {

        public String getName(Player player) {
            return CC.translate("&cInsane Ability Bundle");
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " insaneabilitybundle 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 40"));
                profile.setCoins(profile.getCoins() - 40);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new AbilityMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

}