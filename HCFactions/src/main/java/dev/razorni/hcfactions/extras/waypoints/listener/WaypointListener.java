package dev.razorni.hcfactions.extras.waypoints.listener;

import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.serverrule.LunarClientAPIServerRule;
import dev.razorni.hcfactions.events.koth.Koth;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.waypoints.WaypointManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.function.UnaryOperator;

public class WaypointListener extends Module<WaypointManager> {

    public WaypointListener(WaypointManager waypointManager) {
        super(waypointManager);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(player.getUniqueId());
        this.getManager().getSpawnWaypoint().send(player, player.getWorld().getSpawnLocation(), UnaryOperator.identity());
        this.getManager().getEndExitWaypoint().send(player, this.getManager().getEndExit(), UnaryOperator.identity());
        this.sendServerRule(player);
        for (Koth koth : this.getInstance().getKothManager().getKoths().values()) {
            if (koth.getCaptureZone() == null) {
                continue;
            }
            if (!koth.isActive()) {
                continue;
            }
            this.getManager().getKothWaypoint().send(player, koth.getCaptureZone().getCenter(), kothstring -> kothstring.replaceAll("%name%", koth.getName()));
        }
        if (team != null) {
            this.getManager().getHqWaypoint().send(player, team.getHq(), UnaryOperator.identity());
            this.getManager().getRallyWaypoint().send(player, team.getRallyPoint(), UnaryOperator.identity());
            if (team.getFocus() != null) {
                Team teamfocused = team.getFocusedTeam();
                this.getManager().getFocusWaypoint().send(player, teamfocused.getHq(), llllllllllllllllIIlIlIIIIIlIlIll -> llllllllllllllllIIlIlIIIIIlIlIll.replaceAll("%team%", teamfocused.getName()));
            }
        }
    }

    @EventHandler
    public void onChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        this.getManager().getSpawnWaypoint().remove(player, event.getFrom().getSpawnLocation(), UnaryOperator.identity());
        this.getManager().getSpawnWaypoint().send(player, player.getWorld().getSpawnLocation(), UnaryOperator.identity());
    }

    private void sendServerRule(Player player) {
        if (this.getManager().isLunarMissing()) {
            return;
        }
        if (this.getLunarConfig().getBoolean("LUNAR_API.FIX_1_8_HIT_DELAY")) {
            LunarClientAPIServerRule.setRule(ServerRule.LEGACY_COMBAT, Boolean.TRUE);
            LunarClientAPIServerRule.sendServerRule(player);
        }
    }
}
