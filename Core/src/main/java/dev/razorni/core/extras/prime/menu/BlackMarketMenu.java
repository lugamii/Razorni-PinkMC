package dev.razorni.core.extras.prime.menu;

import dev.razorni.core.Core;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.server.ServerType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.button.BackButton;
import dev.razorni.hcfactions.HCF;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BlackMarketMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eDeep Web Shop ✪");
    }

    @Override
    public int size(Player player) {
        return 9 * 5;
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

        buttons.put(40, new BackButton(new PrimeCategoryMenu()));
        buttons.put(4, new PrimeCategoryMenu.PrimeStatusButton());
        buttons.put(20, new AprilButton());
        buttons.put(21, new framesButton());
        buttons.put(22, new newyearlootboxButton());
        buttons.put(23, new AirdropButton());
        buttons.put(24, new AbilityPackageButton());

        setAutoUpdate(true);

        return buttons;
    }

    @AllArgsConstructor
    public static class GlassButton extends Button {


        public String getName(Player player) {
            return CC.translate(" ");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class AprilButton extends Button {

        public String getName(Player player) {
            return CC.translate("&cApril Lootbox");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d$15000");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                player.sendMessage(CC.translate("&cYou dont have access to this Prime feature. &7(/buy)"));
                return;
            }
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                player.sendMessage(CC.translate("&c⚠ You must be on HCF server to buy this item."));
                return;
            }
            if (HCF.getPlugin().getBalanceManager().getBalance(player.getUniqueId()) < 15000) {
                player.sendMessage(CC.translate("&cYou dont have enough money to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootbox give " + player.getName() + " april 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d$15000"));
                HCF.getPlugin().getBalanceManager().takeBalance(player, 15000);
                new PrimeCategoryMenu().openMenu(player);
                new BlackMarketMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class AbilityPackageButton extends Button {

        public String getName(Player player) {
            return CC.translate("&cAbility Package");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d$10000");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                player.sendMessage(CC.translate("&cYou dont have access to this Prime feature. &7(/buy)"));
                return;
            }
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                player.sendMessage(CC.translate("&c⚠ You must be on HCF server to buy this item."));
                return;
            }
            if (HCF.getPlugin().getBalanceManager().getBalance(player.getUniqueId()) < 10000) {
                player.sendMessage(CC.translate("&cYou dont have enough money to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " abilitypackage 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d$10000"));
                HCF.getPlugin().getBalanceManager().takeBalance(player, 10000);
                new PrimeCategoryMenu().openMenu(player);
                new BlackMarketMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class newyearlootboxButton extends Button {

        public String getName(Player player) {
            return CC.translate("&c2023 Lootbox");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d$15000");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                player.sendMessage(CC.translate("&cYou dont have access to this Prime feature. &7(/buy)"));
                return;
            }
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                player.sendMessage(CC.translate("&c⚠ You must be on HCF server to buy this item."));
                return;
            }
            if (HCF.getPlugin().getBalanceManager().getBalance(player.getUniqueId()) < 15000) {
                player.sendMessage(CC.translate("&cYou dont have enough money to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootbox give " + player.getName() + " 2023 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d$15000"));
                HCF.getPlugin().getBalanceManager().takeBalance(player, 15000);
                new PrimeCategoryMenu().openMenu(player);
                new BlackMarketMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class framesButton extends Button {

        public String getName(Player player) {
            return CC.translate("&cEnd Portal Frames");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_PORTAL_FRAME, 12);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d$3500");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                player.sendMessage(CC.translate("&cYou dont have access to this Prime feature. &7(/buy)"));
                return;
            }
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                player.sendMessage(CC.translate("&c⚠ You must be on HCF server to buy this item."));
                return;
            }
            if (HCF.getPlugin().getBalanceManager().getBalance(player.getUniqueId()) < 3500) {
                player.sendMessage(CC.translate("&cYou dont have enough money to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getName() + " minecraft:end_portal_frame 12");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x12 &efor &d$3500"));
                HCF.getPlugin().getBalanceManager().takeBalance(player, 3500);
                new PrimeCategoryMenu().openMenu(player);
                new BlackMarketMenu().openMenu(player);
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
            return CC.translate("&cAirdrop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.DISPENSER, 1);
        }

        public String getLore() {
            return CC.translate("&ePrice: &d$4000");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                player.sendMessage(CC.translate("&cYou dont have access to this Prime feature. &7(/buy)"));
                return;
            }
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                player.sendMessage(CC.translate("&c⚠ You must be on HCF server to buy this item."));
                return;
            }
            if (HCF.getPlugin().getBalanceManager().getBalance(player.getUniqueId()) < 4000) {
                player.sendMessage(CC.translate("&cYou dont have enough money to buy this."));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "airdrops give " + player.getName() + " 1");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d$4000"));
                HCF.getPlugin().getBalanceManager().takeBalance(player, 4000);
                new PrimeCategoryMenu().openMenu(player);
                new BlackMarketMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

}

