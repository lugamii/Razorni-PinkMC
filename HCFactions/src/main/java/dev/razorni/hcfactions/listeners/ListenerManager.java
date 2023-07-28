package dev.razorni.hcfactions.listeners;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.listeners.type.*;
import dev.razorni.hcfactions.listeners.type.team.PlayerTeamListener;
import dev.razorni.hcfactions.listeners.type.team.TeamListener;

public class ListenerManager extends Manager {
    public ListenerManager(HCF plugin) {
        super(plugin);
        new MainListener(this);
        new ChatListener(this);
        new DeathListener(this);
        new FixListener(this);
        new BorderListener(this);
        new GlitchListener(this);
        new WorldListener(this);
        new CobbleListener(this);
        new DiamondListener(this);
        new PortalListener(this);
        new SmeltListener(this);
        new DropListener(this);
        new StackListener(this);
        new LimiterListener(this);
        new DurabilityListener(this);
        new StrengthListener(this);
        new EndListener(this);
        new TeamListener(this.getInstance().getTeamManager());
        new PlayerTeamListener(this.getInstance().getTeamManager());
    }
}
