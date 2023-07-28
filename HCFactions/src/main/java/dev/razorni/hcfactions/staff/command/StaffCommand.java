package dev.razorni.hcfactions.staff.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.staff.StaffManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StaffCommand extends Command {
    public StaffCommand(CommandManager manager) {
        super(manager, "staff");
        this.setPermissible("azurite.staff");
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
        if (manager.isStaffEnabled(player)) {
            manager.disableStaff(player);
            this.sendMessage(sender, this.getLanguageConfig().getString("STAFF_MODE.DISABLED_STAFF"));
            return;
        }
        manager.enableStaff(player);
        player.sendMessage(this.getLanguageConfig().getString("STAFF_MODE.ENABLED_STAFF"));
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("mod", "modmode", "sm", "staffmode", "h");
    }

    @Override
    public List<String> usage() {
        return null;
    }
}
