package dev.razorni.core.profile.punishment.menu;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.PunishmentType;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PunishmentsMenu extends Menu {

	private Profile profile;

	@Override
	public int size(Player player) {
		return size(getButtons(player)) + 9;
	}

	@Override
	public String getTitle(Player player) {
		return CC.translate("&6Punishments of " + profile.getUsername());
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(11, new SelectPunishmentTypeButton(profile, PunishmentType.BLACKLIST));
		buttons.put(12, new SelectPunishmentTypeButton(profile, PunishmentType.BAN));
		buttons.put(13, new SelectPunishmentTypeButton(profile, PunishmentType.MUTE));
		buttons.put(14, new SelectPunishmentTypeButton(profile, PunishmentType.WARN));
		buttons.put(15, new SelectPunishmentTypeButton(profile, PunishmentType.KICK));

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@AllArgsConstructor
	private static class SelectPunishmentTypeButton extends Button {

		private Profile profile;
		private PunishmentType punishmentType;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(punishmentType.getTypeData().getMaterial())
					.name(punishmentType.getTypeData().getColor() + CC.BOLD + punishmentType.getTypeData().getReadable())
					.lore(CC.GRAY + profile.getPunishmentCountByType(punishmentType) + " " + (punishmentType.getTypeData().getReadable().toLowerCase()) + " on record")
					.durability(punishmentType.getTypeData().getData())
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			player.closeInventory();
			new PunishmentsListMenu(profile, punishmentType, false).openMenu(player);
		}
	}

}
