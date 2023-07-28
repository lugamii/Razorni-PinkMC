package dev.razorni.hcfactions.commands.type.essential;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ClearChatCommand extends Command {
    private final String clearString;

    public ClearChatCommand(CommandManager sender) {
        super(sender, "clearchat");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= 300; ++i) {
            builder.append(CC.t("&7 &d &f &3 &b &9 &6 \n"));
        }
        this.clearString = String.valueOf(builder);
        this.setPermissible("azurite.clearchat");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("cc");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        Bukkit.broadcastMessage(this.clearString);
    }
}
