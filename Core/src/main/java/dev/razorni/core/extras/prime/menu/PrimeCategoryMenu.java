package dev.razorni.core.extras.prime.menu;

import dev.razorni.core.Core;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.server.ServerType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.hcfactions.HCF;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrimeCategoryMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&ePrime Pass Shop ✪");
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

        buttons.put(4, new PrimeStatusButton());
        buttons.put(23, new DeepWebButton());
        buttons.put(21, new PrimeDailyCoinsButton());

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
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class PrimeStatusButton extends Button {

        public String getName(Player player) {
            return CC.translate("&d" + player.getName() + "'s Prime Pass");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        }

        public List<String> getLore(Player player) {
            String prime = Profile.getByUuid(player.getUniqueId()).isPrime() ? CC.GREEN + "Purchased " + CC.GOLD + "✪" : CC.RED + "Not Purchased";
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" &d◆ &fStatus: " + prime));
            if (Core.getInstance().getServerType() == ServerType.HCF) {
                list.add(CC.translate(" &d◆ &fBalance: &a$" + HCF.getPlugin().getBalanceManager().getBalance(player.getUniqueId())));
            }
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                list.add(" ");
                list.add(CC.translate("&cYou dont have access to Prime features. &7(/buy)"));
            }
            return list;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).skull(player.getName()).build();
        }
    }

    @AllArgsConstructor
    public static class DeepWebButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dDeep Web Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.SKULL_ITEM, 1);
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&fPurchase unique items from black market."));
            list.add(" ");
            list.add(CC.translate("&d&lNote &7➥ &cCare when using this shop,"));
            list.add(CC.translate("&cthere is small chance of getting you banned."));
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                player.sendMessage(CC.translate("&cYou dont have access to this Prime feature. &7(/buy)"));
            } else {
                new BlackMarketMenu().openMenu(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).skull(player.getName()).build();
        }
    }

    @AllArgsConstructor
    public static class PrimeDailyCoinsButton extends Button {

        public String getName(Player player) {
            return CC.translate("&dPrime Daily");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.GOLD_NUGGET, 1);
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            long primedaily = Profile.getByUuid(player.getUniqueId()).getPrimedaily() - System.currentTimeMillis();
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                list.add(CC.translate("&cYou dont have access to this Prime feature."));
            }
            if (primedaily > 0L) {
                list.add(CC.translate("&fYou are on cooldown for &d" + DurationFormatUtils.formatDurationWords(Profile.getByUuid(player.getUniqueId()).getPrimedaily() - System.currentTimeMillis(), true, true) + "."));
            } else {
                list.add(CC.translate("&fClick here to redeem &d⛁ 10 and $10000."));
            }
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                player.sendMessage(CC.translate("&c⚠ You must be on HCF server to redeem this."));
                return;
            }
            long primedaily = Profile.getByUuid(player.getUniqueId()).getPrimedaily() - System.currentTimeMillis();
            if (!Profile.getByUuid(player.getUniqueId()).isPrime()) {
                player.sendMessage(CC.translate("&cYou dont have access to this Prime feature. &7(/buy)"));
                return;
            }

            if (Profile.getByUuid(player.getUniqueId()).getPrimedaily()  > 0L) {
                player.sendMessage(CC.translate("&cYou are on cooldown for " + DurationFormatUtils.formatDurationWords(Profile.getByUuid(player.getUniqueId()).getPrimedaily() - System.currentTimeMillis(), true, true) + "."));
                return;
            }

            Profile.getByUuid(player.getUniqueId()).setCoins(Profile.getByUuid(player.getUniqueId()).getCoins() + 10);
            Profile.getByUuid(player.getUniqueId()).setPrimeTime(86400000L);
            HCF.getPlugin().getBalanceManager().giveBalance(player, 10000);
            Profile.getByUuid(player.getUniqueId()).save();
            player.sendMessage(CC.translate("&aYou have successfully redeemed ⛁ 10 and $10000."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

}
