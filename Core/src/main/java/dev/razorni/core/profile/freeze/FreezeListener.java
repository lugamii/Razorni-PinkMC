package dev.razorni.core.profile.freeze;

import dev.razorni.core.database.redis.packets.staff.FreezeQuitPacket;
import dev.razorni.core.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.razorni.core.profile.Profile;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 03/07/2021 / 2:04 AM
 * Core / rip.orbit.gravity.profile.freeze
 */
public class FreezeListener implements Listener {

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.getOptions().isFrozen()) {
//			new FreezeQuitPacket(new JsonBuilder().addProperty("player", event.getPlayer().getDisplayName()));
			new FreezeQuitPacket(profile.getColoredUsername()).send();
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.getOptions().isFrozen()) {
			if (event.getMessage().startsWith("/r")) {
				return;
			}
			if (event.getMessage().startsWith("/msg")) {
				return;
			}
			if (event.getMessage().startsWith("/message")) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.getOptions().isFrozen()) {
			boolean isValid = (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ());
			if (event.getTo().getY() != event.getFrom().getY() && !isValid)
				return;
			if (event.getTo().getYaw() != event.getFrom().getYaw() && !isValid)
				return;
			if (event.getTo().getPitch() != event.getFrom().getPitch() && !isValid)
				return;
			event.setTo(event.getFrom());
			profile.getPlayer().sendMessage(CC.CHAT_BAR);
			profile.getPlayer().sendMessage(CC.translate("&fJoin &xortec.net/discord&f you have &63 minutes&f to join."));
			profile.getPlayer().sendMessage(CC.translate("&fIf you fail to join within that time you will be &6banned&f."));
			profile.getPlayer().sendMessage(CC.CHAT_BAR);
		}
	}

	@EventHandler
	public void onDmg(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
			if (profile.getOptions().isFrozen()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDmgByDmg(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Profile profile = Profile.getByUuid(event.getDamager().getUniqueId());
			if (profile.getOptions().isFrozen()) {
				event.setCancelled(true);
			}
		}
	}

}
