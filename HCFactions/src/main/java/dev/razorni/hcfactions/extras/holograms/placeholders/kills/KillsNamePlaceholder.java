package dev.razorni.hcfactions.extras.holograms.placeholders.kills;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Bukkit;

import java.util.List;

public class KillsNamePlaceholder implements PlaceholderReplacer {

    private int position;

    public KillsNamePlaceholder(int position) {
        this.position = position;
    }

    public String update() {
        List<User> topKills = HCF.getPlugin().getUserManager().getTopKills();
        if (position < topKills.size()) {
            User user = topKills.get(this.position);
            return Bukkit.getOfflinePlayer(user.getUniqueID()).getName();
        }
        return "None";
    }

}
