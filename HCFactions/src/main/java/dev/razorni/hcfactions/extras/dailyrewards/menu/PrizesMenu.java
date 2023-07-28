package dev.razorni.hcfactions.extras.dailyrewards.menu;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.dailyrewards.DailyManager;
import dev.razorni.hcfactions.users.User;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PrizesMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&ePrizes");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(8, new GlassButton());
        buttons.put(9, new GlassButton());
        buttons.put(20, new DailyRewardsButton());
        buttons.put(24, new CoinsDailyButton());
        buttons.put(22, new PlaytimeButton());
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
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class DailyRewardsButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dDaily Rewards");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.DIAMOND, 1);
        }

        public String getLore(Player player) {
            long reclaimLong = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getDailyTimeLeft() - System.currentTimeMillis();
            User user = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (reclaimLong > 0L) {
                return CC.translate("&fCooldown: &d" + DurationFormatUtils.formatDuration(user.getDailyTimeLeft() - System.currentTimeMillis(), "HH:mm:ss", true));
            }
            return CC.translate("&fClick to redeem rewards.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            long reclaimLong = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getDailyTimeLeft() - System.currentTimeMillis();
            User user = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (reclaimLong > 0L) {
                player.sendMessage(CC.translate("&cYou cannot use this for another &l" + DurationFormatUtils.formatDurationWords(user.getDailyTimeLeft() - System.currentTimeMillis(), true, true)));
                return;
            }
            new DailyManager().performCommand(player);
        }


        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class CoinsDailyButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dDaily Coins");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLD_NUGGET, 1);
        }

        public String getLore(Player player) {
            long reclaimLong = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getCoinsleft() - System.currentTimeMillis();
            User user = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (reclaimLong > 0L) {
                return CC.translate("&fCooldown: &d" + DurationFormatUtils.formatDuration(user.getCoinsleft() - System.currentTimeMillis(), "HH:mm:ss", true));
            }
            return CC.translate("&fClick to redeem rewards.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            long reclaimLong = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getCoinsleft() - System.currentTimeMillis();
            User user = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (reclaimLong > 0L) {
                player.sendMessage(CC.translate("&cYou cannot use this for another &l" + DurationFormatUtils.formatDurationWords(user.getCoinsleft() - System.currentTimeMillis(), true, true)));
                return;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "coins add " + player.getName() + " 25");
            user.setCoinsTime(86400000L);
            user.save();
        }


        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class PlaytimeButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dPlaytime Rewards");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.getMaterial(346 + 1), 1);
        }

        public String getLore(Player player) {
            return CC.translate("&fClick to see rewards.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new PlaytimeMenu().openMenu(player);
        }


        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

}