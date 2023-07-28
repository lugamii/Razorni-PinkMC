package dev.razorni.core.profile.staffmode.menu;

import dev.razorni.core.Core;
import dev.razorni.core.profile.staffmode.menu.button.StaffButton;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StaffListMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.PINK + "Online Staff";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 10;
        for (final Player staffMember : Core.getInstance().getServer().getOnlinePlayers()) {
            if (staffMember.hasPermission("core.staff") || staffMember.isOp()) {
                buttons.put(index++, new StaffButton(staffMember.getUniqueId()));
            }
        }

        buttons.put(0, new GlassButton());
        buttons.put(1, new GlassButton());
        buttons.put(2, new GlassButton());
        buttons.put(3, new GlassButton());
        buttons.put(4, new GlassButton());
        buttons.put(5, new GlassButton());
        buttons.put(6, new GlassButton());
        buttons.put(6 + 1, new GlassButton());
        buttons.put(8, new GlassButton());
        buttons.put(9, new GlassButton());
        buttons.put(16 + 1, new GlassButton());
        buttons.put(18, new GlassButton());
        buttons.put(26, new GlassButton());
        buttons.put(26 + 1, new GlassButton());
        buttons.put(28, new GlassButton());
        buttons.put(29, new GlassButton());
        buttons.put(30, new GlassButton());
        buttons.put(31, new GlassButton());
        buttons.put(32, new GlassButton());
        buttons.put(33, new GlassButton());
        buttons.put(34, new GlassButton());
        buttons.put(35, new GlassButton());


        setAutoUpdate(true);


        return buttons;
    }

    @Override
    public int size(Player player) {
        return 26 + 10;
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
            return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2);
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(getMaterial(player)).name(getName(player)).setGlowing(true).build();
        }
    }


}
