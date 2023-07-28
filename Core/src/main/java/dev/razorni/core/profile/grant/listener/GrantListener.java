package dev.razorni.core.profile.grant.listener;

import dev.razorni.core.database.redis.packets.GrantRemovePacket;
import dev.razorni.core.profile.grant.procedure.GrantProcedure;
import dev.razorni.core.profile.grant.procedure.GrantProcedureStage;
import dev.razorni.core.profile.grant.procedure.GrantProcedureType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.discord.DiscordLogger;
import dev.razorni.core.util.uuid.UniqueIDCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.profile.grant.Grant;
import dev.razorni.core.profile.grant.GrantBuild;
import dev.razorni.core.profile.grant.command.SetRankCommand;
import dev.razorni.core.profile.grant.event.GrantAppliedEvent;
import dev.razorni.core.profile.grant.event.GrantExpireEvent;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class GrantListener implements Listener {

	@EventHandler
	public void onGrantAppliedEvent(GrantAppliedEvent event) {
		Player player = event.getPlayer();
		Grant grant = event.getGrant();

		player.sendMessage(CC.GREEN + ("A {rank} grant has been applied to you for {time-remaining}.")
				.replace("{rank}", grant.getRank().getColor() + grant.getRank().getDisplayName() + CC.GREEN)
				.replace("{time-remaining}", grant.getTimeRemaining()));

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setupBukkitPlayer(profile.getPlayer());

	}

	@EventHandler
	public void onGrantExpireEvent(GrantExpireEvent event) {
		Player player = event.getPlayer();
		Grant grant = event.getGrant();

		player.sendMessage(CC.RED + ("Your `{rank}` grant has expired.")
				.replace("{rank}", grant.getRank().getDisplayName())
				.replace("", ""));

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setupBukkitPlayer(profile.getPlayer());

	}

	public static List<Player> time = new ArrayList<>();
	public static List<Player> reason = new ArrayList<>();
	public static Map<Player, GrantBuild> grantBuildMap = new HashMap<>();


	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {

		Player p = event.getPlayer();

		if (!event.getPlayer().hasPermission("gravity.command.grant")) {
			return;
		}

		GrantProcedure procedure = GrantProcedure.getByPlayer(event.getPlayer());

		if (procedure != null && procedure.getStage() == GrantProcedureStage.REQUIRE_TEXT) {
			event.setCancelled(true);

			if (procedure.getType() == GrantProcedureType.REMOVE) {
				if (!event.getMessage().equals("cancel")) {
					procedure.getGrant().setRemovedBy(event.getPlayer().getUniqueId());
					procedure.getGrant().setRemovedAt(System.currentTimeMillis());
					procedure.getGrant().setRemovedReason(event.getMessage());
					procedure.getGrant().setRemoved(true);
					procedure.finish();
					event.getPlayer().sendMessage(CC.GREEN + "The grant has been removed.");

					Profile profile = procedure.getRecipient();

					profile.getGrants().removeIf(other -> Objects.equals(other, procedure.getGrant()));
					profile.getGrants().add(procedure.getGrant());
					profile.save();

					try {
						new DiscordLogger().logGrantRemove(profile.getUsername(), procedure.getGrant());
					} catch (IOException e) {
						e.printStackTrace();
					}

					new GrantRemovePacket(procedure.getRecipient().getUuid(), procedure.getGrant()).send();
				} else {
					GrantProcedure.getProcedures().remove(procedure);
					event.getPlayer().sendMessage(CC.RED + "You have cancelled the grant procedure.");
				}
			}
		}


		if (time.contains(p)) {
			if (event.getMessage().equalsIgnoreCase("cancel")) {
				p.sendMessage(CC.translate("&cProcess cancelled."));
				time.remove(p);
				event.setCancelled(true);
				return;
			}
			GrantBuild grantBuild = grantBuildMap.get(p);
			grantBuild.setTime(event.getMessage());

			grantBuildMap.put(p, grantBuild);

			p.sendMessage(CC.translate("&aNow, type in the reason for granting " + UniqueIDCache.name(grantBuild.getTarget()) + "&a the " + grantBuild.getRank().getDisplayName() + "&a Rank"));
			time.remove(p);
			reason.add(p);
			event.setCancelled(true);
		} else if (reason.contains(p)) {
			if (event.getMessage().equalsIgnoreCase("cancel")) {
				p.sendMessage(CC.translate("&cProcess cancelled."));
				reason.remove(p);
				event.setCancelled(true);
				return;
			}
			GrantBuild grantBuild = grantBuildMap.get(p);
			grantBuild.setReason(event.getMessage());

			grantBuildMap.put(p, grantBuild);

			SetRankCommand.setrank(event.getPlayer(), grantBuild.getTarget(), grantBuild.getRank(), grantBuild.getTime(), grantBuild.getReason());

			reason.remove(p);
			event.setCancelled(true);


		}
	}

}
