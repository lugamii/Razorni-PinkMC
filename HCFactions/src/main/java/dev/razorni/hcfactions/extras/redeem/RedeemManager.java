package dev.razorni.hcfactions.extras.redeem;

import com.google.common.collect.ImmutableList;
import dev.razorni.hcfactions.HCF;
import lombok.Getter;

import java.util.List;

public class RedeemManager {

    @Getter
    private static List<RedeemablePartner> redeemablePartners = ImmutableList.of(
            new RedeemablePartner("Razorni", "Razorni")
    );
    private final HCF plugin;

    public RedeemManager(HCF plugin) {
        this.plugin = plugin;
    }

    public RedeemablePartner getPartner(String id) {
        for (RedeemablePartner partner : redeemablePartners) {
            if (partner.getId().equalsIgnoreCase(id)) {
                return partner;
            }
        }

        return null;
    }

}
