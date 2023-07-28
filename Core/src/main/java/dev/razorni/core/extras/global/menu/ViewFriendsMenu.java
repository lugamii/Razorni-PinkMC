package dev.razorni.core.extras.global.menu;

import dev.razorni.core.database.redis.packets.friend.FriendRemovePacket;
import dev.razorni.core.extras.friends.Friend;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import dev.razorni.core.profile.Profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/07/2021 / 7:20 PM
 * Core / rip.orbit.gravity.profile.global.menu
 */

@AllArgsConstructor
public class ViewFriendsMenu extends PaginatedMenu {

	private final Profile target;

	@Override
	public boolean showPages(Player player) {
		return false;
	}

	@Override
	public String getPrePaginatedTitle(Player player) {
		return CC.translate("&6" + target.getUsername() + "'s Friends");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Friend friend : target.getFriends()) {
			Profile friendProfile = Profile.getByUuid(friend.getFriend());
			buttons.put(i, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM)
							.durability(3)
							.name(CC.translate("&6" + friendProfile.getDisplayName()));

					if (target.getUuid().equals(player.getUniqueId())) {
						if (friendProfile.isOnline()) {
							builder.lore(CC.translate(Arrays.asList(
									"",
									"&6┃ &fStatus: &aOnline",
									"&6┃ &fServer: &6" + friendProfile.getServerOn(),
									"",
									"&f&oRight Click to remove them as a friend.")));
						} else {
							builder.lore(CC.translate(Arrays.asList(
									"",
									"&6┃ &fStatus: &cOffline",
									"&6┃ &fLast Server On: &6" + friendProfile.getServerOn(),
									"",
									"&f&oRight Click to remove them as a friend.")));
						}
					} else {
						if (friendProfile.isOnline()) {
							builder.lore(CC.translate(Arrays.asList(
									"",
									"&6┃ &fStatus: &aOnline",
									"&6┃ &fServer: &6" + friendProfile.getServerOn(),
									""
									)));
						} else {
							builder.lore(CC.translate(Arrays.asList(
									"",
									"&6┃ &fStatus: &cOffline",
									"&6┃ &fLast Server On: &6" + friendProfile.getServerOn(),
									""
									)));
						}
					}

					ItemStack stack = builder.build();
					SkullMeta meta = (SkullMeta) stack.getItemMeta();
					meta.setOwner(friendProfile.getUsername());
					stack.setItemMeta(meta);

					return stack;
				}

				@Override
				public void clicked(Player player, ClickType clickType) {
					if (clickType == ClickType.RIGHT) {
						if (Profile.getByUuid(player.getUniqueId()) != target) {
							return;
						}
//						Core.getInstance().getPacketBase().sendPacket(new PacketFriendRemove(player.getUniqueId(), friendProfile.getUuid()));
//						new FriendRemovePacket(new JsonBuilder()
//								.addProperty("senderUUID", player.getUniqueId().toString())
//								.addProperty("targetUUID", friendProfile.getUuid().toString())
//						).send();

						new FriendRemovePacket(Profile.getByUuid(player.getUniqueId()), target).send();
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
				return new ItemBuilder(Material.FEATHER).name(CC.translate("&6Go Back")).lore(CC.translate("&7Click to go back to the main menu.")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ProfileMainMenu(target).openMenu(player);
			}
		});
		if (target == Profile.getByUuid(player.getUniqueId())) {
			buttons.put(4, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					return new ItemBuilder(Material.ANVIL).name(CC.translate("&6Friends Settings")).lore(CC.translate("&7Click to view your friends settings.")).build();
				}

				@Override
				public void clicked(Player player, ClickType clickType) {
					new FriendSettingsMenu().openMenu(player);
				}
			});
		}
		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.CARPET).name(CC.translate("&6View Friend Requests")).lore(CC.translate("&7Click to view all your friend requests.")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ViewFriendRequestsMenu(target).openMenu(player);
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
