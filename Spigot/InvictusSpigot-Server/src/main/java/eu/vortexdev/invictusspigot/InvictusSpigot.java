package eu.vortexdev.invictusspigot;

import com.google.common.collect.Lists;
import eu.vortexdev.api.protocol.MovementListenerAdapter;
import eu.vortexdev.api.protocol.PacketListenerAdapter;
import eu.vortexdev.invictusspigot.async.ThreadingManager;
import eu.vortexdev.invictusspigot.knockback.CraftKnockbackProfile;
import eu.vortexdev.invictusspigot.knockback.KnockbackManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.List;

public enum InvictusSpigot {
    INSTANCE;

    private final ThreadingManager threadingManager = new ThreadingManager();
    private final KnockbackManager knockbackManager = new KnockbackManager();
    private final List<PacketListenerAdapter> packetListeners = Lists.newArrayList();
    private final List<MovementListenerAdapter> movementListeners = Lists.newArrayList();

    public void init() {
        ConfigurationSerialization.registerClass(CraftKnockbackProfile.class);
        knockbackManager.reloadProfiles();
    }

    public void shutdown() {
        knockbackManager.saveProfiles();
        threadingManager.shutdown();
    }

    public List<PacketListenerAdapter> getPacketListeners() {
        return packetListeners;
    }

    public List<MovementListenerAdapter> getMovementListeners() {
        return movementListeners;
    }

    public KnockbackManager getKnockbackManager() {
        return knockbackManager;
    }

    public ThreadingManager getThreadingManager() {
        return threadingManager;
    }

}
