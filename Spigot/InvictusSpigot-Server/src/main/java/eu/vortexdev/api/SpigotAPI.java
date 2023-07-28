package eu.vortexdev.api;

import eu.vortexdev.api.protocol.MovementListenerAdapter;
import eu.vortexdev.api.protocol.PacketListenerAdapter;
import eu.vortexdev.invictusspigot.InvictusSpigot;

import java.util.List;

public class SpigotAPI {

    public static List<PacketListenerAdapter> getPacketListeners() {
        return InvictusSpigot.INSTANCE.getPacketListeners();
    }

    public static List<MovementListenerAdapter> getMovementListeners() {
        return InvictusSpigot.INSTANCE.getMovementListeners();
    }

    public static void addMovementListener(MovementListenerAdapter listener) {
        InvictusSpigot.INSTANCE.getMovementListeners().add(listener);
    }

    public static void addPacketListener(PacketListenerAdapter listener) {
        InvictusSpigot.INSTANCE.getPacketListeners().add(listener);
    }

}
