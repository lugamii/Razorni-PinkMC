package dev.razorni.core.profile.grant.menu;

import dev.razorni.core.profile.grant.procedure.GrantProcedure;
import dev.razorni.core.profile.grant.procedure.GrantProcedureStage;
import dev.razorni.core.profile.grant.procedure.GrantProcedureType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import dev.razorni.core.profile.grant.Grant;
import dev.razorni.core.profile.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class GrantsMenu extends PaginatedMenu {

	private Profile profile;

	@Override
	public int size(Player player) {
		return size(getButtons(player));
	}

	@Override
	public String getPrePaginatedTitle(Player player) {
		return "&6Grants: " + profile.getUsername();
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (Grant grant : profile.getGrants()) {
			buttons.put(buttons.size(), new GrantInfoButton(profile, grant));
		}

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@AllArgsConstructor
	private static class GrantInfoButton extends Button {

		private Profile profile;
		private Grant grant;

		@Override
		public ItemStack getButtonItem(Player player) {
			String addedBy = "Console";

			if (grant.getAddedBy() != null) {
				addedBy = "Could not fetch...";

				Profile addedByProfile = Profile.getByUuid(grant.getAddedBy());

				if (addedByProfile != null && addedByProfile.isLoaded()) {
					addedBy = addedByProfile.getUsername();
				}
			}

			List<String> lore = new ArrayList<>();

			lore.add(CC.MENU_BAR);
			lore.add("&fAdded by: &6" + addedBy);
			lore.add("&fAdded for: &6" + grant.getAddedReason());
			lore.add("&fAdded at: &6" + grant.getAddedAtDate() + " EST");

			if (!grant.isRemoved()) {
				if (!grant.isPermanent()) {
					if (grant.hasExpired()) {
						lore.add("&fExpires in: &cExpired");
					} else {
						lore.add("&fExpires in: &6" + grant.getTimeRemaining());
					}
				} else {
					lore.add("&fExpires in: &6Never");
				}
			} else {
				String removedBy = "Console";

				if (grant.getRemovedBy() != null) {
					removedBy = "Could not fetch...";

					Profile removedByProfile = Profile.getByUuid(grant.getRemovedBy());

					if (removedByProfile != null && removedByProfile.isLoaded()) {
						removedBy = removedByProfile.getUsername();
					}
				}

				lore.add(CC.MENU_BAR);
				lore.add("&fRemoved by: &6" + removedBy);
				lore.add("&fRemoved for: &6" + grant.getRemovedReason());
				lore.add("&fRemoved at: &6" + grant.getRemovedAtDate());
			}

			lore.add(CC.MENU_BAR);

			if (!grant.isRemoved()) {
				lore.add("&eRight click to remove this grant");
				lore.add(CC.MENU_BAR);
			}

			return new ItemBuilder(Material.PAPER)
					.name(grant.getRank().getColor() + grant.getRank().getDisplayName())
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			if (clickType == ClickType.RIGHT && !grant.isRemoved()) {
				GrantProcedure procedure = new GrantProcedure(player, this.profile, GrantProcedureType.REMOVE, GrantProcedureStage.REQUIRE_TEXT);
				procedure.setGrant(grant);

				player.sendMessage(CC.GREEN + "Type a reason for removing this grant in chat...");
				player.closeInventory();
			}
		}
	}

}
