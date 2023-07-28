package dev.razorni.hcfactions.extras.killstreaks.prizes;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.killstreaks.PersistentKillstreak;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed2 extends PersistentKillstreak {

    public Speed2() {
        super("Speed 2", 12);
    }

    public void apply(Player player) {
        String prefix = HCF.getPlugin().getRankManager().getRankPrefix(player) + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName();
        Bukkit.getServer().broadcastMessage(CC.translate("&a[KS] " + prefix + " &ehas gotten the &aSpeed 2 &ekillstreak!"));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300*20, 1));
    }

}
