package dev.razorni.hcfactions.reclaims.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.reclaims.Reclaim;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ReclaimCommand extends Command {
    public ReclaimCommand(CommandManager manager) {
        super(manager, "reclaim");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("claim");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        Reclaim reclaim = this.getInstance().getReclaimManager().getReclaim(player);
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        if (reclaim == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RECLAIM_COMMAND.NO_RECLAIM"));
            return;
        }
        if (user.isReclaimed()) {
            this.sendMessage(sender, this.getLanguageConfig().getString("RECLAIM_COMMAND.ALREADY_RECLAIMED"));
            return;
        }
        for (String s : reclaim.getCommands()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", player.getName()));
        }
        user.setReclaimed(true);
        user.save();
    }
}
