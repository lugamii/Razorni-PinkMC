package dev.razorni.hcfactions.extras.vouchers.listener;

import cc.invictusgames.ilib.utils.CC;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.vouchers.Voucher;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.EventTeam;
import dev.razorni.hcfactions.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class VoucherListener implements Listener {

    @EventHandler
    public void onClickEvent(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY
                && event.useItemInHand() == Event.Result.DENY || !event.getAction().name().contains("RIGHT")) {
            return;
        }
        if (!event.hasItem() || !event.getItem().hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = event.getItem().getItemMeta();
        if (!itemMeta.hasDisplayName() || !itemMeta.hasLore()) {
            return;
        }

        int hash = Voucher.calculateItemHash(itemMeta);
        Voucher voucher = Voucher.getVouchers().get(hash);

        if (voucher == null) {
            return;
        }

        if (HCF.getPlugin().getTimerManager().getAppleTimer().hasTimer(event.getPlayer())) {
            event.getPlayer().sendMessage(CC.translate("&cYou cannot use this while tagged."));
            return;
        }

        Team cTeam = HCF.getPlugin().getTeamManager().getClaimManager().getTeam(event.getPlayer().getLocation());
        if (cTeam instanceof EventTeam) {
            event.getPlayer().sendMessage(CC.RED + "You cannot use this inside events.");
            return;
        }

        if (voucher.getCommands() != null && !voucher.getCommands().isEmpty()) {
            for (String command : voucher.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("%player%", event.getPlayer().getName()));
            }
            Utils.removeOneItem(event.getPlayer());
        }
    }
}