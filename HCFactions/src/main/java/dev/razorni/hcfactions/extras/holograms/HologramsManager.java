package dev.razorni.hcfactions.extras.holograms;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.holograms.placeholders.balance.BalanceAmountPlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.balance.BalanceNamePlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.deaths.DeathsAmountPlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.deaths.DeathsNamePlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.fkills.FKillsAmountPlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.fkills.FKillsNamePlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.fpoints.FPointsAmountPlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.fpoints.FPointsNamePlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.kills.KillsAmountPlaceholder;
import dev.razorni.hcfactions.extras.holograms.placeholders.kills.KillsNamePlaceholder;

public class HologramsManager {

    public HologramsManager(HCF plugin) {
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%ftop_points_name_" + i + "%", 60.0,
                    new FPointsNamePlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%ftop_points_amount_" + i + "%", 60.0,
                    new FPointsAmountPlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%ftop_kills_name_" + i + "%", 60.0,
                    new FKillsNamePlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%ftop_kills_amount_" + i + "%", 60.0,
                    new FKillsAmountPlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%killstop_name_" + i + "%", 60.0,
                    new KillsNamePlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%killstop_amount_" + i + "%", 60.0,
                    new KillsAmountPlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%deathstop_name_" + i + "%", 60.0,
                    new DeathsNamePlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%deathstop_amount_" + i + "%", 60.0,
                    new DeathsAmountPlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%baltop_name_" + i + "%", 60.0,
                    new BalanceNamePlaceholder(i));
        }
        for (int i = 0; i <= 9; ++i) {
            HologramsAPI.registerPlaceholder(plugin, "%baltop_amount_" + i + "%", 60.0,
                    new BalanceAmountPlaceholder(i));
        }
    }

}
