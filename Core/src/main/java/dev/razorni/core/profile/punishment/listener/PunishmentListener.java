package dev.razorni.core.profile.punishment.listener;

import dev.razorni.core.profile.punishment.procedure.PunishmentProcedure;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedureStage;
import dev.razorni.core.profile.punishment.procedure.PunishmentProcedureType;
import dev.razorni.core.util.CC;
import dev.razorni.core.util.callback.TypeCallback;
import dev.razorni.core.util.menu.menus.ConfirmMenu;
import org.bukkit.event.Listener;

import dev.razorni.core.profile.Profile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class PunishmentListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		if (!event.getPlayer().hasPermission("gravity.command.ban")) {
			return;
		}

		PunishmentProcedure procedure = PunishmentProcedure.getByPlayer(event.getPlayer());

		if (procedure != null && procedure.getStage() == PunishmentProcedureStage.REQUIRE_TEXT) {
			event.setCancelled(true);

			if (event.getMessage().equalsIgnoreCase("cancel")) {
				PunishmentProcedure.getProcedures().remove(procedure);
				event.getPlayer().sendMessage(CC.RED + "You have cancelled the punishment procedure.");
				return;
			}

			if (procedure.getType() == PunishmentProcedureType.RESOLVE) {
				new ConfirmMenu(CC.YELLOW + "Resolve this punishment?", new TypeCallback<Boolean>() {
					@Override
					public void callback(Boolean data) {
						if (data) {
							procedure.getPunishment().setResolvedBy(event.getPlayer().getUniqueId());
							procedure.getPunishment().setResolvedAt(System.currentTimeMillis());
							procedure.getPunishment().setResolvedReason(event.getMessage());
							procedure.getPunishment().setResolved(true);
							procedure.finish();

							event.getPlayer().sendMessage(CC.GREEN + "The punishment has been resolved.");



							Profile p = Profile.getByUuid(event.getPlayer().getUniqueId());
							p.getStaffInfo().setPunishmentResolved(p.getStaffInfo().getPunishmentResolved() + 1);
							p.save();
						} else {
							event.getPlayer().sendMessage(CC.RED + "You did not confirm to pardon the punishment.");
						}
					}
				}, true) {
					@Override
					public void onClose(Player player) {
						if (player.getOpenInventory().getTitle().equalsIgnoreCase(getTitle(player))) {
							event.getPlayer().sendMessage(CC.RED + "You did not confirm to pardon the punishment.");
						}
					}
				}.openMenu(event.getPlayer());
			}
		}
	}

}
