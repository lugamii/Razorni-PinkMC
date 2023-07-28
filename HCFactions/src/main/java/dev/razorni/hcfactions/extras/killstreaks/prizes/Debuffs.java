package dev.razorni.hcfactions.extras.killstreaks.prizes;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.killstreaks.Killstreak;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Debuffs extends Killstreak {

    @Override
    public String getName() {
        return "Debuffs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                9
        };
    }

    @Override
    public void apply(Player player) {
        Potion poison = new Potion(PotionType.POISON);
        poison.setSplash(true);

        Potion slowness = new Potion(PotionType.SLOWNESS);
        slowness.setSplash(true);

        give(player, poison.toItemStack(1));
        give(player, slowness.toItemStack(1));

        String prefix = HCF.getPlugin().getRankManager().getRankPrefix(player) + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName();
        Bukkit.getServer().broadcastMessage(CC.translate("&a[KS] " + prefix + " &ehas gotten the &aDebuffs &ekillstreak!"));

    }

}
