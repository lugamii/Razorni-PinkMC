package dev.razorni.hcfactions.extras.killstreaks.prizes;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.killstreaks.Killstreak;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GoldenApples extends Killstreak {

    @Override
    public String getName() {
        return "5 Golden Apples";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                3
        };
    }

    @Override
    public void apply(Player player) {
        String prefix = HCF.getPlugin().getRankManager().getRankPrefix(player) + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName();
        Bukkit.getServer().broadcastMessage(CC.translate("&a[KS] " + prefix + " &ehas gotten the &a5 Golden Apple &ekillstreak!"));
        give(player, new ItemStack(Material.GOLDEN_APPLE, 5));
    }

}