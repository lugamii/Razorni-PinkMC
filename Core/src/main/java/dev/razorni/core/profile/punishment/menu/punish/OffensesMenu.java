package dev.razorni.core.profile.punishment.menu.punish;

import dev.razorni.core.profile.punishment.object.Offense;
import dev.razorni.core.profile.punishment.object.PunishReason;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.PunishmentType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 10:25 PM
 * Core / rip.orbit.gravity.profile.punishment.menu.punish
 */

@AllArgsConstructor
public class OffensesMenu extends Menu {

	private final Profile target;
	private final PunishReason reason;
	private final PunishmentType type;

	@Override
	public String getTitle(Player player) {
		return "Offenses";
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Offense offense : reason.getOffenses()) {
			if (offense.getType() == type) {
				buttons.put(i, new OffenseButton(reason, offense, target));
				++i;
			}
		}

		buttons.put(8, new BackButton(new AllPunishmentsMenu(target, type)));

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public int size(Player player) {
		int highest = 0;
		for (int buttonValue : getButtons(player).keySet()) {
			if (buttonValue > highest) {
				highest = buttonValue;
			}
		}
		return (int)(Math.ceil((double)(highest + 1) / 9.0D) * 9.0D);
	}

	@AllArgsConstructor
	public static class OffenseButton extends Button {

		private final PunishReason reason;
		private final Offense offense;
		private final Profile target;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.PAPER)
					.name(CC.translate(CC.GOLD + offense.getStage() + " Offense"))
					.lore(CC.translate(Arrays.asList(
							"&fClick to punish",
							"",
							"&fPunish Time: &6" + offense.getBanTime()
					)))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			new ConfirmMenu(target, reason, offense).openMenu(player);
			playSuccess(player);
		}
	}

}
