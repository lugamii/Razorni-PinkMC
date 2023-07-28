package dev.razorni.hcfactions.extras.holograms.placeholders.deaths;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Bukkit;

import java.util.List;

public class DeathsNamePlaceholder implements PlaceholderReplacer {

    private int position;

    public DeathsNamePlaceholder(int position) {
        this.position = position;
    }

    public String update() {
        List<User> topDeaths = HCF.getPlugin().getUserManager().getTopDeaths();
        if (position < topDeaths.size()) {
            User user = topDeaths.get(this.position);
            return Bukkit.getOfflinePlayer(user.getUniqueID()).getName();
        }
        return "None";
    }

}
