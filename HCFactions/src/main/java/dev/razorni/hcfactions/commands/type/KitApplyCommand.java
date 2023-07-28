package dev.razorni.hcfactions.commands.type;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.kits.Kit;
import org.bukkit.entity.Player;

public class KitApplyCommand {

    @Command(names = "kitapply", permission = "")
    public static void kitapply(final Player player, @Param(name = "kitname") String kitName) {
        Kit kit = HCF.getPlugin().getKitManager().getKit(kitName);
        if (kit == null) {
            player.sendMessage(CC.RED + "That kit does not exist.");
            return;
        }
        if (!player.hasPermission("azurite.kit." + kitName) || !player.isOp()) {
            player.sendMessage(CC.RED + "You dont have permission for this kit.");
            return;
        }
        kit.equip(player);
    }

}