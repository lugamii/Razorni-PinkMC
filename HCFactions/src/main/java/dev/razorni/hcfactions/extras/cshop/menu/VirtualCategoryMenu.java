package dev.razorni.hcfactions.extras.cshop.menu;

import dev.razorni.core.extras.virtualshop.menu.VirtualMenuCategory;
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

public class VirtualCategoryMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&eHCF Coins Shop &7┃ &e⛁ " + Profile.getByUuid(player.getUniqueId()).getCoins());
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

        buttons.put(20, new AbilityButton());
        buttons.put(21, new GKitsButton());
        buttons.put(22, new CrateButton());
        buttons.put(23, new LootboxButton());
        buttons.put(24, new MiscButton());
        buttons.put(40, new BackButton(new VirtualMenuCategory()));

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
    public static class AbilityButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dAbility Coins Shop");
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dAbility Items &7(9 Total)"));
            list.add(CC.translate(" &7- &fSwitcher Ability"));
            list.add(CC.translate(" &7- &fCombo Ability"));
            list.add(CC.translate(" &7- &fInvisibility Ability"));
            list.add(CC.translate(" &7- &fThunderbolt Ability"));
            list.add(CC.translate(" &7- &fPocketBard Ability"));
            list.add(CC.translate(" &7- &fAntiBuild Ability"));
            list.add(CC.translate(" &7- &fNinja Ability"));
            list.add(CC.translate(" &7- &fFocus Ability"));
            list.add(CC.translate(" &7- &fRage Ability"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&dPackages & Bundles &7(4 Total)"));
            list.add(CC.translate(" &7- &fAbility Bundle"));
            list.add(CC.translate(" &7- &fOP Ability Bundle"));
            list.add(CC.translate(" &7- &fInsane Ability Bundle"));
            list.add(CC.translate(" &7- &fAbility Package"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
                new AbilityMenu().openMenu(player);
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.NETHER_STAR, 1);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class GKitsButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dGKits Coins Shop");
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dItems &7(5 Total)"));
            list.add(CC.translate(" &7- &fLegendary Diamond GKit"));
            list.add(CC.translate(" &7- &fLegendary Bard GKit"));
            list.add(CC.translate(" &7- &fLegendary Rogue GKit"));
            list.add(CC.translate(" &7- &fLegendary Archer GKit"));
            list.add(CC.translate(" &7- &fLegendary Miner GKit"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new GKitsMenu().openMenu(player);
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.DIAMOND_SWORD, 1);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class CrateButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dCrate Coins Shop");
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dItems &7(8 Total)"));
            list.add(CC.translate(" &7- &fSilver Key"));
            list.add(CC.translate(" &7- &fFate Key"));
            list.add(CC.translate(" &7- &fOP Key"));
            list.add(CC.translate(" &7- &f2023 Key"));
            list.add(CC.translate(" &7- &fMarch Key"));
            list.add(CC.translate(" &7- &fGamble Key"));
            list.add(CC.translate(" &7- &fAbility Key"));
            list.add(CC.translate(" &7- &fPartner Key"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new CrateMenu().openMenu(player);
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.TRIPWIRE_HOOK, 1);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class LootboxButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dLootbox Coins Shop");
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dItems &7(2 Total)"));
            list.add(CC.translate(" &7- &fMarch Lootbox"));
            list.add(CC.translate(" &7- &f2023 Lootbox"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new LootboxMenu().openMenu(player);
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class MiscButton extends Button {


        public String getName(Player player) {
            return CC.translate("&dMiscellaenous Coins Shop");
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate(" "));
            list.add(CC.translate("&dItems &7(3 Total)"));
            list.add(CC.translate(" &7- &fCobweb"));
            list.add(CC.translate(" &7- &fFishing Rod"));
            list.add(CC.translate(" &7- &fAirdrop"));
            list.add(CC.translate(" "));
            list.add(CC.translate("&aClick to open this shop."));
            return list;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new MiscellaenousMenu().openMenu(player);
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.WEB, 1);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

}
