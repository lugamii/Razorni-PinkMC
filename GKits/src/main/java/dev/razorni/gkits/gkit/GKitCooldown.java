package dev.razorni.gkits.gkit;

import dev.razorni.gkits.GKits;
import lombok.Data;
import org.bson.Document;

import java.util.UUID;

@Data
public class GKitCooldown {

    private final GKit gKit;
    private long remaining;

    public GKitCooldown(GKit gKit, long remaining) {
        this.gKit = gKit;
        this.remaining = remaining;
    }

    public GKitCooldown(Document document) {
        this.gKit = GKits.get().getGKitManager().getGKit(UUID.fromString(document.getString("gkit")));
        this.remaining = document.getLong("cooldown");
    }

    public Document toBson() {
        return new Document()
                .append("gkit", gKit.getUuid().toString())
                .append("cooldown", remaining);
    }
}
