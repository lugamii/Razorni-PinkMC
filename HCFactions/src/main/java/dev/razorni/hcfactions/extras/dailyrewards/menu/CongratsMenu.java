package dev.razorni.hcfactions.extras.dailyrewards.menu;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.utils.menuapi.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CongratsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&aCongrats");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int x = 0; x < this.size(player); x++) {
            buttons.put(x, new GlassButton());
        }

        buttons.put(40, new BackButton(new PlaytimeMenu()));
        buttons.put(22, new CongratsButton());

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
    public static class CongratsButton extends Button {

        public String getName(Player player) {
            return CC.translate("&a&lCONGRATS!");
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.PAPER, 1);
        }

        public List<String> getLore(Player player) {
            List<String> list = new ArrayList<>();
            list.add(CC.translate("&6&l┃ &fThanks for being such a loyal player."));
            list.add(CC.translate("&6&l┃ &fBecause you are playing on our server you,"));
            list.add(CC.translate("&6&l┃ &fhave been awarded with additional rewards. &c&l❤"));
            return list;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

}
