package eu.vortexdev.invictusspigot.command;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KillEntitiesCommand extends Command {

    public KillEntitiesCommand() {
        super("killentities");
        setDescription("Kill all entities");
        setPermission("invictusspigot.killentities");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (testPermission(sender)) {
            int entities = 0;
            for (World world : MinecraftServer.getServer().worlds) {
                for (Entity entity : world.entityList) {
                    if (entity instanceof EntityPlayer || entity.dead)
                        continue;
                    entity.dead = true;
                    entities++;
                }
            }
            sender.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "Cleared " + entities + " entities!");
        }
        return false;
    }
}
