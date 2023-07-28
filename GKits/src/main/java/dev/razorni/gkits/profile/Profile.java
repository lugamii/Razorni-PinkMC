package dev.razorni.gkits.profile;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.gkit.GKit;
import dev.razorni.gkits.gkit.GKitCooldown;
import cc.invictusgames.ilib.utils.TimeUtils;
import com.mongodb.client.model.UpdateOptions;
import lombok.Data;
import org.bson.Document;

import java.util.*;

@Data
public class Profile {

    private Set<GKitCooldown> gKitCooldowns = new HashSet<>();
    private final UUID uuid;

    private boolean justJoined = false;

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public Profile(Document document) {
        uuid = UUID.fromString(document.getString("uuid"));

        if (document.containsKey("gKitCooldowns")) {
            for (Document foundKit : document.getList("gKitCooldowns", Document.class)) {
                GKitCooldown gKitCooldown = new GKitCooldown(foundKit);

                if (gKitCooldown.getGKit() != null)
                    gKitCooldowns.add(gKitCooldown);
            }
        }
    }

    public void resetCooldowns() {
        gKitCooldowns = new HashSet<>();
    }

    public void applyCooldown(GKit gKit) {
        gKitCooldowns.add(new GKitCooldown(gKit, System.currentTimeMillis()));
        save(true);
    }

    public String formatRemaining(GKit gKit) {
        if (!isOnCooldown(gKit))
            return "0s";

        GKitCooldown gKitCooldown = getCooldownFor(gKit);
        if (gKitCooldown != null)
            return TimeUtils.formatTimeShort(gKitCooldown.getRemaining() + gKit.getCoolDown()
                    - System.currentTimeMillis());

        return "0s";
    }

    public long getCooldown(GKit gKit) {
        GKitCooldown gKitCooldown = getCooldownFor(gKit);
        if (gKitCooldown == null)
            return Long.MAX_VALUE;

        return gKitCooldown.getRemaining() + gKit.getCoolDown()
                - System.currentTimeMillis();
    }

    public boolean isOnCooldown(GKit gkit) {
        GKitCooldown gKitCooldown = getCooldownFor(gkit);
        return gKitCooldown != null
                && gKitCooldown.getRemaining() + gkit.getCoolDown() > System.currentTimeMillis();
    }

    public GKitCooldown getCooldownFor(GKit gKit) {
        for (GKitCooldown gKitCooldown : gKitCooldowns) {
            if (gKitCooldown.getGKit() == null)
                continue;

            if (gKitCooldown.getGKit().getUuid().equals(gKit.getUuid()))
                return gKitCooldown;
        }

        return null;
    }

    public Document toBson() {
        List<Document> cooldowns = new ArrayList<>();
        for (GKitCooldown gKitCooldown : gKitCooldowns)
            cooldowns.add(gKitCooldown.toBson());

        return new Document()
                .append("uuid", uuid.toString())
                .append("gKitCooldowns", cooldowns);
    }

    public void save(boolean async) {
        if (async) {
            GKits.get().getExecutorService().execute(() -> save(false));
            return;
        }

        Document document = this.toBson();
        GKits.get().getMongoManager().getProfiles().updateOne(new Document("uuid", document.getString("uuid")),
                new Document("$set", document), new UpdateOptions().upsert(true));
    }

}
