package dev.razorni.core.extras.virtualshop.menu;

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

public class PunishmentShopMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&ePunishments Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
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

        buttons.put(40, new BackButton(new CategorySelectorMenu()));
        buttons.put(22, new UnbanButton());
        buttons.put(21, new UnmuteButton());
        buttons.put(23, new UnblacklistButton());

        setAutoUpdate(true);


        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9 * 5;
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
    public static class UnmuteButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 50"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dUnmute");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (Profile.getByUuid(player.getUniqueId()).getCoins() < 50) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
                return;
            }
            if (Profile.getByUuid(player.getUniqueId()).getActiveMute() != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unmute " + player.getName() + " " + "Bought");
                player.sendMessage(CC.GREEN + "Successfully bought Unmute from Shop.");
                Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 50);
                Profile.getByUuid(player.getUniqueId()).save();
                new VirtualMenuCategory().openMenu(player);
                new PunishmentShopMenu().openMenu(player);
            } else {
                player.sendMessage(CC.translate("&cYou are not currently muted."));
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class UnbanButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 100"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dUnban");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (Profile.getByUuid(player.getUniqueId()).getCoins() < 100) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
                return;
            }
            if (Profile.getByUuid(player.getUniqueId()).getActiveBan() != null) {
                player.sendMessage(CC.GREEN + "Successfully bought Unban from Shop.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unban " + player.getName() + " " + "Bought");
                Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 100);
                Profile.getByUuid(player.getUniqueId()).save();
                new VirtualMenuCategory().openMenu(player);
                new PunishmentShopMenu().openMenu(player);
            } else {
                player.sendMessage(CC.translate("&cYou are not currently banned."));
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class UnblacklistButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 150"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dUnblacklist");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (Profile.getByUuid(player.getUniqueId()).getCoins() < 150) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
                return;
            }
            if (Profile.getByUuid(player.getUniqueId()).getActiveMute() != null) {
                player.sendMessage(CC.GREEN + "Successfully bought Unblacklist from Shop.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unblacklist " + player.getName() + " " + "Bought");
                Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 150);
                Profile.getByUuid(player.getUniqueId()).save();
                new VirtualMenuCategory().openMenu(player);
                new PunishmentShopMenu().openMenu(player);
            } else {
                player.sendMessage(CC.translate("&cYou are not currently blacklisted."));
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }


}
