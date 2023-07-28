package eu.vortexdev.invictusspigot.command;

import eu.vortexdev.invictusspigot.config.InvictusConfig;
import eu.vortexdev.invictusspigot.util.BukkitUtil;
import eu.vortexdev.invictusspigot.util.JavaUtil;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class PotionCommand extends Command {

    public PotionCommand() {
        super("pot");
        setDescription("Set potion values");
        setAliases(Arrays.asList("setpot", "potion", "pot", "pots"));
        setPermission("invictusspigot.potion");
    }

    @Override
    public boolean execute(CommandSender s, String currentAlias, String[] args) {
        if (testPermission(s)) {
            if(args.length == 0) {
                helpMessage(s);
                return true;
            } else if(args.length >= 2) {
                Float value = JavaUtil.tryParseFloat(args[1]);
                if(value == null) {
                    s.sendMessage(InvictusConfig.prefix + ChatColor.RED + "Value " + args[1] + " is not valid.");
                    return true;
                }
                switch (args[0].toLowerCase()) {
                    case "gravity": {
                        InvictusConfig.set("potions.gravity", value);
                        InvictusConfig.potionFallSpeed = value;
                        break;
                    }
                    case "offset": {
                        InvictusConfig.set("potions.verticalOffset", value);
                        InvictusConfig.potionThrowOffset = value;
                        break;
                    }
                    case "speed": {
                        InvictusConfig.set("potions.speed", value);
                        InvictusConfig.potionThrowMultiplier = value;
                        break;
                    }
                    default: {
                        helpMessage(s);
                        return true;
                    }
                }
                s.sendMessage(InvictusConfig.prefix + ChatColor.WHITE + "You've set potion " + ChatColor.GRAY + WordUtils.capitalizeFully(args[0]) + ChatColor.WHITE + " to " + InvictusConfig.mainColor + value);
                InvictusConfig.save();
            } else {
                helpMessage(s);
            }
        }
        return true;
    }

    public void helpMessage(CommandSender s) {
        s.sendMessage(BukkitUtil.LINE);
        s.sendMessage(InvictusConfig.mainColor.toString() + ChatColor.ITALIC + "Potion Help:");
        s.sendMessage("/pot speed [value]");
        s.sendMessage("/pot distance [value]");
        s.sendMessage("/pot offset [value]");
        s.sendMessage(BukkitUtil.LINE);
    }

}
