package dev.razorni.hcfactions.extras.killstreaks.prizes;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.killstreaks.Killstreak;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gapple extends Killstreak {

    @Override
    public String getName() {
        return "God Apple";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                12
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
        String prefix = HCF.getPlugin().getRankManager().getRankPrefix(player) + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName();
        Bukkit.getServer().broadcastMessage(CC.translate("&a[KS] " + prefix + " &ehas gotten the &aGod Apple &ekillstreak!"));
    }

}
