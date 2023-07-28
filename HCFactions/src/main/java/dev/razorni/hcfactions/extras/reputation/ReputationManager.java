package dev.razorni.hcfactions.extras.reputation;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.CC;
import org.bukkit.entity.Player;

public class ReputationManager {

    public String getPrefix(Player player) {
        User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
        if (profile.getReputations() > 1000) {
            return CC.translate("&6[ &c❥ &4" + profile.getReputations() + "&6] ");
        } else if (profile.getReputations() > 1) {
            return CC.translate("&6[&2■ &a" + profile.getReputations() + "&6] ");
        }
         return "";
    }

    public String getPrefixNoBrackets(Player player) {
        User profile = HCF.getPlugin().getUserManager().getByUUID(player.getUniqueId());
        if (profile.getReputations() > 1000) {
            return CC.translate("&c❥ &4" + profile.getReputations());
        } else if (profile.getReputations() > 1) {
            return CC.translate("&2■ &a" + profile.getReputations());
        }
        return "";
    }

}
