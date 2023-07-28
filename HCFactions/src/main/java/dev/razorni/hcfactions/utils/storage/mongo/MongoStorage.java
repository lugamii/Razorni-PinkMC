package dev.razorni.hcfactions.utils.storage.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.utils.storage.Storage;
import dev.razorni.hcfactions.utils.storage.StorageManager;
import dev.razorni.hcfactions.utils.storage.json.JsonStorage;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.enums.TeamType;
import dev.razorni.hcfactions.teams.type.*;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.Tasks;
import org.bson.Document;

import java.util.logging.Level;

public class MongoStorage extends Module<StorageManager> implements Storage {
    private final MongoCollection<Document> teamsCollection;
    private final MongoClient mongoClient;
    private final JsonStorage timerStorage;
    private final MongoCollection<Document> usersCollection;

    public MongoStorage(StorageManager manager, MongoClient client, MongoDatabase database) {
        super(manager);
        this.mongoClient = client;
        this.teamsCollection = database.getCollection("teams");
        this.usersCollection = database.getCollection("users");
        this.timerStorage = new JsonStorage(manager);
    }

    @Override
    public void loadTeams() {
        for (Document document : this.teamsCollection.find()) {
            Team team = null;
            switch (TeamType.valueOf(document.getString("type"))) {
                case PLAYER: {
                    team = new PlayerTeam(this.getInstance().getTeamManager(), document);
                    break;
                }
                case SAFEZONE: {
                    team = new SafezoneTeam(this.getInstance().getTeamManager(), document);
                    break;
                }
                case ROAD: {
                    team = new RoadTeam(this.getInstance().getTeamManager(), document);
                    break;
                }
                case MOUNTAIN: {
                    team = new GlowstoneMountainTeam(this.getInstance().getTeamManager(), document);
                    break;
                }
                case EVENT: {
                    team = new EventTeam(this.getInstance().getTeamManager(), document);
                    break;
                }
                case CITADEL: {
                    team = new CitadelTeam(this.getInstance().getTeamManager(), document);
                    break;
                }
            }
            if (team == null) {
                this.getInstance().getLogger().log(Level.SEVERE, "[Azurite] Error occurred while loading a team! Report immediately! (MONGO)");
            } else {
                this.getInstance().getTeamManager().getTeams().put(team.getUniqueID(), team);
                this.getInstance().getTeamManager().getStringTeams().put(team.getName(), team);
                for (Claim claim : team.getClaims()) {
                    this.getInstance().getTeamManager().getClaimManager().saveClaim(claim);
                }
            }
        }
    }

    @Override
    public void saveUsers() {
        for (User user : this.getInstance().getUserManager().getUsers().values()) {
            this.saveUser(user, false);
        }
    }

    @Override
    public void load() {
        this.loadTeams();
        this.loadUsers();
        this.loadTimers();
    }

    @Override
    public void saveUser(User user, boolean async) {
        if (async) {
            Tasks.executeAsync(this.getManager(), () -> this.saveUser(user, false));
            return;
        }
        Document document = new Document("_id", user.getUniqueID().toString());
        document.putAll(user.serialize());
        this.usersCollection.replaceOne(Filters.eq("_id", user.getUniqueID().toString()), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void close() {
        this.saveTimers();
        this.saveTeams();
        this.saveUsers();
        this.mongoClient.close();
    }

    @Override
    public void loadUsers() {
        for (Document document : this.usersCollection.find()) {
            new User(this.getInstance().getUserManager(), document);
        }
    }

    @Override
    public void loadTimers() {
        this.timerStorage.loadTimers();
    }

    @Override
    public void saveTeam(Team team, boolean async) {
        if (async) {
            Tasks.executeAsync(this.getManager(), () -> this.saveTeam(team, false));
            return;
        }
        Document document = new Document("_id", team.getUniqueID().toString());
        document.putAll(team.serialize());
        this.teamsCollection.replaceOne(Filters.eq("_id", team.getUniqueID().toString()), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void deleteTeam(Team team) {
        this.teamsCollection.deleteOne(Filters.eq("_id", team.getUniqueID().toString()));
    }

    @Override
    public void saveTimers() {
        this.timerStorage.saveTimers();
    }

    @Override
    public void saveTeams() {
        for (Team team : this.getInstance().getTeamManager().getTeams().values()) {
            this.saveTeam(team, false);
        }
    }
}
