package dev.razorni.core.extras.tips;

import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import dev.razorni.core.profile.Profile;
import dev.razorni.core.extras.tips.file.TipFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/07/2021 / 12:53 AM
 * Core / rip.orbit.core.profile.tips
 */
public class TipManager {

	@Getter private Core core;
	@Getter private TipFile file;
	@Getter @Setter
	private int tipOn;

	@Getter private List<List<String>> tips;

	public TipManager(Core core) {
		this.core = core;

		tipOn = 0;

		tips = new ArrayList<>();

		file = new TipFile();

		try {
			for (String sect : getFile().getConfigurationSection("tips").getKeys(false)) {
				getTips().add(getFile().getStringList("tips." + sect));
			}


			new BukkitRunnable() {
				@Override
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) {
						Profile profile = Profile.getByUuid(p.getUniqueId());
						if (profile.getOptions().isTipsEnabled()) {
							getTips().get(tipOn).forEach(s -> p.sendMessage(CC.translate(s)));
							++tipOn;
							if (tipOn > getTips().size() - 1) {
								setTipOn(0);
							}
						}
					}
				}
			}.runTaskTimerAsynchronously(core, 20 * 10, 20 * 60 * 2);
		} catch (Exception ignored) {

		}
	}

}
