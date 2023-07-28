package dev.razorni.hcfactions.extras.redeem.command;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.commands.Command;
import dev.razorni.hcfactions.extras.redeem.menu.RedeemMenu;
import dev.razorni.hcfactions.users.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RedeemCommand extends Command {

    public RedeemCommand(CommandManager manager) {
        super(manager, "redeem");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("support");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());

        if (user.isRedeemed()) {
            player.sendMessage(CC.translate(HCF.getPlugin().getConfig().getString("REDEEM.ALREADY-REDEEMED")));
            return;
        }

        new RedeemMenu().openMenu(player);
    }

}
