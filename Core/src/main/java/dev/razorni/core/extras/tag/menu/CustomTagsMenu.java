package dev.razorni.core.extras.tag.menu;

import dev.razorni.core.Core;
import dev.razorni.core.extras.tag.Tag;
import dev.razorni.core.extras.tag.menu.buttons.ResetTagButton;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.button.BackButton;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTagsMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.GOLD + "Customs Prefixes";
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 36;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(3, new ResetTagButton());
        buttons.put(5, new BackButton(new CategorySelectorMenu()));

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Core.getInstance().getTagHandler().getCustomtags().forEach(tag -> {
            buttons.put(buttons.size(), new TagSelectionButton(tag));

        });

        return buttons;
    }

    @AllArgsConstructor
    private static class TagSelectionButton extends Button {

        private final Tag tag;

        @Override
        public ItemStack getButtonItem(Player player) {

            final List<String> lore = new ArrayList<>();

            lore.add(" ");
            lore.add(CC.translate("&b» &eExample: " + tag.getPrefix() + " " + player.getDisplayName()));
            lore.add(CC.GRAY + "Shift + Click to preview in chat!");
            lore.add(" ");
            if (player.hasPermission("tag." + tag.getName())) {
                lore.add(CC.translate("&b» &a&l❤ &r&aYou have access to this tag."));
            } else if (!player.hasPermission("tag." + tag.getName())) {
                lore.add(CC.translate("&b» &4✘ &r&cPlease purchase using /store."));
            }

            return new ItemBuilder(Material.BEACON)
                    .name(tag.getPrefix())
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType == ClickType.SHIFT_RIGHT) {
                player.sendMessage(CC.translate("&eExample: " + tag.getPrefix() + " " + CC.GRAY + player.getDisplayName() + ": " + CC.WHITE + "Hi"));
                return;
            }
            if (!player.hasPermission("tag." + tag.getName())) {
                player.sendMessage(CC.RED + "You don`t own this tag, obtain it using /store.");
                return;
            }
            if (Profile.getByUuid(player.getUniqueId()).getTag() == tag) {
                player.sendMessage(CC.RED + "You already have this tag active.");
                return;
            }

            player.closeInventory();

            player.sendMessage(CC.translate("&fYour &6tag&f has been updated to " + tag.getPrefix()));

            Profile.getByUuid(player.getUniqueId()).setTag(tag);
            Profile.getByUuid(player.getUniqueId()).save();

        }
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
