package dev.razorni.core.extras.virtualshop.menu;

import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankCategoryMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eRanks Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
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
        buttons.put(21, new PermamentShop());
        buttons.put(23, new TemporarilyShop());

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
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class PermamentShop extends Button {


        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dRanks &7(6 Total)"));
            list.add(CC.translate(" &d◆ &bApprentice &fRank &7(Permament)"));
            list.add(CC.translate(" &d◆ &aRogue &fRank &7(Permament)"));
            list.add(CC.translate(" &d◆ &2Sentinel &fRank &7(Permament)"));
            list.add(CC.translate(" &d◆ &6Prophet &fRank &7(Permament)"));
            list.add(CC.translate(" &d◆ &eChancellor &fRank &7(Permament)"));
            list.add(CC.translate(" &d◆ &cImmortal &7(Top Permament Rank)"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dPermament Ranks");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new PermRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class TemporarilyShop extends Button {


        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dRanks &7(6 Total)"));
            list.add(CC.translate(" &d◆ &bApprentice &fRank &7(14 Days)"));
            list.add(CC.translate(" &d◆ &aRogue &fRank &7(14 Days)"));
            list.add(CC.translate(" &d◆ &2Sentinel &fRank &7(14 Days)"));
            list.add(CC.translate(" &d◆ &6Prophet &fRank &7(14 Days)"));
            list.add(CC.translate(" &d◆ &eChancellor &fRank &7(14 Days)"));
            list.add(CC.translate(" &d◆ &cImmortal &7(Top 14 Days Rank)"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dTemporarily Ranks");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new TempRanksMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

}