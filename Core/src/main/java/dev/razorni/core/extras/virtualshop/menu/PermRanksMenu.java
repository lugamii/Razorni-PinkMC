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

public class PermRanksMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&ePerm Ranks Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
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

        buttons.put(40, new BackButton(new RankCategoryMenu()));
        buttons.put(12, new ApprenticeButton());
        buttons.put(14, new RogueButton());
        buttons.put(12 + 9, new SentinelButton());
        buttons.put(12 + 11, new ProphetButton());
        buttons.put(12 + 18, new ChancellorButton());
        buttons.put(12 + 20, new ImmortalButton());

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
    public static class ApprenticeButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 100"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&bApprentice &fRank");
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + player.getName() + " Apprentice perm Bought");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "alert &c&lStore &7➥&c " + player.getName() + " &fhas purchased &bApprentice &frank. &7(/buy)");
                player.sendMessage(CC.GREEN + "Successfully bought Apprentice from Shop.");
                Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 100);
                Profile.getByUuid(player.getUniqueId()).save();
                new VirtualMenuCategory().openMenu(player);
                new PermRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class RogueButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 150"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&aRogue &fRank");
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
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + player.getName() + " Rogue perm Bought");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "alert &c&lStore &7➥&c " + player.getName() + " &fhas purchased &aRogue &frank. &7(/buy)");
            player.sendMessage(CC.GREEN + "Successfully bought Rogue from Shop.");
            Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 150);
            Profile.getByUuid(player.getUniqueId()).save();
            new VirtualMenuCategory().openMenu(player);
            new PermRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class SentinelButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 200"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&2Sentinel &fRank");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (Profile.getByUuid(player.getUniqueId()).getCoins() < 200) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
                return;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + player.getName() + " Sentinel perm Bought");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "alert &c&lStore &7➥&c " + player.getName() + " &fhas purchased &2Sentinel &frank. &7(/buy)");
            player.sendMessage(CC.GREEN + "Successfully bought Sentinel from Shop.");
            Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 200);
            Profile.getByUuid(player.getUniqueId()).save();
            new VirtualMenuCategory().openMenu(player);
            new PermRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class ProphetButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 250"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&6Prophet &fRank");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (Profile.getByUuid(player.getUniqueId()).getCoins() < 250) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
                return;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + player.getName() + " Prophet perm Bought");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "alert &c&lStore &7➥&c " + player.getName() + " &fhas purchased &6Prophet &frank. &7(/buy)");
            player.sendMessage(CC.GREEN + "Successfully bought Prophet from Shop.");
            Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 250);
            Profile.getByUuid(player.getUniqueId()).save();
            new VirtualMenuCategory().openMenu(player);
            new PermRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class ChancellorButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 300"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&eChancellor &fRank");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (Profile.getByUuid(player.getUniqueId()).getCoins() < 300) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
                return;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + player.getName() + " Chancellor perm Bought");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "alert &c&lStore &7➥&c " + player.getName() + " &fhas purchased &eChancellor &frank. &7(/buy)");
            player.sendMessage(CC.GREEN + "Successfully bought Chancellor from Shop.");
            Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 300);
            Profile.getByUuid(player.getUniqueId()).save();
            new VirtualMenuCategory().openMenu(player);
            new PermRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class ImmortalButton extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&ePrice: &d⛁ 350"));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&cImmortal &fRank");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (Profile.getByUuid(player.getUniqueId()).getCoins() < 350) {
                player.sendMessage(CC.translate("&cYou dont have enough ⛁ to buy this."));
                return;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + player.getName() + " Immortal perm Bought");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "alert &c&lStore &7➥&c " + player.getName() + " &fhas purchased &cImmortal &frank. &7(/buy)");
            player.sendMessage(CC.GREEN + "Successfully bought Immortal from Shop.");
            Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() - 350);
            Profile.getByUuid(player.getUniqueId()).save();
            new VirtualMenuCategory().openMenu(player);
            new PermRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }


}
