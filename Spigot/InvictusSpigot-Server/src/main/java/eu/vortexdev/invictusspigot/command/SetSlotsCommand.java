package eu.vortexdev.invictusspigot.command;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import eu.vortexdev.invictusspigot.util.BukkitUtil;
import eu.vortexdev.invictusspigot.util.JavaUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SetSlotsCommand extends Command {

    public SetSlotsCommand() {
        super("setmaxslots");
        setAliases(Arrays.asList("setslots", "slots"));
        setDescription("Set max slots of server");
        setPermission("invictusspigot.setslots");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (testPermission(sender)) {
            if (args.length == 1) {
                Integer value = JavaUtil.tryParseInteger(args[0]);
                if(value == null) {
                    sender.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Invalid number!");
                    return true;
                }
                sender.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "You've set max slots to " + InvictusConfig.mainColor + Bukkit.setMaxPlayers(value));
            } else {
                helpMessage(sender);
            }
        }
        return true;
    }

    public void helpMessage(CommandSender sender) {
        sender.sendMessage(BukkitUtil.LINE);
        sender.sendMessage(InvictusConfig.mainColor.toString() + ChatColor.ITALIC + "Set Slots Help:");
        sender.sendMessage("/setslots [amount]");
        sender.sendMessage(BukkitUtil.LINE);
    }

}
