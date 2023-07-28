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

public class CategorySelectorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eGlobal Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new VirtualMenuCategory.GlassButton());
        buttons.put(1, new VirtualMenuCategory.GlassButton());
        buttons.put(6 + 1, new VirtualMenuCategory.GlassButton());
        buttons.put(8, new VirtualMenuCategory.GlassButton());
        buttons.put(9, new VirtualMenuCategory.GlassButton());
        buttons.put(16 + 1, new VirtualMenuCategory.GlassButton());
        buttons.put(26 + 1, new VirtualMenuCategory.GlassButton());
        buttons.put(35, new VirtualMenuCategory.GlassButton());
        buttons.put(36, new VirtualMenuCategory.GlassButton());
        buttons.put(36 + 1, new VirtualMenuCategory.GlassButton());
        buttons.put(43, new VirtualMenuCategory.GlassButton());
        buttons.put(44, new VirtualMenuCategory.GlassButton());

        buttons.put(21, new PunishmentsShop());
        buttons.put(22, new TagsShop());
        buttons.put(23, new RanksShop());

        buttons.put(40, new BackButton(new VirtualMenuCategory()));

        setAutoUpdate(true);


        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9 * 5;
    }


    @AllArgsConstructor
    public static class PunishmentsShop extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dType &7(3 Total)"));
            list.add(CC.translate(" &d◆ &fUnban"));
            list.add(CC.translate(" &d◆ &fUnblacklist"));
            list.add(CC.translate(" &d◆ &fUnmute"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dPunishments Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new PunishmentShopMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class RanksShop extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dPermament & Temporarily &7(6 Total)"));
            list.add(CC.translate(" &d◆ &bApprentice &fRank"));
            list.add(CC.translate(" &d◆ &aRogue &fRank"));
            list.add(CC.translate(" &d◆ &2Sentinel &fRank"));
            list.add(CC.translate(" &d◆ &6Prophet &fRank"));
            list.add(CC.translate(" &d◆ &eChancellor &fRank"));
            list.add(CC.translate(" &d◆ &cImmortal &7(Top Rank)"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dRanks Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new RankCategoryMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class TagsShop extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dCategories &7(4 Total)"));
            list.add(CC.translate(" &d◆ &fSymbol Tags"));
            list.add(CC.translate(" &d◆ &fCountry Tags"));
            list.add(CC.translate(" &d◆ &fText Tags"));
            list.add(CC.translate(" &d◆ &fCustom Tags"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dTags Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.NAME_TAG, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.sendMessage(CC.RED + "We apologize for this not working, our owner Consealment is lazy and didnt made Tags. Thanks.");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

}
