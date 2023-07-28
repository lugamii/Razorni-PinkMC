package dev.razorni.hcfactions.staff.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.staff.StaffManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StaffBuildCommand extends Command {
    public StaffBuildCommand(CommandManager manager) {
        super(manager, "staffbuild");
        this.setPermissible("azurite.staffbuild");
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
        Player player = (Player) sender;
        StaffManager manager = this.getInstance().getStaffManager();
        if (!manager.isStaffEnabled(player)) {
            this.sendMessage(sender, this.getLanguageConfig().getString("STAFF_BUILD_COMMAND.NOT_IN_STAFF"));
            return;
        }
        if (manager.isStaffBuild(player)) {
            manager.getStaffBuild().remove(player.getUniqueId());
            this.sendMessage(sender, this.getLanguageConfig().getString("STAFF_BUILD_COMMAND.BUILD_DISABLED"));
            return;
        }
        manager.getStaffBuild().add(player.getUniqueId());
        this.sendMessage(sender, this.getLanguageConfig().getString("STAFF_BUILD_COMMAND.BUILD_ENABLED"));
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return null;
    }
}