package dev.razorni.core.extras.holograms;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.razorni.core.Core;
import dev.razorni.core.extras.holograms.placeholder.NameMCPlaceholder;

public class HologramsManager {

    public HologramsManager(Core plugin) {
        HologramsAPI.registerPlaceholder(plugin, "%namemc-likes%", 5.0,
                new NameMCPlaceholder());
    }

}
