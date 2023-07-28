package dev.razorni.hub.menu;

import dev.razorni.hub.Hub;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.Button;
import dev.razorni.hub.utils.shits.CC;
import dev.razorni.hub.utils.shits.ItemBuilder3;
import lombok.AllArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectorMenu extends Menu {

    @Override
    public int size(Player player) {
        return 9 * 5;
    }

    @Override
    public String getTitle(Player player) {
        return "Server Selector";
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

        int pos = Hub.getInstance().getSettingsConfig().getConfig().getInt("SELECTOR.HCF-ITEM-SLOT");
        buttons.put(pos, new KitsButton());
        buttons.put(40, new SummerButton());
        setAutoUpdate(true);
        return buttons;
    }

    @AllArgsConstructor
    public static class KitsButton extends Button {

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat("/play HCF");
            player.closeInventory();
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> list = new ArrayList<String>();
            for (String s :  Hub.getInstance().getSettingsConfig().getConfig().getStringList(CC.translate("SELECTOR.HCF-LORE"))) {
                list.add(PlaceholderAPI.setPlaceholders(player, s).replaceAll("%queued%", String.valueOf(Hub.getInstance().getQueueManager().getInQueue("HCF"))));
            }
            return new ItemBuilder3(Material.SKULL_ITEM, 3)
                    .setDisplayName(CC.translate(Hub.getInstance().getSettingsConfig().getConfig().getString("SELECTOR.HCF-ITEM-NAME")))
                    .setLore(CC.translate(list))
                    .setSkullOwner(player.getName())
                    .build();
        }

    }

    @AllArgsConstructor
    public static class SummerButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> list = new ArrayList<String>();
            list.add(CC.translate("&ffrom Razorni Services &d&l❤"));
            return new ItemBuilder3(Material.INK_SACK, 14)
                    .setDisplayName(CC.translate("&dHappy New Year #2023"))
                    .setLore(CC.translate(list))
                    .build();
        }

    }

    @AllArgsConstructor
    public static class GlassButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder3(Material.STAINED_GLASS_PANE, 10)
                    .setDisplayName(" ")
                    .glowing(true)
                    .build();
        }
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
