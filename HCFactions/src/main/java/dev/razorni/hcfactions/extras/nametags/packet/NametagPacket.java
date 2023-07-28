package dev.razorni.hcfactions.extras.nametags.packet;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.nametags.NametagManager;
import dev.razorni.hcfactions.extras.nametags.extra.NameVisibility;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class NametagPacket extends Module<NametagManager> {
    protected Player player;

    public NametagPacket(NametagManager manager, Player player) {
        super(manager);
        this.player = player;
    }

    public abstract void addToTeam(Player player, String team);

    public abstract void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibilitt);
}
