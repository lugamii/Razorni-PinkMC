package dev.razorni.hcfactions.extras.ecoshop.menu;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.ecoshop.buttons.BuyItemButton;
import dev.razorni.hcfactions.extras.ecoshop.buttons.BuySpawnerButton;
import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.ItemBuilder;
import dev.razorni.hcfactions.utils.menuapi.menu.Button;
import dev.razorni.hcfactions.utils.menuapi.menu.Menu;
import dev.razorni.hcfactions.utils.menuapi.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PotionMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&dSpawners Shop &7┃ &f$" + HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance());
    }

    @Override
    public int size(Player player) {
        return 9 * 3;
    }

    @Override
    public boolean isAutoUpdate() {
        return false;
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
        buttons.put(18, new GlassButton());
        buttons.put(19, new GlassButton());
        buttons.put(25, new GlassButton());
        buttons.put(26, new GlassButton());

        buttons.put(4, new HeadButton());
        buttons.put(22, new BackButton(new CategorySelectorMenu()));

        buttons.put(11,
                new BuySpawnerButton("Spider Spawner", 20000, 1,
                        new ItemBuilder(Material.MONSTER_EGG).durability((short) 52).build(),
                        "spawners give " + player.getName() + " Spider"));
        buttons.put(12,
                new BuySpawnerButton("Skeleton Spawner", 20000, 1,
                        new ItemBuilder(Material.MONSTER_EGG).durability((short) 51).build(),
                        "spawners give " + player.getName() + " Skeleton"));
        buttons.put(13,
                new BuyItemButton("8x Cow Eggs", 1000, 8,
                        new ItemBuilder(Material.MONSTER_EGG).durability((short) 92).build(),
                        new ItemStack(Material.MONSTER_EGG, 8, (short) 92)));
        buttons.put(14,
                new BuySpawnerButton("Zombie Spawner", 20000, 1,
                        new ItemBuilder(Material.MONSTER_EGG).durability((short) 54).build(),
                        "spawners give " + player.getName() + " Zombie"));
        buttons.put(15,
                new BuySpawnerButton("Cow Spawner", 20000, 1,
                        new ItemBuilder(Material.MONSTER_EGG).durability((short) 92).build(),
                        "spawners give " + player.getName() + " Cow"));
        setAutoUpdate(false);
        return buttons;
    }

    @AllArgsConstructor
    public static class ECButton extends Button {


        public String getName(Player player) {
            return CC.GOLD + "Enderchest";
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.ENDER_CHEST, 1);
        }

        public String getLore(Player player) {
            return CC.translate("&6&l┃ &fStore your items in enderchest for more space.");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.openInventory(player.getEnderChest());
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).name(getName(player)).build();
        }
    }

    @AllArgsConstructor
    public static class HeadButton extends Button {


        public String getName(Player player) {
            return CC.PINK + player.getName() + "'s Balance";
        }

        public ItemStack getMaterial(Player player) {
            return new ItemStack(Material.SKULL_ITEM, 1);
        }

        public String getLore(Player player) {
            return CC.translate("&fYour Balance: &d$" + HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId()).getBalance());
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).lore(getLore(player)).durability(3).skull(player.getName()).name(getName(player)).build();
        }
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
