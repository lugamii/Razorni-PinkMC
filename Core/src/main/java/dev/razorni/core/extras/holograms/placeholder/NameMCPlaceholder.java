package dev.razorni.core.extras.holograms.placeholder;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import dev.razorni.core.Core;

public class NameMCPlaceholder implements PlaceholderReplacer {

    public String update() {
        return String.valueOf(Core.getInstance().getVerificationHandler().getSize());
    }

}
