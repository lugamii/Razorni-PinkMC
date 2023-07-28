package dev.razorni.hcfactions.extras.killstreaks.command;

import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.extras.killstreaks.menu.KillstreakMenu;
import org.bukkit.entity.Player;

public class KillstreakCommand {

    @Command(names = {"ks", "killstreak"}, permission = "azurite.ks")
    public static void setKillstreak(Player player) {
        new KillstreakMenu().openMenu(player);
    }

}
