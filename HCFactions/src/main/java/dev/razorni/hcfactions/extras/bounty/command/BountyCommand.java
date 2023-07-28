package dev.razorni.hcfactions.extras.bounty.command;

import dev.razorni.hcfactions.utils.commandapi.command.Command;
import org.bukkit.command.CommandSender;

public class BountyCommand {

    @Command(names = "bounty", permission = "")
    public static void bounty(CommandSender sender) {
/*        if (Core.getInstance().getServerType() == ServerType.KITMAP) {
            Player player = BountyListener.currentBountyPlayer == null ? null : Bukkit.getPlayer(BountyListener.currentBountyPlayer);
           if (player == null) {
               sender.sendMessage(CC.RED + "There is no currently active bounties.");
               return;
           }
            for (String s : HCF.getPlugin().getConfig().getStringList("BOUNTY.SPOTTED")) {
                sender.sendMessage(CC.translate(s.replaceAll("%z%", String.valueOf(player.getLocation().getBlockZ())).replaceAll("%y%", String.valueOf(player.getLocation().getBlockY())).replaceAll("%x%", String.valueOf(player.getLocation().getBlockX())).replaceAll("%player%", player.getName())));
            }
        } else {
            sender.sendMessage(CC.RED + "You may use this command only on Kits.");
        }
    }

 */
    }

}
