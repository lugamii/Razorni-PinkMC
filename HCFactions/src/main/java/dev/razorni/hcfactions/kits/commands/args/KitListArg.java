package dev.razorni.hcfactions.kits.commands.args;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Argument;
import dev.razorni.hcfactions.kits.Kit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class KitListArg extends Argument {
    public KitListArg(CommandManager manager) {
        super(manager, Collections.singletonList("list"));
        this.setPermissible("azurite.kit.list");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permissible)) {
            this.sendMessage(sender, Config.INSUFFICIENT_PERM);
            return;
        }
        for (String s : this.getLanguageConfig().getStringList("KIT_COMMAND.KIT_LIST.KITS_LIST")) {
            if (!s.equalsIgnoreCase("%kits%")) {
                this.sendMessage(sender, s);
                return;
            }
            for (Kit kit : this.getInstance().getKitManager().getKits().values()) {
                this.sendMessage(sender, this.getLanguageConfig().getString("KIT_COMMAND.KIT_LIST.KITS_FORMAT").replaceAll("%kit%", kit.getName()).replaceAll("%seconds%", String.valueOf(kit.getSeconds())));
            }
        }
    }
}
