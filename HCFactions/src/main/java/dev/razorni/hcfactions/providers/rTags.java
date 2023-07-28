package dev.razorni.hcfactions.providers;

import dev.razorni.hcfactions.events.king.KingManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.nametags.Nametag;
import dev.razorni.hcfactions.extras.nametags.NametagAdapter;
import dev.razorni.hcfactions.extras.nametags.NametagManager;
import dev.razorni.hcfactions.extras.nametags.extra.NameVisibility;
import dev.razorni.hcfactions.staff.StaffManager;
import dev.razorni.hcfactions.teams.type.PlayerTeam;
import dev.razorni.hcfactions.timers.listeners.servertimers.SOTWTimer;
import dev.razorni.hcfactions.utils.versions.type.Version1_8_R3;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class rTags extends Module<NametagManager> implements NametagAdapter {
    private final boolean invisTeam;

    public rTags(NametagManager manager) {
        super(manager);
        this.invisTeam = (this.getInstance().getVersionManager().getVersion() instanceof Version1_8_R3);
    }

    private String createTeam(Player from, Player to, String id, String prefix, String suffix, NameVisibility visibility) {
        Nametag nametag = this.getManager().getNametags().get(from.getUniqueId());
        String displayName = prefix.isEmpty() ? "" : prefix + " ";
        if (nametag != null) {
            nametag.getPacket().create(id, suffix, displayName, "", true, visibility);
            nametag.getPacket().addToTeam(to, id);
        }
        return displayName + suffix;
    }

    private String createTeam(Player from, Player to, String id, String prefix, String suffix) {
        return this.createTeam(from, to, id, prefix, suffix, NameVisibility.ALWAYS);
    }

    @Override
    public String getAndUpdate(Player from, Player to) {
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(from.getUniqueId());
        SOTWTimer sotwTimer = this.getInstance().getTimerManager().getSotwTimer();
        KingManager kingManager = this.getInstance().getKingManager();
        StaffManager staffManager = this.getInstance().getStaffManager();
        if (staffManager.isStaffEnabled(to)) {
            return this.createTeam(from, to, "staff", Config.PREFIX_STAFF, Config.RELATION_STAFF);
        }
        if (kingManager.isActive() && kingManager.getKing() == to) {
            return this.createTeam(from, to, "king", Config.PREFIX_KING, Config.RELATION_KING);
        }
        if ((team != null && team.getPlayers().contains(to.getUniqueId())) || from == to) {
            return this.createTeam(from, to, "team", "", Config.RELATION_TEAMMATE);
        }
        if (this.invisTeam && to.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return this.createTeam(from, to, "invis", "", "", NameVisibility.NEVER);
        }
        if (team != null && team.isAlly(to)) {
            return this.createTeam(from, to, "allies", "", Config.RELATION_ALLIED);
        }
        if (sotwTimer.isActive() && !sotwTimer.getEnabled().contains(to.getUniqueId())) {
            return this.createTeam(from, to, "sotw", "", Config.RELATION_SOTW);
        }
        if (this.getInstance().getTimerManager().getArcherTagTimer().hasTimer(to)) {
            return this.createTeam(from, to, "tags", "", Config.RELATION_ARCHERTAG);
        }
        if (team != null && team.isFocused(to)) {
            return this.createTeam(from, to, "focused", "", Config.RELATION_FOCUSED);
        }
        if (this.getInstance().getTimerManager().getPvpTimer().hasTimer(to)) {
            return this.createTeam(from, to, "pvptimers", "", Config.RELATION_PVPTIMER);
        }
        if (this.getInstance().getTimerManager().getInvincibilityTimer().hasTimer(to)) {
            return this.createTeam(from, to, "invinc", "", Config.RELATION_INVINCIBLES);
        }
        return this.createTeam(from, to, "enemies", "", Config.RELATION_ENEMY);
    }
}
