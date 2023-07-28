package dev.razorni.hcfactions.extras.supplydrop.command;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.supplydrop.SupplyDropMenu;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SupplyDropCommand extends Command {

    public SupplyDropCommand(CommandManager manager) {
        super(manager, "airdrop");
        this.setPermissible("azurite.airdrops");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("supplydrop");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            for (String msg : HCF.getPlugin().getConfig().getStringList("AIRDROPS.HELP-USAGE")) {
                player.sendMessage(CC.translate(msg).replace("%d_arrow%", "\u00BB"));
            }
        } else if (args[0].equalsIgnoreCase("edit")) {
            new SupplyDropMenu().onOpenEditorInventory(player);
        } else if (args[0].equalsIgnoreCase("forcespawn")) {
            HCF.getPlugin().getSupplyDropManager().removeAirdrop();
            HCF.getPlugin().getSupplyDropManager().spawnAirdrop();
        }
        return;
    }

}
