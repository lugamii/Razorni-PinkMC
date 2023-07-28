package dev.razorni.hcfactions.events.koth.command.args;

import cc.invictusgames.ilib.utils.TimeUtils;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.events.koth.Koth;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KothListArg {

    @Command(names = "event list", permission = "azurite.koth.list")
    public static void kothlist(final Player player) {
        player.sendMessage(CC.LINE);
        player.sendMessage(CC.translate("&b&lKoth List"));
        for (Koth koth : HCF.getPlugin().getKothManager().getKoths().values()) {
            player.sendMessage(CC.translate(ChatColor.WHITE + " &7* &fName: " + ChatColor.AQUA + koth.getName() + ChatColor.WHITE + " &7┃ &fLocation: " + ChatColor.AQUA + koth.getCaptureZone().getX1() + " " + koth.getCaptureZone().getY1() + " " + koth.getCaptureZone().getZ1() + ChatColor.WHITE + " &7┃ &fCapture Time: " + ChatColor.AQUA + TimeUtils.formatDetailed(koth.getMinutes())));
        }
        player.sendMessage(CC.LINE);
    }
}