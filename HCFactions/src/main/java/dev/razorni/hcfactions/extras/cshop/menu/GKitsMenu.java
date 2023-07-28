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

public class GKitsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eGKits Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(8, new GlassButton());
        buttons.put(11, new DiamondButton());
        buttons.put(12, new BardButton());
        buttons.put(13, new ArcherButton());
        buttons.put(14, new RogueButton());
        buttons.put(15, new MinerButton());
        buttons.put(9, new GlassButton());
        buttons.put(16 + 1, new GlassButton());
        buttons.put(22, new BackButton(new VirtualCategoryMenu()));
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
    public static class DiamondButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dLegendary Diamond GKit");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.DIAMOND_HELMET, 1);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gkm apply " + player.getName() + " LegendaryDiamond");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 35"));
                profile.setCoins(profile.getCoins() - 35);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new GKitsMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class BardButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dLegendary Bard GKit");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLD_HELMET, 1);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gkm apply " + player.getName() + " LegendaryBard");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 35"));
                profile.setCoins(profile.getCoins() - 35);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new GKitsMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class ArcherButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dLegendary Archer GKit");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.LEATHER_HELMET, 1);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gkm apply " + player.getName() + " LegendaryArcher");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 35"));
                profile.setCoins(profile.getCoins() - 35);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new GKitsMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class RogueButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dLegendary Rogue GKit");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.CHAINMAIL_HELMET, 1);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gkm apply " + player.getName() + " LegendaryRogue");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 35"));
                profile.setCoins(profile.getCoins() - 35);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new GKitsMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }

    @AllArgsConstructor
    public static class MinerButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dLegendary Miner GKit");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.IRON_HELMET, 1);
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gkm apply " + player.getName() + " LegendaryMiner");
                player.sendMessage(CC.translate("&eSuccessfully purchased " + getName(player) + " x1 &efor &d⛁ 35"));
                profile.setCoins(profile.getCoins() - 35);
                profile.save();
                new VirtualCategoryMenu().openMenu(player);
                new GKitsMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore()).build();
        }
    }


}
