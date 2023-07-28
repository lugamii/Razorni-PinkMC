package dev.razorni.core.profile.punishment.menu.punish;

import dev.razorni.core.profile.punishment.object.PunishReason;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.PunishmentHandler;
import dev.razorni.core.profile.punishment.PunishmentType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 10:53 PM
 * Core / rip.orbit.gravity.profile.punishment.menu.punish
 */

@AllArgsConstructor
public class AllPunishmentsMenu extends Menu {

	private final Profile target;
	private final PunishmentType type;

	@Override
	public String getTitle(Player player) {
		return StringUtils.capitalize(type.toString().toLowerCase()) + " " + target.getUsername();
	}

	@Override
	public int size(Player player) {
		return 54;
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (PunishReason reason : new PunishmentHandler().getReasons()) {
			if (reason.getTypes().contains(type)) {
				buttons.put(i, new PunishButton(reason, target, type));
				++i;
			}
		}

		buttons.put(53, new BackButton(new PunishMenu(target)));

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@AllArgsConstructor
	public static class PunishButton extends Button {

		private final PunishReason reason;
		private final Profile target;
		private final PunishmentType type;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.BOOK_AND_QUILL)
					.name(CC.GOLD + reason.getTitle())
					.lore(CC.translate("&fClick to view a selection of all the &6" + reason.getTitle() + " Punishment Offenses"))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			new OffensesMenu(target, reason, type).openMenu(player);
			playSuccess(player);
		}
	}

}
