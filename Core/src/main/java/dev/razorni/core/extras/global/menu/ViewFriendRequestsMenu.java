package dev.razorni.core.extras.global.menu;

import dev.razorni.core.extras.friends.request.FriendRequest;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import dev.razorni.core.util.uuid.UniqueIDCache;
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
public class ViewFriendRequestsMenu extends PaginatedMenu {

	private final Profile target;

	@Override
	public boolean showPages(Player player) {
		return false;
	}

	@Override
	public String getPrePaginatedTitle(Player player) {
		return CC.translate("&6Friend Requests");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		Profile profile = Profile.getByUuid(player.getUniqueId());
		for (FriendRequest friendRequest : target.getFriendRequests()) {
			buttons.put(i, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					ItemBuilder builder = new ItemBuilder(Material.PAPER)
							.name(CC.translate("&6" + UniqueIDCache.name(friendRequest.getSender()) + "'s Friend Request"));

					if (profile == target) {
						builder.lore(CC.translate(Arrays.asList(
								"",
								"&fRight Click to deny this friend request",
								"&fLeft Click to accept this friend request"
						)));
					}

					return builder.build();
				}

				@Override
				public void clicked(Player player, ClickType clickType) {
					if (profile == target) {
						if (clickType == ClickType.RIGHT) {
							target.getFriendRequests().remove(friendRequest);
							target.save();
							Profile.getByUuid(friendRequest.getTarget()).getFriendRequests().remove(friendRequest);
							Profile.getByUuid(friendRequest.getTarget()).save();
						} else {
							player.chat("/friend accept " + UniqueIDCache.name(friendRequest.getSender()));
						}
					}
				}
			});
			++i;
		}

		return buttons;
	}

	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(3, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.FEATHER).name(CC.translate("&6Go Back")).lore(CC.translate("&7Click to go back to the previous menu.")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ViewFriendsMenu(target).openMenu(player);
			}
		});

		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.CHAINMAIL_HELMET).name(CC.translate("&6All " + target.getUsername() + "'s Friends")).lore(CC.translate("&7Click to view all of " + target.getUsername() + " friends.")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ViewFriendsMenu(target).openMenu(player);
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
