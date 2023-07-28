package eu.vortexdev.invictusspigot.command;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.PluginManager;

public class ChunkUnloadCommand extends Command {

    public ChunkUnloadCommand() {
        super("chunkunload");
        setPermission("invictusspigot.chunkunload");
        setDescription("Unload all chunks");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (testPermission(sender)) {
            int chunks = 0;
            PluginManager pluginManager = Bukkit.getPluginManager();
            for (World world : Bukkit.getServer().getWorlds())
                for (Chunk chunk : world.getLoadedChunks())
                    if (!world.isChunkInUse(chunk.getX(), chunk.getZ())) {
                        ChunkUnloadEvent event = new ChunkUnloadEvent(chunk);
                        pluginManager.callEvent(event);
                        if (chunk.unload(true))
                            chunks++;
                    }
            sender.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "Unloaded " + chunks + " chunks");
        }
        return false;
    }
}
