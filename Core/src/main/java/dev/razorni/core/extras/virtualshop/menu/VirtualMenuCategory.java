package dev.razorni.core.extras.virtualshop.menu;

import dev.razorni.core.Core;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.server.ServerType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualMenuCategory  extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eVirtual Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
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

        buttons.put(21, new HCFShop());
        buttons.put(23, new GlobalShop());

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
    public static class HCFShop extends Button {


        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                list.add(CC.translate("&4⚠ You must be on HCF server to open this shop."));
            }
            list.add(CC.translate(" "));
            list.add(CC.translate("&dCategories &7(5 Total)"));
            list.add(CC.translate(" &d◆ &fAbility Shop"));
            list.add(CC.translate(" &d◆ &fGKits Shop"));
            list.add(CC.translate(" &d◆ &fCrate Shop"));
            list.add(CC.translate(" &d◆ &fMiscellaenous Shop"));
            list.add(CC.translate(" &d◆ &fLootbox Shop"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dHCF Coins Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_PEARL, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!(Core.getInstance().getServerType() == ServerType.HCF)) {
                player.sendMessage(CC.RED + "You must be on HCF server to open this shop.");
                return;
            }
            player.chat("/coinshop");
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class GlobalShop extends Button {

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dCategories &7(3 Total)"));
            list.add(CC.translate(" &d◆ &fRanks Shop"));
            list.add(CC.translate(" &d◆ &fTags Shop"));
            list.add(CC.translate(" &d◆ &fPunishments Shop"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        public String getName(Player player) {
            return CC.translate("&dGlobal Coins Shop");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new CategorySelectorMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

}