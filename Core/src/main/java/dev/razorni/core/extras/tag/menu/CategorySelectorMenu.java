package dev.razorni.core.extras.tag.menu;

import dev.razorni.core.extras.tag.menu.buttons.ResetTagButton;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.razorni.core.util.Skull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CategorySelectorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.GOLD + "Prefixes";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(4, new ResetTagButton());
        buttons.put(13, new CustomButton());
        buttons.put(13 + 9 + 9 - 2, new TextsButton());
        buttons.put(13 + 9 + 9 + 2, new SymbolsButton());
        buttons.put(13 + 9 + 9, new CountriesButton());
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
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }

    @AllArgsConstructor
    public static class CustomButton extends Button {


        public String getName() {
            return CC.GOLD + ("Customs Prefixes");
        }

        public ItemStack getMaterial(Player player) {
            return new Skull().getSkull("http://textures.minecraft.net/texture/6a64581320bfac755d844ace4fb9ceccac656c8c2211ed749af646af2f620463", UUID.fromString("2d994a7a-ee8a-4620-a4ee-787affa9801e"));
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new CustomTagsMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName()).setGlowing(true).lore(CC.GRAY + "Click to open!").build();
        }
    }

    @AllArgsConstructor
    public static class CountriesButton extends Button {


        public String getName(Player player) {
            return CC.GOLD + ("Countries Prefixes");
        }

        public ItemStack getMaterial() {
            return new Skull().getSkull("http://textures.minecraft.net/texture/2ae71cdb9be76611b1ee7fb43295664280af22f08cab4f703b37e0d430c377", UUID.fromString("a9b9efbb-f483-43fe-b3fb-70f04049033f"));
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new CountryTagsMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial()).name(getName(player)).setGlowing(true).lore(CC.GRAY + "Click to open!").build();
        }
    }

    @AllArgsConstructor
    public static class TextsButton extends Button {


        public String getName(Player player) {
            return CC.GOLD + ("Texts Prefixes");
        }

        public ItemStack getMaterial(Player player) {
            return new Skull().getSkull("http://textures.minecraft.net/texture/fa2afa7bb063ac1ff3bbe08d2c558a7df2e2bacdf15dac2a64662dc40f8fdbad", UUID.randomUUID());
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new TextTagsMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).lore(CC.GRAY + "Click to open!").build();
        }
    }

    @AllArgsConstructor
    public static class SymbolsButton extends Button {


        public String getName(Player player) {
            return CC.GOLD + ("Symbols Prefixes");
        }

        public ItemStack getMaterial(Player player) {
            return new Skull().getSkull("http://textures.minecraft.net/texture/d9980c1d211809a9b6565088f56a38f2ef49115c1054fa66245122e9eeedecc2", UUID.randomUUID());
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            new SymbolTagsMenu().openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).lore(CC.GRAY + "Click to open!").build();
        }
    }

}
