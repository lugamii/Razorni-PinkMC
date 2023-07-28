package dev.razorni.core.profile.punishment.menu;

import dev.razorni.core.profile.punishment.menu.punish.PunishMenu;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedure;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedureStage;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedureType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.TimeUtil;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.button.BackButton;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PunishmentsListMenu extends PaginatedMenu {

	private Profile profile;
	private PunishmentType punishmentType;
	private boolean punishMenu;

	@Override
	public int size(Player player) {
		return size(getButtons(player));
	}

	@Override
	public String getPrePaginatedTitle(Player player) {
		return "&6" + punishmentType.getTypeData().getReadable();
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (Punishment punishment : profile.getPunishments()) {
			if (punishment.getType() == punishmentType) {
				buttons.put(buttons.size(), new PunishmentInfoButton(punishment));
			}
		}

		return buttons;
	}

	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		if (!punishMenu) {
			buttons.put(3, new BackButton(new PunishmentsMenu(profile)));
		} else {
			buttons.put(3, new BackButton(new PunishMenu(profile)));
		}
		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.SKULL_ITEM).name(CC.translate("&c" + profile.getUsername() + "")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
			}
		});



		return buttons;
	}

	@AllArgsConstructor
	private class PunishmentInfoButton extends Button {

		private Punishment punishment;

		@Override
		public ItemStack getButtonItem(Player player) {
			String addedBy = "Console";

			if (punishment.getAddedBy() != null) {
				try {
					Profile addedByProfile = Profile.getByUuid(punishment.getAddedBy());
					addedBy = addedByProfile.getUsername();
				} catch (Exception e) {
					addedBy = "Could not fetch...";
				}
			}

			List<String> lore = new ArrayList<>();

			lore.add(CC.MENU_BAR);
			lore.add("&fAdded by: &6" + addedBy);
			lore.add("&fAdded for: &6" + punishment.getAddedReason());
			lore.add("&fAdded at: &6" + punishment.getAddedAtDate() + " EST");

			if (punishment.isActive() && !punishment.isPermanent() && punishment.getDuration() != -1) {
				lore.add("&fRemaining: &6" + punishment.getTimeRemaining());
			}

			if (punishment.isResolved()) {
				String removedBy = "Console";

				if (punishment.getResolvedBy() != null) {
					try {
						Profile removedByProfile = Profile.getByUuid(punishment.getResolvedBy());
						removedBy = removedByProfile.getUsername();
					} catch (Exception e) {
						removedBy = "Could not fetch...";
					}
				}

				lore.add(CC.MENU_BAR);
				lore.add("&fResolved at: &6" + TimeUtil.dateToString(new Date(punishment.getResolvedAt())));
				lore.add("&fResolved by: &6" + removedBy);
				lore.add("&fResolved for: &6" + punishment.getResolvedReason());
			}

			lore.add(CC.MENU_BAR);

			if (punishment.isActive() && !punishment.isResolved() && punishment.getType().isCanBePardoned()) {
				lore.add("&fRight click to resolve this punishment");
				lore.add(CC.MENU_BAR);
			}

			return new ItemBuilder(Material.PAPER)
					.name("&6" + TimeUtil.dateToString(new Date(punishment.getAddedAt())))
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			if (!player.hasPermission("gravity.command.unban")) {
				player.sendMessage(CC.translate("&cNo permission."));
				return;
			}
			if (clickType == ClickType.RIGHT && !punishment.isResolved() && punishment.getType().isCanBePardoned()) {
				PunishmentProcedure procedure = new PunishmentProcedure(player, profile, PunishmentProcedureType.RESOLVE, PunishmentProcedureStage.REQUIRE_TEXT);
				procedure.setPunishment(punishment);

				player.sendMessage(CC.GREEN + "Type a reason for resolving this punishment in chat make sure to provide proof if you're Senior-Mod below and it's optional if you're above Senior-Mod...");
				player.closeInventory();
			}
		}
	}

}
