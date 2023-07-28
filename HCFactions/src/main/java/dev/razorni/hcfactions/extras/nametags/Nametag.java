package dev.razorni.hcfactions.extras.nametags;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.nametags.packet.NametagPacket;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class Nametag extends Module<NametagManager> {
    private final Player player;
    private final NametagPacket packet;

    public Nametag(NametagManager manager, Player player) {
        super(manager);
        this.player = player;
        this.packet = manager.createPacket(player);
    }
}
