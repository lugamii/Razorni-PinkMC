package dev.razorni.hcfactions.extras.holograms.placeholders.kills;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;

import java.util.List;

public class KillsAmountPlaceholder implements PlaceholderReplacer {

    private int position;

    public KillsAmountPlaceholder(int position) {
        this.position = position;
    }

    public String update() {
        List<User> topKills = HCF.getPlugin().getUserManager().getTopKills();
        if (position < topKills.size()) {
            User user = topKills.get(this.position);
            return String.valueOf(user.getKills());
        }
        return "None";
    }

}
