package dev.razorni.hcfactions.kits.commands.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KitApplyArg extends Argument {
    public KitApplyArg(CommandManager manager) {
        super(manager, Collections.singletonList("apply"));
        this.setPermissible("azurite.kit.apply");
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KIT_COMMAND.KIT_APPLY.USAGE");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 2) {
            String other = args[args.length - 1];
            return this.getInstance().getKitManager().getKits().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length < 2) {
            this.sendUsage(sender);
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        String kitName = args[1];
        Kit kit = this.getInstance().getKitManager().getKit(kitName);
        if (kit == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.NOT_FOUND").replaceAll("%kit%", kitName));
            return;
        }
        if (target == null) {
            this.sendMessage(sender, Config.PLAYER_NOT_FOUND.replaceAll("%player%", args[0]));
            return;
        }
        kit.equip(target);
        this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_APPLY.APPLIED").replaceAll("%kit%", kit.getName()).replaceAll("%player%", target.getName()));
    }
}
