package org.spigotmc;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import net.jafama.FastMath;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.concurrent.TimeUnit;

public class TicksPerSecondCommand extends Command {

	private final long init = System.currentTimeMillis();

	public TicksPerSecondCommand(String name) {
		super(name);
		this.description = "Gets the current ticks per second for the server";
		this.usageMessage = "/tps";
		this.setPermission("bukkit.command.tps");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		double[] tps = org.bukkit.Bukkit.spigot().getTPS();
		String[] tpsAvg = new String[tps.length];

		for (int i = 0; i < tps.length; i++) {
			tpsAvg[i] = format(tps[i]);
		}

		int chunks = 0, entities = 0;

		for (World world : MinecraftServer.getServer().server.getWorlds()) {
			entities += ((CraftWorld)world).getHandle().entityList.size();
			chunks += world.getLoadedChunks().length;
		}

		for(String str : InvictusConfig.tpsCommand) {
			sender.sendMessage(str
					.replace("%used_ram%", Long.toString(Runtime.getRuntime().totalMemory() / 1048576L - Runtime.getRuntime().freeMemory() / 1048576L))
					.replace("%free_ram%", Long.toString(Runtime.getRuntime().freeMemory() / 1048576L))
					.replace("%total_ram%", Long.toString(Runtime.getRuntime().totalMemory() / 1048576L))
					.replace("%current_tps%", format(MinecraftServer.getServer().tps.getAverage()))
					.replace("%uptime%", formatTime(System.currentTimeMillis() - init))
					.replace("%list_tps%", StringUtils.join(tpsAvg, ", "))
					.replace("%total_entity%", Integer.toString(entities))
					.replace("%total_chunk_loaded%", Integer.toString(chunks))
					.replace("%total_player_online%", Integer.toString(Bukkit.getOnlinePlayers().size()))
					.replace("%max_slot%", Integer.toString(Bukkit.getMaxPlayers()))
					);
		}
		return true;
	}

    public static String formatTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        StringBuilder sb = new StringBuilder(64);
        if (days > 0L)
            sb.append(days).append("d");
        if (hours > 0L)
            sb.append(hours).append("h");
        if (minutes > 0L)
            sb.append(minutes).append("m");
        if (seconds > 0L)
            sb.append(seconds).append("s");
        return sb.toString();
    }

	private static String format(double tps) // PaperSpigot - made static
	{
		return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString()
				+ ((tps > 20.0) ? "*" : "") + FastMath.min(FastMath.round(tps * 100.0) / 100.0, 20.0);
	}
}
