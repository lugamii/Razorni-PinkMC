package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TpRandomCommand extends Command {
    public TpRandomCommand(CommandManager manager) {
        super(manager, "tprandom");
        this.setPermissible("azurite.tprandom");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        if (players.size() == 1) {
            this.sendMessage(sender, this.getLanguageConfig().getString("TELEPORT_COMMAND.TPRANDOM_COMMAND.INSUFFICIENT_PLAYERS"));
            return;
        }
        Player online;
        Player selected;
        for (online = (Player) sender, selected = players.get(ThreadLocalRandom.current().nextInt(players.size())); online == selected; selected = players.get(ThreadLocalRandom.current().nextInt(players.size()))) {
        }
        Logger.info(online.getName() + " has randomly teleported to " + selected.getName() + ". (/tprandom)");
        online.teleport(selected);
        this.sendMessage(sender, this.getLanguageConfig().getString("TELEPORT_COMMAND.TPRANDOM_COMMAND.TELEPORTED").replaceAll("%player%", selected.getName()));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("randomtp", "randtp");
    }
}
