package dev.razorni.core.extras.global.menu;

import dev.razorni.core.Core;
import dev.razorni.core.extras.reminder.Reminder;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
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
 * 23/07/2021 / 6:39 PM
 * Core / rip.orbit.gravity.profile.global.menu
 */

@AllArgsConstructor
public class ViewSeenNotificationsMenu extends PaginatedMenu {

	private final Profile target;

	@Override
	public boolean showPages(Player player) {
		return false;
	}

	@Override
	public String getPrePaginatedTitle(Player player) {
		return CC.translate("&6Notifications");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		Profile profile = Profile.getByUuid(player.getUniqueId());
		for (Reminder reminder : Core.getInstance().getReminderHandler().getReminders()) {
			if (profile.getReadReminders().contains(reminder)) {
				buttons.put(i, new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						return new ItemBuilder(Material.PAPER)
								.name(CC.translate("&6" + reminder.getTitle()))
								.lore(CC.translate(Arrays.asList(
										"",
										"&6┃ &fMessage: &6" + reminder.getMessage(),
										"&6┃ &fSent At: &6" + reminder.getSentDate(),
										""
								)))
								.build();
					}

					@Override
					public void clicked(Player player, ClickType clickType) {

					}
				});
				++i;
			}
		}

		return buttons;
	}

	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(4, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.FEATHER).name(CC.translate("&6Go Back")).lore(CC.translate("&7Click to go back to your preview menu.")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ViewNotificationsMenu(target).openMenu(player);
			}
		});

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@Override
	public int size(Player player) {
		return 45;
	}
}
