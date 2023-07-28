package dev.razorni.hub.utils.hologramapi;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.razorni.hub.Hub;
import dev.razorni.hub.utils.hologramapi.placeholders.HCFQueuedPlaceholder;

public class HologramsManager {

    public HologramsManager(Hub plugin) {
        HologramsAPI.registerPlaceholder(plugin, "%queued-hcf%", 1.0,
                new HCFQueuedPlaceholder());
    }

}
