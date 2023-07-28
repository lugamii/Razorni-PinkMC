package dev.razorni.core.profile.grant.listener;

import dev.razorni.core.extras.rank.Rank;
import dev.razorni.core.util.BukkitUtils;
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
import dev.razorni.core.profile.grant.GrantBuild;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/09/2021 / 1:07 AM
 * lCore / me.lbuddyboy.core.profile.grant.menu
 */

@AllArgsConstructor
public class GrantMenu extends PaginatedMenu {

	private final Profile target;

	@Override
	public String getPrePaginatedTitle(Player player) {
		return CC.translate("&6Grant: " + target.getUsername());
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		List<Rank> ranks = new ArrayList<>();

		for (Map.Entry<UUID, Rank> entry : Rank.getRanks().entrySet()) {
			ranks.add(entry.getValue());
		}

		ranks.sort(Comparator.comparingInt(Rank::getWeight));

		int i = 0;
		for (Rank rank : ranks) {

			buttons.put(i, new RankButton(rank, target));
			++i;
		}
		return buttons;
	}

	@AllArgsConstructor
	public static class RankButton extends Button {

		private final Rank rank;
		private final Profile target;

		@Override
		public void clicked(Player player, ClickType clickType) {
			if (!player.hasPermission("gravity.command.grant." + rank.getDisplayName())) {
				return;
			}

			player.closeInventory();

			GrantBuild grantBuild = new GrantBuild(player.getUniqueId(), target.getUuid(), rank, null, null);
			GrantListener.grantBuildMap.put(player, grantBuild);
			GrantListener.time.add(player);
			player.sendMessage(CC.translate("&aType the time you would like to grant this player the " + rank.getDisplayName() + " &arank for."));
			player.sendMessage(CC.translate("&aType 'cancel' to cancel the process"));
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.WOOL).name(CC.translate(rank.getColor() + rank.getDisplayName())).durability(BukkitUtils.toDyeColor(rank.getColor())).lore(CC.translate(Arrays.asList(
					"",
					"&fClick to select the " + rank.getColor() + rank.getDisplayName() + "&f Rank",
					""
			))).build();
		}
	}

}
