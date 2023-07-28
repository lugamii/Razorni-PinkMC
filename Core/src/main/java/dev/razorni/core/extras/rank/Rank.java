package dev.razorni.core.extras.rank;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.razorni.core.Core;
import dev.razorni.core.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bson.Document;
import org.bukkit.ChatColor;
import dev.razorni.core.profile.Profile;

import java.util.*;
import java.util.stream.Collectors;

public class Rank {

    @Getter
    private static Map<UUID, Rank> ranks = new HashMap<>();
    private static MongoCollection<Document> collection;

    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    private String displayName;
    @Getter
    private final List<String> permissions = new ArrayList<>();
    @Getter
    private final List<Rank> inherited = new ArrayList<>();
    @Getter
    @Setter
    private String prefix = "";
    @Getter
    @Setter
    private String suffix = "";
    @Getter
    @Setter
    private ChatColor color = ChatColor.WHITE;
    @Getter
    @Setter
    private int weight;
    @Setter
    private boolean defaultRank;

    public Rank(String displayName) {
        this.uuid = UUID.randomUUID();
        this.displayName = displayName;

        ranks.putIfAbsent(uuid, this);
    }

    public Rank(UUID uuid, String displayName) {
        this.uuid = uuid;
        this.displayName = displayName;
    }

    public Rank(UUID uuid, String displayName, String prefix, String suffix, ChatColor color, int weight, boolean defaultRank) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
        this.weight = weight;
        this.defaultRank = defaultRank;

        ranks.putIfAbsent(uuid, this);
    }

    public static void init() {
        collection = Core.getInstance().getMongoHandler().getMongoDatabase().getCollection("ranks");

        Map<Rank, List<UUID>> inheritanceReferences = new HashMap<>();

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                Rank rank = new Rank(UUID.fromString(document.getString("uuid")),
                        document.getString("displayName"));
                rank.load(document);

                Core.getInstance().getMongoHandler().getGSON().<List<String>>fromJson(document.getString("permissions"), Core.getInstance().getMongoHandler().getLIST_STRING_TYPE())
                        .forEach(perm -> rank.getPermissions().add(perm));

                List<UUID> ranksToInherit = new ArrayList<>();

                for (JsonElement element : new JsonParser().parse(document.getString("inherits")).getAsJsonArray()) {
                    ranksToInherit.add(UUID.fromString(element.getAsString()));
                }

                inheritanceReferences.put(rank, ranksToInherit);

                ranks.putIfAbsent(rank.getUuid(), rank);
            }
        }

        for (Rank rank : ranks.values()) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&6[Core] &fLoaded the " + rank.getColor() + rank.getDisplayName() + " &fRank"));
        }

        inheritanceReferences.forEach((rank, list) -> {
            list.forEach(uuid -> {
                Rank inherited = ranks.get(uuid);

                if (inherited != null) {
                    rank.getInherited().add(inherited);
                }
            });
        });

        getDefaultRank();
    }

    /**
     * Retrieves a rank by UUID if one exists.
     *
     * @param uuid The UUID.
     * @return A rank that matches the given UUID if found.
     */
    public static Rank getRankByUuid(UUID uuid) {
        return ranks.get(uuid);
    }

    /**
     * Retrieves a rank by name if one exists.
     *
     * @param name The name.
     * @return A rank that matches the given name if found.
     */
    public static Rank getRankByDisplayName(String name) {
        for (Rank rank : ranks.values()) {
            if (rank.getDisplayName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    /**
     * Retrieves the default rank or creates a new default rank if one does not already exist.
     *
     * @return A default rank, or a new default rank if unavailable.
     */
    public static Rank getDefaultRank() {
        for (Rank rank : ranks.values()) {
            if (rank.isDefaultRank()) {
                return rank;
            }
        }

        Rank defaultRank = new Rank("Default");
        defaultRank.setDefaultRank(true);
        defaultRank.save();

        ranks.put(defaultRank.getUuid(), defaultRank);

        return defaultRank;
    }

    public static Rank getNameMCRank() {
        return getRankByDisplayName("NameMC");
    }

    public boolean isDefaultRank() {
        return defaultRank;
    }

    public boolean addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            return true;
        }

        return false;
    }

    public boolean deletePermission(String permission) {
        return permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        if (permissions.contains(permission)) {
            return true;
        }

        for (Rank rank : inherited) {
            if (rank.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    public boolean canInherit(Rank rankToCheck) {
        if (inherited.contains(rankToCheck) || rankToCheck.inherited.contains(this)) {
            return false;
        }

        for (Rank rank : inherited) {
            if (!rank.canInherit(rankToCheck)) {
                return false;
            }
        }

        return true;
    }

    public List<String> getAllPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.addAll(this.permissions);

        for (Rank rank : inherited) {
            permissions.addAll(rank.getAllPermissions());
        }

        return permissions;
    }

    public void load() {
        load(collection.find(Filters.eq("uuid", uuid.toString())).first());
    }

    public void load(Document document) {
        if (document == null) {
            return;
        }

        prefix = ChatColor.translateAlternateColorCodes('&', document.getString("prefix"));
        suffix = ChatColor.translateAlternateColorCodes('&', document.getString("suffix"));
        color = ChatColor.valueOf(document.getString("color"));
        weight = document.getInteger("weight");
        defaultRank = document.getBoolean("defaultRank");
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("displayName", displayName);
        document.put("prefix", prefix.replace(String.valueOf(ChatColor.COLOR_CHAR), "&"));
        document.put("suffix", suffix.replace(String.valueOf(ChatColor.COLOR_CHAR), "&"));
        document.put("color", color.name());
        document.put("weight", weight);
        document.put("defaultRank", defaultRank);
        document.put("permissions", Core.getInstance().getMongoHandler().getGSON().toJson(permissions));
        document.put("inherits", Core.getInstance().getMongoHandler().getGSON().toJson(inherited.stream().map(Rank::getUuid).map(UUID::toString).collect(Collectors.toList())));

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

    public void delete() {
        ranks.remove(uuid);

        for (Profile p : Profile.getProfiles().values()) {
            if (p.getActiveRank().getUuid() == uuid) {
                p.activateNextGrant();
            }
        }

        collection.deleteOne(Filters.eq("uuid", uuid.toString()));
    }

    public String formattedName() {
        return getColor() + getDisplayName();
    }

}
