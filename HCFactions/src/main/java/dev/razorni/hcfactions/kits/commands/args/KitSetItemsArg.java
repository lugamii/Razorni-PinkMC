package dev.razorni.hcfactions.kits.commands.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.kits.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KitSetItemsArg extends Argument {
    public KitSetItemsArg(CommandManager manager) {
        super(manager, Arrays.asList("setitems", "edit"));
        this.setPermissible("azurite.kit.setitems");
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
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        String kitName = args[0];
        Kit kit = this.getInstance().getKitManager().getKit(kitName);
        if (kit == null) {
            this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.NOT_FOUND").replaceAll("%kit%", kitName));
            return;
        }
        kit.update(player.getInventory().getContents(), player.getInventory().getArmorContents());
        kit.save();
        this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_SETITEMS.SET_ITEMS").replaceAll("%kit%", kit.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String other = args[args.length - 1];
            return this.getInstance().getKitManager().getKits().keySet().stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        return super.tabComplete(sender, args);
    }

    @Override
    public String usage() {
        return this.getLanguageConfig().getString("KIT_COMMAND.KIT_SETITEMS.USAGE");
    }
}
