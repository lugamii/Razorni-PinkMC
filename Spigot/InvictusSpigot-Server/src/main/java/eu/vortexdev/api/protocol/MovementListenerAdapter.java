package eu.vortexdev.api.protocol;

import net.minecraft.server.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface MovementListenerAdapter {
    default void onUpdateLocation(Player player, Location from, Location to, PacketPlayInFlying packet) {
    }

    default void onUpdateBlockLocation(Player player, Location from, Location to) {
    }

    default void onUpdateRotation(Player player, Location from, Location to, PacketPlayInFlying packet) {
    }
}
