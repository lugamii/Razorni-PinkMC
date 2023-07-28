package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TogglePMCommand extends Command {
    public TogglePMCommand(CommandManager manager) {
        super(manager, "togglepm");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        Player player = (Player) sender;
        User user = this.getInstance().getUserManager().getByUUID(player.getUniqueId());
        String path = "TOGGLEPM_COMMAND.PM_" + (user.isPrivateMessages() ? "FALSE" : "TRUE");
        user.setPrivateMessages(!user.isPrivateMessages());
        this.sendMessage(sender, this.getLanguageConfig().getString(path));
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("tpm");
    }

    @Override
    public List<String> usage() {
        return null;
    }
}
