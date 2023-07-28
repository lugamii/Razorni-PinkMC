package dev.razorni.hcfactions.extras.killstreaks.prizes;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.killstreaks.PersistentKillstreak;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Invis extends PersistentKillstreak {

    public Invis() {
        super("Invis", 27);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600*20, 1));
        String prefix = HCF.getPlugin().getRankManager().getRankPrefix(player) + HCF.getPlugin().getRankManager().getRankColor(player) + player.getName();
        Bukkit.getServer().broadcastMessage(CC.translate("&a[KS] " + prefix + " &ehas gotten the &aInvis &ekillstreak!"));
    }
    
}
