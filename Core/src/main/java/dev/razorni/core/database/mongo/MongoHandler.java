package dev.razorni.core.database.mongo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.razorni.core.Core;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import dev.razorni.core.extras.report.Report;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/08/2021 / 3:00 AM
 * Core / rip.orbit.gravity.database.mongo
 */

@Getter
public class MongoHandler {

	private MongoDatabase mongoDatabase;

	private final Gson GSON = new Gson();

	private final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();
	private final Type LIST_UUID_TYPE = new TypeToken<List<UUID>>() {}.getType();
	private final Type MAP_STRING_LONG = new TypeToken<Map<String, Long>>() {}.getType();
	private final Type REPORT = new TypeToken<Report>() {}.getType();

	public MongoHandler() {

		loadMongo();

	}

	private void loadMongo() {
		FileConfiguration mainConfig = Core.getInstance().getConfig();
		if (mainConfig.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
			mongoDatabase = new MongoClient(new ServerAddress(mainConfig.getString("MONGO.HOST"), mainConfig.getInt("MONGO.PORT")), MongoCredential.createCredential(
					mainConfig.getString("MONGO.AUTHENTICATION.USERNAME"),
					mainConfig.getString("MONGO.DATABASE"), mainConfig.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()), MongoClientOptions.builder().build()
			).getDatabase(mainConfig.getString("MONGO.DATABASE"));
		} else {
			mongoDatabase = new MongoClient(mainConfig.getString("MONGO.HOST"), mainConfig.getInt("MONGO.PORT"))
					.getDatabase(mainConfig.getString("MONGO.DATABASE"));
		}
	}

}
