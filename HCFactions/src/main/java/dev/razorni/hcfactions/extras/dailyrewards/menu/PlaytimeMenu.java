package dev.razorni.hcfactions.extras.dailyrewards.menu;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.utils.menuapi.menu.button.BackButton;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.listeners.type.MainListener;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.Formatter;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlaytimeMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&ePlaytime Rewards");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new PrizesMenu.GlassButton());
        buttons.put(1, new PrizesMenu.GlassButton());
        buttons.put(6 + 1, new PrizesMenu.GlassButton());
        buttons.put(8, new PrizesMenu.GlassButton());
        buttons.put(9, new PrizesMenu.GlassButton());
        buttons.put(13, new PlaytimeButton());
        buttons.put(20, new Reward1Button());
        buttons.put(21, new Reward2Button());
        buttons.put(22, new Reward3Button());
        buttons.put(23, new Reward4Button());
        buttons.put(24, new Reward5Button());
        buttons.put(30, new Reward6Button());
        buttons.put(32, new Reward7Button());
        buttons.put(16 + 1, new PrizesMenu.GlassButton());
        buttons.put(26 + 1, new PrizesMenu.GlassButton());
        buttons.put(35, new PrizesMenu.GlassButton());
        buttons.put(36, new PrizesMenu.GlassButton());
        buttons.put(40, new BackButton(new PrizesMenu()));
        buttons.put(36 + 1, new PrizesMenu.GlassButton());
        buttons.put(43, new PrizesMenu.GlassButton());
        buttons.put(44, new PrizesMenu.GlassButton());

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
    public static class PlaytimeButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dYour Playtime");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.getMaterial(346 + 1), 1);
        }

        public String getLore(Player player) {
            return CC.translate("&f" + Formatter.formatDetailed(HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getPlaytime() + MainListener.getPlaySession().getCurrentSession()));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class Reward1Button extends Button {

        public String getName(Player player) {
            return CC.translate("&dReward #1");
        }

        public ItemStack getMaterial(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L) {
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            } else if (!profile.isReward1()) {
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            }
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L) {
                return CC.translate("&fYou must have minimum playtime of 1 day.");
            } else if (!profile.isReward1()) {
                return CC.translate("&fClick to redeem reward.");
            }
            return CC.translate("&fAlready redeemed reward.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L) {
                player.sendMessage(CC.translate("&cYou must have minimum playtime of 1 day."));
                return;
            } else if (!profile.isReward1()) {
                player.sendMessage(CC.translate("&aYou have received the playtime rewards #1 rewards."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "airdrops give " + player.getName() + " 5");
                profile.setReward1(true);
                profile.save();
                return;
            }
            player.sendMessage(CC.translate("&cYou have already claimed this reward."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class Reward2Button extends Button {

        public String getName(Player player) {
            return CC.translate("&dReward #2");
        }

        public ItemStack getMaterial(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 2) {
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            } else if (!profile.isReward2()) {
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            }
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 2) {
                return CC.translate("&fYou must have minimum playtime of 2 days.");
            } else if (!profile.isReward2()) {
                return CC.translate("&fClick to redeem reward.");
            }
            return CC.translate("&fAlready redeemed reward.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 2) {
                player.sendMessage(CC.translate("&cYou must have minimum playtime of 2 days."));
                return;
            } else if (!profile.isReward2()) {
                player.sendMessage(CC.translate("&aYou have received the playtime rewards #2 rewards."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " AbilityPackage 5");
                profile.setReward2(true);
                profile.save();
                return;
            }
            player.sendMessage(CC.translate("&cYou have already claimed this reward."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class Reward3Button extends Button {

        public String getName(Player player) {
            return CC.translate("&dReward #3");
        }

        public ItemStack getMaterial(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 3) {
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            } else if (!profile.isReward3()) {
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            }
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 3) {
                return CC.translate("&fYou must have minimum playtime of 3 days.");
            } else if (!profile.isReward3()) {
                return CC.translate("&fClick to redeem reward.");
            }
            return CC.translate("&fAlready redeemed reward.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 3) {
                player.sendMessage(CC.translate("&cYou must have minimum playtime of 3 days."));
                return;
            } else if (!profile.isReward3()) {
                player.sendMessage(CC.translate("&aYou have received the playtime rewards #3 rewards."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " Gamble 3");
                profile.setReward3(true);
                profile.save();
                return;
            }
            player.sendMessage(CC.translate("&cYou have already claimed this reward."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class Reward4Button extends Button {

        public String getName(Player player) {
            return CC.translate("&dReward #4");
        }

        public ItemStack getMaterial(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 4) {
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            } else if (!profile.isReward4()) {
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            }
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 4) {
                return CC.translate("&fYou must have minimum playtime of 4 days.");
            } else if (!profile.isReward4()) {
                return CC.translate("&fClick to redeem reward.");
            }
            return CC.translate("&fAlready redeemed reward.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 4) {
                player.sendMessage(CC.translate("&cYou must have minimum playtime of 4 days."));
                return;
            } else if (!profile.isReward4()) {
                player.sendMessage(CC.translate("&aYou have received the playtime rewards #4 rewards."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " invisibility 3");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " antibuild 3");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability give " + player.getName() + " Switcher 3");
                profile.setReward4(true);
                profile.save();
                return;
            }
            player.sendMessage(CC.translate("&cYou have already claimed this reward."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class Reward5Button extends Button {

        public String getName(Player player) {
            return CC.translate("&dReward #5");
        }

        public ItemStack getMaterial(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 5) {
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            } else if (!profile.isReward5()) {
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            }
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 5) {
                return CC.translate("&fYou must have minimum playtime of 5 days.");
            } else if (!profile.isReward5()) {
                return CC.translate("&fClick to redeem reward.");
            }
            return CC.translate("&fAlready redeemed reward.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 5) {
                player.sendMessage(CC.translate("&cYou must have minimum playtime of 5 days."));
                return;
            } else if (!profile.isReward5()) {
                player.sendMessage(CC.translate("&aYou have received the playtime rewards #5 rewards."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "coins add " + player.getName() + " 50");
                profile.setReward5(true);
                profile.save();
                return;
            }
            player.sendMessage(CC.translate("&cYou have already claimed this reward."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class Reward6Button extends Button {

        public String getName(Player player) {
            return CC.translate("&dReward #6");
        }

        public ItemStack getMaterial(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 6) {
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            } else if (!profile.isReward6()) {
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            }
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 6) {
                return CC.translate("&fYou must have minimum playtime of 6 days.");
            } else if (!profile.isReward6()) {
                return CC.translate("&fClick to redeem reward.");
            }
            return CC.translate("&fAlready redeemed reward.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 6) {
                player.sendMessage(CC.translate("&cYou must have minimum playtime of 6 days."));
                return;
            } else if (!profile.isReward6()) {
                player.sendMessage(CC.translate("&aYou have received the playtime rewards #6 rewards."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootbox give " + player.getName() + " 2023 1");
                profile.setReward6(true);
                profile.save();
                return;
            }
            player.sendMessage(CC.translate("&cYou have already claimed this reward."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

    @AllArgsConstructor
    public static class Reward7Button extends Button {

        public String getName(Player player) {
            return CC.translate("&dReward #7");
        }

        public ItemStack getMaterial(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 7) {
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            } else if (!profile.isRewardseven()) {
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            }
            return new ItemStack(Material.INK_SACK, 1, (short) 1);
        }

        public String getLore(Player player) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 7) {
                return CC.translate("&fYou must have minimum playtime of 7 days.");
            } else if (!profile.isRewardseven()) {
                return CC.translate("&fClick to redeem reward.");
            }
            return CC.translate("&fAlready redeemed reward.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
            if (profile.getPlaytime() < 86400000L * 7) {
                player.sendMessage(CC.translate("&cYou must have minimum playtime of 7 days."));
                return;
            } else if (!profile.isRewardseven()) {
                player.sendMessage(CC.translate("&aYou have received the playtime rewards #7 rewards."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lootbox give " + player.getName() + " March 2");
                Bukkit.getServer().broadcastMessage(CC.translate("&c[Playtime Rewards] " + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName() + " &fhas just claimed his last playtime reward by using command &c/prizes"));
                player.playEffect(EntityEffect.FIREWORK_EXPLODE);
                profile.setRewardseven(true);
                profile.save();
                new CongratsMenu().openMenu(player);
                return;
            }
            player.sendMessage(CC.translate("&cYou have already claimed this reward."));
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getLore(player)).build();
        }
    }

}
