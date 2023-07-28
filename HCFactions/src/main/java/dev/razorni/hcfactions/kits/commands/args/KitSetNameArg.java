package dev.razorni.hcfactions.kits.commands.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KitSetNameArg extends Argument {
    public KitSetNameArg(CommandManager manager) {
        super(manager, Arrays.asList("setname", "updatename"));
        this.setPermissible("azurite.kit.setname");
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
        Kit kit = this.getInstance().getKitManager().getKit(args[0]);
        String name = args[1];
        if (kit == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.NOT_FOUND").replaceAll("%kit%", args[0]));
            return;
        }
        this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_SETNAME.SET_NAME").replaceAll("%oldName%", kit.getName()).replaceAll("%newName%", name));
        this.getInstance().getKitManager().getKits().remove(kit.getName());
        this.getInstance().getKitManager().getKits().put(name, kit);
        kit.setName(name);
        kit.save();
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KIT_COMMAND.KIT_SETNAME.USAGE");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getKitManager().getKits().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }
}
