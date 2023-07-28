package dev.razorni.core.profile.option.staff.menu;

import dev.razorni.core.Core;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.profile.punishment.PunishmentType;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedure;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedureStage;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedureType;
import dev.razorni.core.extras.report.Report;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.TimeUtil;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.profile.Profile;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/07/2021 / 2:14 PM
 * Core / rip.orbit.gravity.profile.option.staff.menu
 */
@AllArgsConstructor
public class StaffHistoryMenu extends Menu {

	@Getter private Profile profile;

	@Override
	public String getTitle(Player player) {
		return "Staff History";
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();


		buttons.put(10, new StaffHistoryButton(profile, PunishmentType.BLACKLIST));
		buttons.put(11, new StaffHistoryButton(profile, PunishmentType.BAN));
		buttons.put(12, new StaffHistoryButton(profile, PunishmentType.MUTE));
		buttons.put(13, new StaffHistoryButton(profile, PunishmentType.WARN));
		buttons.put(14, new StaffHistoryButton(profile, PunishmentType.KICK));
		buttons.put(15, new StaffHistoryReportsButton(profile));
		buttons.put(16, new UnPunishmentsButton(profile));

		return buttons;
	}

	@Override
	public int size(Player player) {
		return size(getButtons(player)) + 9;
	}

	@AllArgsConstructor
	private static class UnPunishmentsButton extends Button {
		private Profile profile;

		@Override
		public ItemStack getButtonItem(Player player) {
			int getTotal = profile.getStaffInfo().getPunishmentResolved();
			return new ItemBuilder(Material.ANVIL)
					.name(CC.PINK + "Resolved Punishments: " + CC.YELLOW + getTotal)
					.lore(CC.translate("&7» Click to view their &dResolved Punishments"))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile p = Profile.getByUuid(player.getUniqueId());
			new UnPunishmentsMenu(profile, p.getStaffInfo().getPunishments()).openMenu(player);

		}

	}

	@AllArgsConstructor
	private static class StaffHistoryButton extends Button {
		private Profile profile;
		private PunishmentType punishmentType;

		@Override
		public ItemStack getButtonItem(Player player) {
			int getTotal = 0;
			if (punishmentType == PunishmentType.BLACKLIST) {
				getTotal = profile.getStaffInfo().getBlacklists();
			} else if (punishmentType == PunishmentType.BAN) {
				getTotal = profile.getStaffInfo().getBans();
			} else if (punishmentType == PunishmentType.MUTE) {
				getTotal = profile.getStaffInfo().getMutes();
			} else if (punishmentType == PunishmentType.KICK) {
				getTotal = profile.getStaffInfo().getKicks();
			} else if (punishmentType == PunishmentType.WARN) {
				getTotal = profile.getStaffInfo().getWarns();
			}
			return new ItemBuilder(punishmentType.getTypeData().getMaterial())
					.name(punishmentType.getTypeData().getColor() + punishmentType.name() + CC.WHITE + ": " + getTotal)
					.lore(CC.translate("&7» Click to view their " + punishmentType.getTypeData().getColor() + punishmentType.name()))
					.durability(punishmentType.getTypeData().getData())
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			CompletableFuture.runAsync(() -> {
				new StaffHistoryMenu.PunishmentsMenu(profile, profile.getStaffInfo().getPunishments(), punishmentType).openMenu(player);
			});

		}

	}

	@AllArgsConstructor
	private static class StaffHistoryReportsButton extends Button {
		private Profile profile;

		@Override
		public ItemStack getButtonItem(Player player) {
			int getTotal = profile.getStaffInfo().getReportsResolved();
			return new ItemBuilder(Material.PAPER)
					.name(CC.GOLD + "Resolved Reports" + CC.WHITE + ": " + getTotal)
					.lore(CC.translate("&7» Click to view their &6Resolved Reports"))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			new ReportsResolvedMenu(profile).openMenu(player);
		}

	}

	@AllArgsConstructor
	private static class ReportsResolvedMenu extends PaginatedMenu {
		private Profile profile;


		@Override
		public String getPrePaginatedTitle(Player player) {
			return "Resolved Reports";
		}

		@Override
		public boolean isAutoUpdate() {
			return true;
		}

		@Override
		public Map<Integer, Button> getAllPagesButtons(Player player) {
			Map<Integer, Button> buttons = new HashMap<>();

			int i = 0;

			for (Report report : Core.getInstance().getReportHandler().getReports()) {
				if (!report.isResolved())
					continue;
				if (!report.getResolvedBy().equals(profile.getUsername()))
					continue;
				buttons.put(i, new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						ItemStack stack = new ItemBuilder(Material.PAPER)
								.lore(Arrays.asList(
										CC.CHAT_BAR,
										CC.translate("&6&lReport Info"),
										CC.CHAT_BAR,
										CC.translate("&7┃ &fCreator: " + report.getSender()),
										CC.translate("&7┃ &fMade At: " + report.getCreatedDate()),
										CC.translate("&7┃ &fReason: " + report.getMessage()),
										CC.translate("&7┃ &fServer: " + report.getServer()),
										CC.CHAT_BAR,
										CC.translate("&6&lResolved Info"),
										CC.translate("&7┃ &fResolved By: " + report.getResolvedBy()),
										CC.translate("&7┃ &fResolved At: " + report.getRemovedAtDate()),
										CC.CHAT_BAR
								))
								.name(CC.translate("&6" + report.getName()))
								.build();

						if (report.isReport()) {
							stack = new ItemBuilder(Material.PAPER)
									.lore(Arrays.asList(
											CC.CHAT_BAR,
											CC.translate("&6&lReport Info"),
											CC.CHAT_BAR,
											CC.translate("&7┃ &fCreator: " + report.getSender()),
											CC.translate("&7┃ &fMade At: " + report.getCreatedDate()),
											CC.translate("&7┃ &fReason: " + report.getMessage()),
											CC.translate("&7┃ &fServer: " + report.getServer()),
											CC.translate("&7┃ &fReported: " + report.getReported()),
											CC.CHAT_BAR,
											CC.translate("&6&lResolved Info"),
											CC.translate("&7┃ &fResolved By: " + report.getResolvedBy()),
											CC.translate("&7┃ &fResolved At: " + report.getRemovedAtDate()),
											CC.CHAT_BAR
									))
									.name(CC.translate("&6" + report.getName()))
									.build();
						}

						return stack;
					}

					@Override
					public void clicked(Player player, ClickType clickType) {
						if (!player.isOp()) {
							player.sendMessage(CC.translate("&cYou can only delete reports if you're operator."));
							return;
						}
						Core.getInstance().getReportHandler().removeReport(report);

					}
				});
				++i;
			}

			return buttons;
		}

		@Override
		public int size(Player player) {
			return size(getButtons(player)) + 9;
		}

	}

	@AllArgsConstructor
	private static class PunishmentsMenu extends PaginatedMenu {
		private Profile profile;
		private List<Punishment> punishments;
		private PunishmentType type;


		@Override
		public String getPrePaginatedTitle(Player player) {
			return type.name() + "'s";
		}

		@Override
		public boolean isAutoUpdate() {
			return true;
		}

		@Override
		public Map<Integer, Button> getAllPagesButtons(Player player) {
			Map<Integer, Button> buttons = new HashMap<>();

			int i = 0;
			for (Punishment punishment : this.punishments) {
				if (punishment.getType() != type)
					continue;
				if (punishment.isResolved())
					continue;
				buttons.put(i, new Button() {
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

						if (!punishment.isActive() && !punishment.isResolved() && punishment.getType().isCanBePardoned()) {
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
						if (clickType == ClickType.RIGHT && !punishment.isResolved() && punishment.getType().isCanBePardoned()) {
							PunishmentProcedure procedure = new PunishmentProcedure(player, profile, PunishmentProcedureType.RESOLVE, PunishmentProcedureStage.REQUIRE_TEXT);
							procedure.setPunishment(punishment);

							player.sendMessage(CC.GREEN + "Type a reason for resolving this punishment in chat make sure to provide proof if you're Senior-Mod below and it's optional if you're above Senior-Mod...");
							player.closeInventory();
						}
					}
				});
				++i;


			}

			return buttons;
		}

		@Override
		public int size(Player player) {
			return size(getButtons(player));
		}
	}

	@AllArgsConstructor
	private static class UnPunishmentsMenu extends PaginatedMenu {
		private Profile profile;
		private List<Punishment> punishments;


		@Override
		public String getPrePaginatedTitle(Player player) {
			return "All Resolved";
		}

		@Override
		public boolean isAutoUpdate() {
			return true;
		}

		@Override
		public Map<Integer, Button> getAllPagesButtons(Player player) {
			Map<Integer, Button> buttons = new HashMap<>();

			int i = 0;
			for (Punishment punishment : this.punishments) {
				if (!punishment.isResolved())
					continue;
				buttons.put(i, new Button() {
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

						if (!punishment.isActive() && !punishment.isResolved() && punishment.getType().isCanBePardoned()) {
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
						if (clickType == ClickType.RIGHT && !punishment.isResolved() && punishment.getType().isCanBePardoned()) {
							PunishmentProcedure procedure = new PunishmentProcedure(player, profile, PunishmentProcedureType.RESOLVE, PunishmentProcedureStage.REQUIRE_TEXT);
							procedure.setPunishment(punishment);

							player.sendMessage(CC.GREEN + "Type a reason for resolving this punishment in chat make sure to provide proof if you're Senior-Mod below and it's optional if you're above Senior-Mod...");
							player.closeInventory();
						}
					}
				});
				++i;


			}

			return buttons;
		}

		@Override
		public int size(Player player) {
			return size(getButtons(player));
		}
	}
}
