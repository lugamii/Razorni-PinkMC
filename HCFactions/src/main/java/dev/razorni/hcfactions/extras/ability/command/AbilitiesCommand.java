package dev.razorni.hcfactions.extras.ability.command;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.ability.menu.AbilityListMenu;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AbilitiesCommand extends Command {
    public AbilitiesCommand(CommandManager commandManager) {
        super(commandManager, "abilities");
        this.setPermissible("azurite.abilities");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("partneritems", "abilitymenu");
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
        new AbilityListMenu(this.getInstance().getMenuManager(), player).open();
    }

    @Override
    public List<String> usage() {
        return null;
    }
}
