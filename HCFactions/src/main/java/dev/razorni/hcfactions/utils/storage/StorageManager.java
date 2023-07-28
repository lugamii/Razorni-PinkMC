package dev.razorni.hcfactions.utils.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.storage.json.JsonStorage;
import dev.razorni.hcfactions.utils.storage.mongo.MongoStorage;
import lombok.Getter;

@Getter
public class StorageManager extends Manager {
    private final Storage storage;
    private final StorageType storageType;

    public StorageManager(HCF plugin) {
        super(plugin);
        this.storageType = StorageType.valueOf(this.getConfig().getString("STORAGE_TYPE"));
        this.storage = this.load();
    }

    @Override
    public void disable() {
        this.storage.close();
    }

    @Override
    public void enable() {
        this.storage.load();
    }

    public Storage load() {
        if (this.storageType == StorageType.MONGO) {
            String uri = "mongodb://" + (this.getConfig().getBoolean("MONGO.AUTH.ENABLED") ? this.getConfig().getString("MONGO.AUTH.USERNAME") + ":" + this.getConfig().getString("MONGO.AUTH.PASSWORD") + "@" : "") + this.getConfig().getString("MONGO.SERVER_IP");
            MongoClient client = MongoClients.create(uri);
            return new MongoStorage(this, client, client.getDatabase(this.getConfig().getString("MONGO.DATABASE")));
        }
        return new JsonStorage(this);
    }

}