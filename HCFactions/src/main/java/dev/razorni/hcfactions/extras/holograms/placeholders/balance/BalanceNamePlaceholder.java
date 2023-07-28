package dev.razorni.hcfactions.extras.holograms.placeholders.balance;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;
import org.bukkit.Bukkit;

import java.util.List;

public class BalanceNamePlaceholder implements PlaceholderReplacer {

    private int position;

    public BalanceNamePlaceholder(int position) {
        this.position = position;
    }

    public String update() {
        List<User> topBa = HCF.getPlugin().getUserManager().getTopBalance();
        if (position < topBa.size()) {
            User user = topBa.get(this.position);
            return Bukkit.getOfflinePlayer(user.getUniqueID()).getName();
        }
        return "None";
    }

}
