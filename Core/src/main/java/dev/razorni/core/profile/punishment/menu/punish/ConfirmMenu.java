package dev.razorni.core.profile.punishment.menu.punish;

import dev.razorni.core.profile.punishment.object.Offense;
import dev.razorni.core.profile.punishment.object.PunishReason;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.ItemBuilder;
import dev.razorni.core.util.menu.Button;
import dev.razorni.core.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.punishment.PunishmentType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/08/2021 / 10:28 PM
 * Core / rip.orbit.gravity.profile.punishment.menu.punish
 */

@AllArgsConstructor
public class ConfirmMenu extends Menu {

	private final Profile target;
	private final PunishReason reason;
	private final Offense offense;

	@Override
	public String getTitle(Player player) {
		return "Confirm Menu";
	}

	@Override
	public int size(Player player) {
		return 9;
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(3, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.WOOL).name(CC.translate("&a&lConfirm")).durability(5).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				String time = offense.getBanTime()
						.replace(" minutes", "m")
						.replace(" minute", "m")
						.replace(" hours", "h")
						.replace(" hour", "h")
						.replace(" days", "d")
						.replace(" day", "d")
						.replace(" week", "w")
						.replace(" weeks", "w");
				if (offense.getType() == PunishmentType.WARN) {
					player.chat("/" + offense.getType().toString().toLowerCase() + " " + target.getUsername() + " "  + reason.getTitle());
				} else {
					player.chat("/" + offense.getType().toString().toLowerCase() + " " + target.getUsername() + " " + time + " " + reason.getTitle());
				}
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 12F, 1F);
				player.closeInventory();
 			}
		});
		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return new ItemBuilder(Material.WOOL).name(CC.translate("&c&lCancel")).durability(14).build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				playSuccess(player);
				new PunishMenu(target).openMenu(player);
				player.closeInventory();
			}
		});

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}
}
