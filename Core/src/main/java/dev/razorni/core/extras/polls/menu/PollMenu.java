package dev.razorni.core.extras.polls.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import org.bukkit.inventory.ItemStack;
import dev.razorni.core.extras.polls.Poll;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 5:58 PM
 * HCTeams / rip.orbit.hcteams.polls.menu
 */
public class PollMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "All Polls";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Poll poll : Poll.getPolls()) {
			buttons.put(i, new PollButton(poll));
		}
		++i;

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@Override
	public int size(Player player) {
		return 9;
	}

	@AllArgsConstructor
	public static class PollButton extends Button {

		private final Poll poll;

		public String getName(Player player) {
			return CC.translate("&6" + poll.getTitle() + " Poll");
		}

		public List<String> getDescription(Player player) {
			return CC.translate(Arrays.asList(
					"&7┃ &fQuestion&7: &6" + poll.getQuestion(),
					"",
					"&7┃ &fYes'&7: &a" + poll.getYes(),
					"&7┃ &fNo's&7: &c" + poll.getNo(),
					"",
					"&7&oLeft Click to vote &aYes",
					"&7&oRight Click to vote &cNo"
			));
		}
		public Material getMaterial(Player player) {
			return Material.BOOK_AND_QUILL;
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			if (poll.getClaimedPlayers().contains(player.getUniqueId())) {
				player.sendMessage(CC.translate("&cYou have already voted!"));
				return;
			}
			if (clickType == ClickType.LEFT) {
				poll.setYes(poll.getYes() + 1);
				player.sendMessage(CC.translate("&aVoted Yes!"));
			} else {
				poll.setNo(poll.getNo() + 1);
				player.sendMessage(CC.translate("&cVoted No!"));
			}
			poll.getClaimedPlayers().add(player.getUniqueId());
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(getMaterial(player)).name(getName(player)).lore(getDescription(player)).build();
		}
	}

}
