package dev.razorni.hub.utils.hologramapi.placeholders;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.hub.Hub;

public class HCFQueuedPlaceholder implements PlaceholderReplacer {

    public String update() {
        return String.valueOf(Hub.getInstance().getQueueManager().getInQueue("HCF"));
    }

}
