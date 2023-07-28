package dev.razorni.hcfactions.utils;

import dev.razorni.hcfactions.HCF;
import lombok.Data;

@Data
public class Lang {

    public static String FACTIONS_CLAIM_LEAVING_ENTERING;

    public Lang() {
        FACTIONS_CLAIM_LEAVING_ENTERING = HCF.getPlugin().getConfig().getString("FACTIONS.CLAIM_MESSAGES.LEAVING_ENTERING");
    }

}
