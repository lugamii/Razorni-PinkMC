package dev.razorni.hcfactions.extras.trade.command;

import dev.razorni.hcfactions.utils.menuapi.CC;
import dev.razorni.hcfactions.utils.menuapi.Clickable;
import dev.razorni.hcfactions.utils.menuapi.Request;
import dev.razorni.hcfactions.utils.menuapi.TimeUtil;
import dev.razorni.hcfactions.utils.commandapi.command.Command;
import dev.razorni.hcfactions.utils.commandapi.command.Param;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.trade.TradeMenu;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.SafezoneTeam;
import org.bukkit.entity.Player;

public class TradeCommand {

    @Command(names = {"trade"}, permission = "")
    public static void trade(Player player, @Param(name = "target") Player target) {

    if (player == target) {
      player.sendMessage(CC.translate("&cYou cannot trade with yourself."));
      return;
    }

    Team cTeam = HCF.getPlugin().getTeamManager().getClaimManager().getTeam(player.getLocation());
    if (!(cTeam instanceof SafezoneTeam)) {
      player.sendMessage(CC.translate("&cYou can only use this command at Spawn."));
      return;
    }

    if (player.getLocation().distance(target.getLocation()) > 6) {
      player.sendMessage(
          CC.translate("&cYou are too far away from " + target.getName() + " to trade."));
      return;
    }

    Request request = new Request(
        new Clickable("&c" + player.getName() + " &fhas sent a trade request, &cclick here &fto accept.",
            "&bClick to accept",
            "/trade accept"),
        player.getUniqueId(), target.getUniqueId(),
        System.currentTimeMillis() + TimeUtil.parseTimeLong("20s"));

    request.addAction((players) -> {

      Player player1 = players.get(0);
      Player player2 = players.get(1);

      new TradeMenu(player2, player1).openMenu(player1);
    });

    request.send();

    player.sendMessage(
        CC.translate("&aSuccessfully sent a trade request to " + target.getName() + "&a."));
    }

    @Command(names = {"trade accept"}, permission = "")
    public static void tradeAccept(Player player) {
        if (Request.hasRequest(player)) {
            Request request = Request.getRequest(player);

            request.execute();
        } else {
            player.sendMessage(CC.translate("&cYou do not have a pending trade request."));
        }
    }

}