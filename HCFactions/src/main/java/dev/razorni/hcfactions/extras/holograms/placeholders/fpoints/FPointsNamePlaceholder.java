package dev.razorni.hcfactions.extras.holograms.placeholders.fpoints;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.teams.type.PlayerTeam;

import java.util.List;

public class FPointsNamePlaceholder implements PlaceholderReplacer {

    private int position;

    public FPointsNamePlaceholder(int position) {
        this.position = position;
    }

    public String update() {
        List<PlayerTeam> topTeams = HCF.getPlugin().getTeamManager().getTeamSorting().getTeamTop();
        if (this.position >= topTeams.size()) {
            return "None";
        }
        PlayerTeam team = topTeams.get(this.position);

        return String.valueOf(team.getName());
    }

}
