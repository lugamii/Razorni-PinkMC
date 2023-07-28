package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.users.menu.SettingsMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SettingsCommand extends Command {
    public SettingsCommand(CommandManager manager) {
        super(manager, "setting");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, Config.PLAYER_ONLY);
            return;
        }
        new SettingsMenu(this.getInstance().getMenuManager(), (Player) sender).open();
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("settings");
    }
}
