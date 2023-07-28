package dev.razorni.hcfactions.extras.killstreaks.prizes;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.killstreaks.Killstreak;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BountyKeys extends Killstreak {

    @Override
    public String getName() {
        return "3 Reward Keys";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                6
        };
    }

    @Override
    public void apply(Player player) {
        String prefix = HCF.getPlugin().getRankManager().getRankPrefix(player) + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName();
        Bukkit.getServer().broadcastMessage(CC.translate("&a[KS] " + prefix + " &ehas gotten the &aReward Keys &ekillstreak!"));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + player.getName() + " reward 3");
    }

}
