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

public class CrateMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eCrate Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(8, new GlassButton());
        buttons.put(11, new SilverButton());
        buttons.put(12, new FateButton());
        buttons.put(13, new OPButton());
        buttons.put(14, new OrangeButton());
        buttons.put(15, new GambleButton());
        buttons.put(3, new AbilityButton());
        buttons.put(5, new twentytwntythreeButton());
        buttons.put(9, new GlassButton());
        buttons.put(16 + 1, new GlassButton());
        buttons.put(18, new GlassButton());
        buttons.put(19, new GlassButton());
        buttons.put(25, new GlassButton());
        buttons.put(26, new GlassButton());

        buttons.put(22, new BackButton(new VirtualCategoryMenu()));


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
    public static class OrangeButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dMarch Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 20");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 20) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " March 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 20"));
                profile.setCoins(profile.getCoins() - 20);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class twentytwntythreeButton extends Button {

        public String getName(Player player) {
            return CC.translate("&d2023 Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.DIAMOND, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 20");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 20) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " 2023 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 20"));
                profile.setCoins(profile.getCoins() - 20);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class GambleButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dGamble Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1, (short) 2);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d⛁ 20");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getCoins() < 20) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Gamble 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 20"));
                profile.setCoins(profile.getCoins() - 20);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class AbilityButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dAbility Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1, (short) 6);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " ability 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 15"));
                profile.setCoins(profile.getCoins() - 15);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class PartnerButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dPartner Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GHAST_TEAR, 1);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " partner 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 25"));
                profile.setCoins(profile.getCoins() - 25);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class SilverButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dSilver Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1, (short) 12);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " silver 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 10"));
                profile.setCoins(profile.getCoins() - 10);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class FateButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dFate Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1, (short) 9);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " fate 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 10"));
                profile.setCoins(profile.getCoins() - 10);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class OPButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dOP Crate");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " op 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 10"));
                profile.setCoins(profile.getCoins() - 10);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new CrateMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }


}
