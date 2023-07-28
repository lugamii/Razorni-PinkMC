package dev.razorni.core.commands.menu;

import dev.razorni.core.Core;
import dev.razorni.core.database.redis.packets.global.ReportUpdatePacket;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.pagination.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.extras.report.Report;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/07/2021 / 10:03 PM
 * Core / rip.orbit.gravity.commands.menu
 */
public class ResolvedReportsMenu extends PaginatedMenu {
	@Override
	public String getPrePaginatedTitle(Player player) {
		return CC.translate("&6Resolved Reports");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Report report : Core.getInstance().getReportHandler().getReports()) {
			if (!report.isResolved())
				continue;
			buttons.put(i, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					if (report.isReport()) {
						return new ItemBuilder(Material.PAPER).name(CC.translate("&6" + report.getName()))
								.lore(CC.translate(Arrays.asList(
										CC.CHAT_BAR,
										CC.translate("&6&lReport Info"),
										CC.CHAT_BAR,
										CC.translate("&7┃ &fCreator: &6" + report.getSender()),
										CC.translate("&7┃ &fMade At: &6" + report.getCreatedDate()),
										CC.translate("&7┃ &fReason: &6" + report.getMessage()),
										CC.translate("&7┃ &fReported: &6" + report.getReported()),
										CC.translate("&7┃ &fServer: &6" + report.getServer()),
										CC.CHAT_BAR,
										CC.translate("&6&lResolved Info"),
										CC.translate("&7┃ &fResolved By: &6" + report.getResolvedBy()),
										CC.translate("&7┃ &fResolved At: &6" + report.getRemovedAtDate()),
										CC.CHAT_BAR,
										CC.translate("&fClick to delete this report."),
										CC.CHAT_BAR
								)))
								.build();
					}
					return new ItemBuilder(Material.PAPER).name(CC.translate("&6" + report.getName()))
							.lore(CC.translate(Arrays.asList(
									CC.CHAT_BAR,
									CC.translate("&6&lReport Info"),
									CC.CHAT_BAR,
									CC.translate("&7┃ &fCreator: &6" + report.getSender()),
									CC.translate("&7┃ &fMade At: &6" + report.getCreatedDate()),
									CC.translate("&7┃ &fReason: &6" + report.getMessage()),
									CC.translate("&7┃ &fServer: &6" + report.getServer()),
									CC.CHAT_BAR,
									CC.translate("&6&lResolved Info"),
									CC.translate("&7┃ &fResolved By: &6" + report.getResolvedBy()),
									CC.translate("&7┃ &fResolved At: &6" + report.getRemovedAtDate()),
									CC.CHAT_BAR,
									CC.translate("&fClick to delete this report."),
									CC.CHAT_BAR
							)))
							.build();
				}

				@Override
				public void clicked(Player player, ClickType clickType) {

					Core.getInstance().getReportHandler().removeReport(report);

					new ReportUpdatePacket(report, null, false, true).send();

//					JsonBuilder builder = new JsonBuilder();
//					builder.addProperty("name", report.getName());
//					builder.addProperty("message", report.getMessage());
//					builder.addProperty("sender", report.getSender());
//					builder.addProperty("server", report.getServer());
//					builder.addProperty("createdAt", report.getCreatedAt());
//					builder.addProperty("reported", report.getReported());
//					builder.addProperty("report", report.isReport());
//					builder.addProperty("resolved", report.isResolved());
//					builder.addProperty("resolvedAt", report.getResolvedAt());
//					builder.addProperty("resolvedBy", report.getResolvedBy());
//					builder.addProperty("remove", true);
//					builder.addProperty("sendMessage", false);
//
//					new ReportUpdatePacket(builder).send();
				}
			});
			++i;
		}

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(4, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.BOOK).name(CC.translate("&cGo Back")).lore(CC.translate("&7Click to view all the reports.")).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				new ReportsMenu().openMenu(player);
			}
		});

		return buttons;
	}

	@Override
	public int size(Player player) {
		return 54;
	}
}
