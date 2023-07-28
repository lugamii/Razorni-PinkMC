package dev.razorni.core.listener;

import dev.razorni.core.Core;
import dev.razorni.core.profile.staffmode.menu.StaffListMenu;
import dev.razorni.core.server.ServerType;
import dev.razorni.core.util.CC;
import dev.razorni.hcfactions.HCF;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EssentialsListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {

		try {
			if (!(event.getWhoClicked() instanceof Player)) {
				return;
			}

			if (event.getClickedInventory().getTitle().contains("Plugins (")) {
				event.setCancelled(true);
			}
		} catch (NullPointerException ignored) {

		}

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		final String lowercase = event.getMessage();
		if (Core.getInstance().getServerType() == ServerType.HUB) {
			if (lowercase.contains("/mod") || lowercase.contains("/staff")) {
				player.chat("/hubmod");
				event.setCancelled(true);
			}
		}
		if (lowercase.startsWith("/ver") ||
				lowercase.startsWith("/about") ||
				lowercase.startsWith("/author") ||
				lowercase.startsWith("/version") ||
				lowercase.startsWith("/?")) {
			player.sendMessage(" ");
			player.sendMessage(CC.translate("&3&lrSpigot"));
			player.sendMessage(CC.translate("&7This server is running rSpigot (MC: 1.7 - 1.8)"));
			player.sendMessage(CC.translate("&7by Razorni Development Team"));
			player.sendMessage(" ");
			event.setCancelled(true);
		if (lowercase.startsWith("//calc") ||
				lowercase.startsWith("//eval") ||
				lowercase.startsWith("//solve") ||
				lowercase.startsWith("/me") ||
				lowercase.startsWith("/bukkit:me") ||
				lowercase.startsWith("/minecraft:") ||
				lowercase.startsWith("/minecraft:me")) {
			player.sendMessage(CC.RED + "That command was not found.");
			event.setCancelled(true);
			}
	  	}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void foodFix(FoodLevelChangeEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player player = (Player) event.getEntity();
			player.setSaturation(10.0F);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		if (event.getBlock().getType() == Material.ICE || event.getBlock().getType() == Material.PACKED_ICE || event.getBlock().getType() == Material.SNOW_BLOCK) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void staffInteract(PlayerInteractEvent event) {
		if (!(Core.getInstance().getServerType() == ServerType.HCF))
			return;
		if (HCF.getPlugin().getStaffManager().isStaffEnabled(event.getPlayer())) {
			if (event.getPlayer().getItemInHand().getType() == Material.SKULL_ITEM) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					new StaffListMenu().openMenu(event.getPlayer());
				}
			}
		}
	}
}
