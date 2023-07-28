package dev.razorni.gkits.profile;

import dev.razorni.gkits.GKits;
import dev.razorni.gkits.profile.listener.ProfileListener;
import cc.invictusgames.ilib.utils.callback.TypeCallable;
import com.mongodb.client.model.Filters;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class ProfileManager {

    private final GKits plugin;
    private final Map<UUID, Profile> profileMap;

    public ProfileManager(GKits plugin) {
        this.plugin = plugin;
        profileMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new ProfileListener(plugin), plugin);
    }

    public Profile getProfile(UUID uuid) {
        if (profileMap.containsKey(uuid))
            return profileMap.get(uuid);

        return profileMap.put(uuid, new Profile(uuid));
    }

    public void loadOrCreate(UUID uuid, TypeCallable<Profile> callable, boolean async) {
        if (async) {
            loadOrCreate(uuid, callable, false);
            return;
        }

        Document document = plugin.getMongoManager().getProfiles()
                .find(Filters.eq("uuid", uuid.toString())).first();

        Profile profile;

        if (profileMap.containsKey(uuid)) {
            callable.callback(getProfile(uuid));
            return;
        }

        if (document == null) {
            profile = getProfile(uuid);
            callable.callback(profile);
            return;
        }

        profile = new Profile(document);
        profileMap.put(uuid, profile);
        callable.callback(profile);
    }
}
