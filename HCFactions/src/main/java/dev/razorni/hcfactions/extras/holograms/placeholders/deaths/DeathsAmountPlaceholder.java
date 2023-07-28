package dev.razorni.hcfactions.extras.holograms.placeholders.deaths;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.users.User;

import java.util.List;

public class DeathsAmountPlaceholder implements PlaceholderReplacer {

    private int position;

    public DeathsAmountPlaceholder(int position) {
        this.position = position;
    }

    public String update() {
        List<User> topDeaths = HCF.getPlugin().getUserManager().getTopDeaths();
        if (position < topDeaths.size()) {
            User user = topDeaths.get(this.position);
            return String.valueOf(user.getDeaths());
        }
        return "None";
    }

}
