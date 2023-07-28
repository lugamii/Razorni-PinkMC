package dev.razorni.core.extras.global.menu;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import dev.razorni.core.profile.Profile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/07/2021 / 11:39 PM
 * Core / rip.orbit.gravity.profile.global.menu
 */

@AllArgsConstructor
public class ProfileMainMenu extends Menu {

	private final Profile target;

	@Override
	public String getTitle(Player player) {
		return "Global Statistics";
	}

	@Override
	public int size(Player player) {
		return 9;
	}

	@Override
	public Map<Integer, Button> getButtons(Player p) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(2, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				ItemBuilder builder = new ItemBuilder(Material.PAINTING);
				if (!target.getUsername().equals(Profile.getByUuid(player.getUniqueId()).getUsername())) {
					builder.lore(CC.translate("&7Click to view " + target.getUsername() + "'s friends."));
					builder.name(CC.translate("&6" + target.getUsername() + "'s Friends"));
				} else {
					builder.lore(CC.translate("&7Click to view your friends."));
					builder.name(CC.translate("&6Your " + "Friends"));
				}
				return builder.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ViewFriendsMenu(target).openMenu(player);
			}
		});

		buttons.put(4, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM);
				if (!target.getUsername().equals(Profile.getByUuid(player.getUniqueId()).getUsername())) {
					builder.lore(CC.translate("&7Click to view " + target.getUsername() + "'s statistics."));
					builder.name(CC.translate("&6" + target.getUsername() + "'s Statistics"));
				} else {
					builder.lore(CC.translate("&7Click to view your statistics."));
					builder.name(CC.translate("&6Your " + "Statistics"));
				}
				builder.durability(3);

				ItemStack stack = builder.build();
				SkullMeta meta = (SkullMeta) stack.getItemMeta();
				meta.setOwner(target.getUsername());
				stack.setItemMeta(meta);

				return stack;
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ViewProfileMenu(target).openMenu(player);
			}
		});

		buttons.put(6, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				ItemBuilder builder = new ItemBuilder(Material.BOOK);
				if (!target.getUsername().equals(Profile.getByUuid(player.getUniqueId()).getUsername())) {
					builder.lore(CC.translate("&7Click to view " + target.getUsername() + "'s notifications."));
					builder.name(CC.translate("&6" + target.getUsername() + "'s Notifications"));
				} else {
					builder.lore(CC.translate("&7Click to view your notifications."));
					builder.name(CC.translate("&6Your " + "Notifications"));
				}
				return builder.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ViewNotificationsMenu(target).openMenu(player);
			}
		});

		return buttons;
	}
}
