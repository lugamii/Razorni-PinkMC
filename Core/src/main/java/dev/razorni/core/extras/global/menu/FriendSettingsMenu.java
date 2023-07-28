package dev.razorni.core.extras.global.menu;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.profile.Profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/08/2021 / 2:21 PM
 * Core / rip.orbit.gravity.profile.global.menu
 */
public class FriendSettingsMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&6Friend Settings");
	}

	@Override
	public int size(Player player) {
		return 18;
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		Profile profile = Profile.getByUuid(var1.getUniqueId());

		buttons.put(3, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				Profile profile = Profile.getByUuid(player.getUniqueId());

				ItemBuilder builder = new ItemBuilder(Material.STONE_BUTTON);

				builder.name(CC.translate("&6Toggle Click To Friend"));

				if (profile.getOptions().isFriendRightClickEnabled()) {
					builder.lore(CC.translate(Arrays.asList(
							" ",
							"&fClick to toggle on/off when you right",
							"&fclick a player it sends them a friend",
							"&frequest.",
							" ",
							"&fStatus: &aEnabled",
							" "
					)));
				} else {
					builder.lore(CC.translate(Arrays.asList(
							" ",
							"&fClick to toggle on/off when you right",
							"&fclick a player it sends them a friend",
							"&frequest.",
							" ",
							"&fStatus: &cDisabled",
							" "
					)));
				}

				return builder.build();
			}


			@Override
			public void clicked(Player player, ClickType clickType) {
				profile.getOptions().setFriendRightClickEnabled(!profile.getOptions().isFriendRightClickEnabled());
				profile.save();
			}

		});

		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {

				ItemBuilder builder = new ItemBuilder(Material.NETHER_STAR);

				builder.name(CC.translate("&6Toggle Friend Requests"));

				if (profile.getOptions().isFriendRequestsEnabled()) {
					builder.lore(CC.translate(Arrays.asList(
							" ",
							"&fClick to toggle on/off if you can receive",
							"&ffriend requests.",
							" ",
							"&fStatus: &aEnabled",
							" "
					)));
				} else {
					builder.lore(CC.translate(Arrays.asList(
							" ",
							"&fClick to toggle on/off if you can receive",
							"&ffriend requests.",
							" ",
							"&fStatus: &cDisabled",
							" "
					)));
				}

				return builder.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				if (profile.getOptions().isFriendRequestsEnabled()) {
					profile.getOptions().setFriendRequestsEnabled(false);
				} else {
					profile.getOptions().setFriendRequestsEnabled(true);
				}
				profile.save();
			}
		});

		buttons.put(17, new BackButton(new ViewFriendsMenu(profile)));

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
