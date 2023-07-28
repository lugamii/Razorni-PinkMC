package dev.razorni.hcfactions.extras.ecoshop.menu;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.ecoshop.buttons.BuyItemButton;
import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.utils.menuapi.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&dItems Shop &7┃ &f$" + HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance());
    }

    @Override
    public int size(Player player) {
        return 9 * 6;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
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
        buttons.put(26 + 1 + 9, new GlassButton());
        buttons.put(35 + 9, new GlassButton());
        buttons.put(36 + 9, new GlassButton());
        buttons.put(36 + 1 + 9, new GlassButton());
        buttons.put(43 + 9, new GlassButton());
        buttons.put(44 + 9, new GlassButton());

        buttons.put(4, new PotionMenu.HeadButton());
        buttons.put(49, new BackButton(new CategorySelectorMenu()));

        buttons.put(11,
                new BuyItemButton("Carrot", 600, 16, new ItemStack(Material.CARROT_ITEM)));
        buttons.put(12,
                new BuyItemButton("Glistering Melon", 1200, 16, new ItemStack(Material.SPECKLED_MELON)));
        buttons.put(13,
                new BuyItemButton("Sugar Cane", 600, 16, new ItemStack(Material.SUGAR_CANE)));
        buttons.put(14,
                new BuyItemButton("Nether Stalk", 600, 16, new ItemStack(Material.NETHER_STALK)));
        buttons.put(15,
                new BuyItemButton("Potato", 600, 16, new ItemStack(Material.POTATO_ITEM)));
        buttons.put(21,
                new BuyItemButton("Melon Seeds", 600, 16, new ItemStack(Material.MELON_SEEDS)));
        buttons.put(23,
                new BuyItemButton("Fermented Spider Eye", 1200, 16, new ItemStack(Material.FERMENTED_SPIDER_EYE)));
        buttons.put(29,
                new BuyItemButton("Coal", 1000, 16, new ItemStack(Material.COAL)));
        buttons.put(30,
                new BuyItemButton("Feather", 600, 16, new ItemStack(Material.FEATHER)));
        buttons.put(31,
                new BuyItemButton("Blaze Rod", 600, 16, new ItemStack(Material.BLAZE_ROD)));
        buttons.put(32,
                new BuyItemButton("End Portal Frame", 5000, 3, new ItemStack(Material.ENDER_PORTAL_FRAME)));
        buttons.put(33,
                new BuyItemButton("Ghast Tear", 1000, 16, new ItemStack(Material.GHAST_TEAR)));
        buttons.put(39,
                new BuyItemButton("Slime Ball", 900, 16, new ItemStack(Material.SLIME_BALL)));
        buttons.put(40,
                new BuyItemButton("Beacon", 30000, 1, new ItemStack(Material.BEACON)));
        buttons.put(41,
                new BuyItemButton("Spider Eye", 900, 16, new ItemStack(Material.SPIDER_EYE)));

        return buttons;
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

}
