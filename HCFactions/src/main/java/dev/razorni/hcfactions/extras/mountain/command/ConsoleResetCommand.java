package dev.razorni.hcfactions.extras.mountain.command;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import org.bukkit.entity.Player;

public class ConsoleResetCommand {

    @Command(names = "glowconsole", permission = "azurite.glowcons")
    public static void glowcons(final Player player) {
        HCF.getPlugin().getGlowstoneMountainManager().reset();
    }

}
