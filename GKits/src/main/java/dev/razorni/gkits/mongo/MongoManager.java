package dev.razorni.gkits.mongo;

import dev.razorni.gkits.GKits;
import cc.invictusgames.ilib.mongo.MongoService;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoManager extends MongoService {

    private final GKits plugin;

    public MongoManager(GKits plugin) {
        super(plugin.getGKitConfig().getMongoConfig(),
                plugin.getGKitConfig().getMongoDatabase());
        this.plugin = plugin;
    }

    public MongoCollection<Document> getGKits() {
        return getCollection("gkits");
    }

    public MongoCollection<Document> getProfiles() {
        return getCollection("profiles");
    }
}
