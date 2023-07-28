package eu.vortexdev.invictusspigot.command;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import eu.vortexdev.invictusspigot.util.BukkitUtil;
import eu.vortexdev.invictusspigot.util.JavaUtil;
import net.minecraft.server.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Arrays;

public class SetNoTrackCommand extends Command {

    public SetNoTrackCommand() {
        super("setnotrack");
        setDescription("Adjusts a world's no track distance");
        setPermission("invictusspigot.notrack");
        setAliases(Arrays.asList("notrack", "setnotrackdistance"));
    }

    @Override
    public boolean execute(CommandSender s, String arg1, String[] args) {
        if (testPermission(s)) {
            if (args.length == 2) {
                String worldName = args[0];
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "World " + worldName + " does not exist!");
                    return true;
                }

                Integer trackRange = JavaUtil.tryParseInteger(args[1]);
                if(trackRange == null) {
                    s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Invalid number!");
                    return true;
                }
                trackRange = Math.max(trackRange, 0);

                ((CraftWorld) world).getHandle().getTracker().setNoTrackDistance(trackRange);
                s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "Track distance of world " + ChatColor.GRAY + worldName + ChatColor.WHITE + " was set to " + ChatColor.GRAY + trackRange);
            } else {
                helpMessage(s);
            }
        }
        return false;
    }

    public void helpMessage(CommandSender sender) {
        sender.sendMessage(BukkitUtil.LINE);
        sender.sendMessage(InvictusConfig.mainColor.toString() + ChatColor.ITALIC + "Set No Track Distance Help:");
        sender.sendMessage("/notrack [world] [amount]");
        sender.sendMessage(BukkitUtil.LINE);
    }

}
