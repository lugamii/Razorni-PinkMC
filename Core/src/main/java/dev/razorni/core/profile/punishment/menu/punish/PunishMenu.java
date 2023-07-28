package dev.razorni.core.profile.punishment.menu.punish;

import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.PunishmentType;
import dev.razorni.core.profile.punishment.menu.PunishmentsListMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 10:21 PM
 * Core / rip.orbit.gravity.profile.punishment.menu.punish
 */

@AllArgsConstructor
public class PunishMenu extends Menu {

	private final Profile target;

	@Override
	public String getTitle(Player player) {
		return "Punish Menu";
	}

	@Override
	public int size(Player player) {
		return 54;
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(11, new SelectPunishmentTypeButton(this.target, PunishmentType.BLACKLIST));
		buttons.put(12, new SelectPunishmentTypeButton(this.target, PunishmentType.BAN));
		buttons.put(13, new SelectPunishmentTypeButton(this.target, PunishmentType.MUTE));
		buttons.put(14, new SelectPunishmentTypeButton(this.target, PunishmentType.KICK));
		buttons.put(15, new SelectPunishmentTypeButton(this.target, PunishmentType.WARN));

		buttons.put(30, new PunishmentTypeButton(this.target, PunishmentType.BLACKLIST, Material.BEDROCK));
		buttons.put(31, new PunishmentTypeButton(this.target, PunishmentType.BAN, Material.GOLD_BLOCK));
		buttons.put(32, new PunishmentTypeButton(this.target, PunishmentType.MUTE, Material.IRON_BLOCK));
		buttons.put(40, new PunishmentTypeButton(this.target, PunishmentType.WARN, Material.EMERALD_BLOCK));

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@AllArgsConstructor
	public static class SelectPunishmentTypeButton extends Button {

		private final Profile profile;
		private final PunishmentType punishmentType;

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
			new PunishmentsListMenu(profile, punishmentType, true).openMenu(player);
		}
	}

	@AllArgsConstructor
	public static class PunishmentTypeButton extends Button {

		private final Profile profile;
		private final PunishmentType type;
		private final Material material;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(material)
					.name(type.getTypeData().getColor() + CC.BOLD + StringUtils.capitalize(type.toString().toLowerCase()))
					.lore(CC.translate("&fClick to " + type.toString().toLowerCase() + " " + profile.getUsername()))
					.durability(type.getTypeData().getData())
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			playSuccess(player);
			player.closeInventory();
			new AllPunishmentsMenu(profile, type).openMenu(player);
		}
	}

}
