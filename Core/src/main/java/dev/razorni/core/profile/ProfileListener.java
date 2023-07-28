package dev.razorni.core.profile;

import dev.razorni.core.Core;
import dev.razorni.core.util.Locale;
import dev.razorni.core.database.redis.packets.ServerChangePacket;
import dev.razorni.core.database.redis.packets.staff.StaffJoinPacket;
import dev.razorni.core.database.redis.packets.staff.StaffQuitPacket;
import dev.razorni.core.extras.global.menu.ProfileMainMenu;
import dev.razorni.core.profile.punishment.Punishment;
import dev.razorni.core.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class ProfileListener implements Listener {

	private Core core = Core.getInstance();
	private Map<UUID, Long> currentSession = new HashMap<>();

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		Player player = core.getServer().getPlayer(event.getUniqueId());

		if (!Core.getInstance().isLoaded()) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage(CC.RED + "The server is starting...");
			return;
		}
		// Need to check if player is still logged in when receiving another login attempt
		// This happens when a player using a custom client can access the server list while in-game (and reconnecting)
		if (player != null && player.isOnline()) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage(CC.RED + "You tried to login too quickly after disconnecting.\nTry again in a few seconds.");
			core.getServer().getScheduler().runTask(core, () -> player.kickPlayer(CC.RED + "Duplicate login kick"));
			return;
		}

		Profile profile = null;

		try {
			profile = new Profile(event.getName(), event.getUniqueId());

			if (!profile.isLoaded()) {
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
				event.setKickMessage(Locale.FAILED_TO_LOAD_PROFILE.format());
				return;
			}

			profile.setUsername(event.getName());

			if (profile.getFirstSeen() == null) {
				profile.setFirstSeen(System.currentTimeMillis());
			}

			profile.setLastSeen(System.currentTimeMillis());

			if (profile.getCurrentAddress() == null) {
				profile.setCurrentAddress(hash(event.getAddress().getHostAddress()));
			}

			if (!profile.getIpAddresses().contains(hash(event.getAddress().getHostAddress()))) {
				profile.getIpAddresses().add(hash(event.getAddress().getHostAddress()));
			}

			List<Profile> alts = Profile.getByIpAddress(hash(event.getAddress().getHostAddress()));

			for (Profile alt : alts) {
				if (alt.getCurrentAddress().equals(profile.getCurrentAddress())) {
					if (alt.getUuid() == profile.getUuid())
						continue;

					profile.getKnownAlts().add(alt.getUuid());
				}
			}

			profile.save();
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load profile for " + event.getName());
			e.printStackTrace();
		}

		if (profile == null || !profile.isLoaded()) {
			event.setKickMessage(Locale.FAILED_TO_LOAD_PROFILE.format());
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			return;
		}

		Profile.getProfiles().put(profile.getUuid(), profile);

//		core.getUuidCache().update(event.getName(), event.getUniqueId());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		int requests = profile.getFriendRequests().size();
		int reminders = Core.getInstance().getReminderHandler().getReminders().size();
		int remindersRead = profile.getReadReminders().size();
	}

	public static String hash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
			BigInteger number = new BigInteger(1, hash);
			StringBuilder hexString = new StringBuilder(number.toString(16));

			while (hexString.length() < 32) {
				hexString.insert(0, '0');
			}

			return hexString.toString();
		} catch (Exception var5) {
			var5.printStackTrace();
			return "null";
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		Profile profile = Profile.getProfiles().get(player.getUniqueId());
		String server = Core.getInstance().getConfig().getString("SERVER_NAME");

		profile.setupPermissionsAttachment(core, event.getPlayer());

		new ServerChangePacket(player.getUniqueId().toString(), server, false).send();

		profile.save();

		player.setDisplayName(profile.getActiveGrant().getRank().getPrefix() + player.getName() + profile.getActiveGrant().getRank().getSuffix());

		if (player.hasPermission("gravity.staff")) {
			new StaffJoinPacket(player.getDisplayName(), server).send();
		}
	}

	public long getCurrent(UUID check) {
		if (currentSession.containsKey(check)) {
			return (System.currentTimeMillis() - currentSession.get(check));
		}

		return (0L);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Profile profile = Profile.getProfiles().remove(event.getPlayer().getUniqueId());
		String server = Core.getInstance().getConfig().getString("SERVER_NAME");


		profile.setLastSeen(System.currentTimeMillis());

		Player player = event.getPlayer();

		new ServerChangePacket(player.getUniqueId().toString(), server, false).send();

		if (player.hasPermission("gravity.staff")) {
			new StaffQuitPacket(player.getDisplayName(), server).send();
		}

		if (profile.isLoaded()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						profile.save();
					} catch (Exception e) {
						Bukkit.getLogger().log(Level.SEVERE, "Failed to save profile for " + event.getPlayer().getName());
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(Core.getInstance());
		}
	}

	private void handleBan(AsyncPlayerPreLoginEvent event, Punishment punishment) {
		event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		event.setKickMessage(punishment.getKickMessage());
	}
}
