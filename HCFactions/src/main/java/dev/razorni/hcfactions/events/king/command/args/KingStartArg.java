package dev.razorni.hcfactions.events.king.command.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class KingStartArg extends Argument {
    public KingStartArg(CommandManager manager) {
        super(manager, Collections.singletonList("start"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        String text = String.join(" ", (CharSequence[]) Arrays.copyOfRange(args, 1, args.length));
        if (player == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (this.getInstance().getKingManager().isActive()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KING_COMMAND.KING_START.ALREADY_ACTIVE"));
            return;
        }
        this.getInstance().getKingManager().startKing(player, text);
        this.getInstance().getNametagManager().update();
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KING_COMMAND.KING_START.USAGE");
    }
}
