package dev.razorni.hcfactions.extras.reputation.command;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReputationCommand {

    @Command(names = {"reputation", "rep"}, permission = "")
    public static void reputation(CommandSender sender, @Param(name = "target") Player target) {
        if (target.getName().equals(sender.getName())) {
            sender.sendMessage(CC.translate("&c❤ &eYour Reputation equals to &6" + HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId()).getReputations() + " &epoints!"));
        } else {
            sender.sendMessage(CC.translate("&c❤ &6" + target.getName() + " Reputation equals to &6" + HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId()).getReputations() + " &epoints!"));
        }
    }

    @Command(names = "reputation set", permission = "azurite.reputation")
    public static void reputationset(CommandSender sender, @Param(name = "target") Player target, @Param(name = "amount") int amount) {
        User profile = HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId());
        sender.sendMessage(CC.translate("&aYou have successfully set reputatons to " + target.getName() + "."));
        profile.setReputations(amount);
        profile.save();
    }

    @Command(names = "reputation add", permission = "azurite.reputation")
    public static void reputationadd(CommandSender sender, @Param(name = "target") Player target, @Param(name = "amount") int amount) {
        User profile = HCF.getPlugin().getUserManager().getByUUID(target.getUniqueId());
        sender.sendMessage(CC.translate("&aYou have successfully add reputatons to " + target.getName() + "."));
        profile.addReputation(amount);
        profile.save();
    }

}
