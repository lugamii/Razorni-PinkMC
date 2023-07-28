package dev.razorni.hcfactions.staff.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.staff.StaffManager;
import dev.razorni.hcfactions.staff.task.FreezeMessageTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class FreezeCommand extends Command {
    private final Map<UUID, FreezeMessageTask> tasks;

    public FreezeCommand(CommandManager manager) {
        super(manager, "freeze");
        this.tasks = new HashMap<>();
        this.setPermissible("azurite.freeze");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        StaffManager manager = this.getInstance().getStaffManager();
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        if (target.hasPermission("azurite.freeze.bypass")) {
            this.sendMessage(sender, this.getLanguageConfig().getString("FREEZE_COMMAND.CANNOT_FREEZE"));
            return;
        }
        if (manager.isFrozen(target)) {
            manager.unfreezePlayer(target);
            this.tasks.get(target.getUniqueId()).cancel();
            this.sendMessage(sender, this.getLanguageConfig().getString("FREEZE_COMMAND.UNFROZE_PLAYER").replaceAll("%player%", target.getName()));
            return;
        }
        manager.freezePlayer(target);
        this.tasks.put(target.getUniqueId(), new FreezeMessageTask(this.getInstance().getStaffManager(), target));
        this.sendMessage(sender, this.getLanguageConfig().getString("FREEZE_COMMAND.FROZE_PLAYER").replaceAll("%player%", target.getName()));
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("froze");
    }

    @Override
    public List<String> usage() {
        return this.getLanguageConfig().getStringList("FREEZE_COMMAND.USAGE");
    }
}
