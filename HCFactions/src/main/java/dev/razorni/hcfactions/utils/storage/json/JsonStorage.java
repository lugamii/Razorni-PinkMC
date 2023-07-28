package dev.razorni.hcfactions.utils.storage.json;

import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.utils.storage.Storage;
import dev.razorni.hcfactions.utils.storage.StorageManager;
import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.teams.claims.Claim;
import dev.razorni.hcfactions.teams.enums.TeamType;
import dev.razorni.hcfactions.teams.type.*;
import dev.razorni.hcfactions.timers.listeners.servertimers.SOTWTimer;
import dev.razorni.hcfactions.timers.type.CustomTimer;
import dev.razorni.hcfactions.timers.type.PlayerTimer;
import dev.razorni.hcfactions.users.User;
import dev.razorni.hcfactions.utils.Tasks;
import dev.razorni.hcfactions.utils.configs.ConfigJson;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class JsonStorage extends Module<StorageManager> implements Storage {
    private final ConfigJson teamsJson;
    private final ConfigJson usersJson;
    private final ConfigJson timersJson;

    public JsonStorage(StorageManager manager) {
        super(manager);
        this.teamsJson = new ConfigJson(this.getInstance(), "data" + File.separator + "teams.json");
        this.usersJson = new ConfigJson(this.getInstance(), "data" + File.separator + "users.json");
        this.timersJson = new ConfigJson(this.getInstance(), "data" + File.separator + "timers.json");
    }

    @Override
    public void close() {
        this.saveTeams();
        this.saveUsers();
        this.saveTimers();
    }

    @Override
    public void loadTimers() {
        for (PlayerTimer timer : this.getInstance().getTimerManager().getPlayerTimers().values()) {
            Map<String, Object> normal = (Map<String, Object>) this.timersJson.getValues().get((timer.isPausable() ? "Normal:" : "") + timer.getName());
            Map<String, Object> paused = (Map<String, Object>) this.timersJson.getValues().get("Paused:" + timer.getName());
            if (normal != null) {
                timer.getTimerCache().putAll(normal.entrySet().stream().collect(Collectors.toMap(s -> UUID.fromString(s.getKey()), s -> Long.parseLong((String) s.getValue()))));
            }
            if (paused != null) {
                timer.getPausedCache().putAll(paused.entrySet().stream().collect(Collectors.toMap(s -> UUID.fromString(s.getKey()), s -> Long.parseLong((String) s.getValue()))));
            }
        }
        List<String> sotw = (List<String>) this.timersJson.getValues().get("SOTW_ENABLED:");
        if (sotw != null) {
            this.getInstance().getTimerManager().getSotwTimer().getEnabled().addAll(sotw.stream().map(UUID::fromString).collect(Collectors.toList()));
        }
        if (this.timersJson.getValues().containsKey("SOTW:")) {
            long sotwTime = Long.parseLong((String) this.timersJson.getValues().get("SOTW:"));
            if (sotwTime > 0L) {
                SOTWTimer timer = this.getInstance().getTimerManager().getSotwTimer();
                timer.setActive(true);
                timer.setRemaining(sotwTime);
            }
        }
        for (String s : this.timersJson.getValues().keySet()) {
            if (!s.contains("CTimer:")) {
                continue;
            }
            String value = (String) this.timersJson.getValues().get(s);
            String[] values = value.split(":");
            String name = s.split(":")[1];
            CustomTimer timer = new CustomTimer(this.getInstance().getTimerManager(), name, values[1], 0L);
            timer.setRemaining(Long.parseLong(values[0]));
        }
    }

    @Override
    public void saveTeam(Team team, boolean async) {
        if (async) {
            Tasks.executeAsync(this.getManager(), () -> this.saveTeam(team, false));
            return;
        }
        this.teamsJson.getValues().put(team.getUniqueID().toString(), team.serialize());
        this.teamsJson.save();
    }

    @Override
    public void saveUsers() {
        for (User user : this.getInstance().getUserManager().getUsers().values()) {
            this.usersJson.getValues().put(user.getUniqueID().toString(), user.serialize());
        }
        this.usersJson.save();
    }

    @Override
    public void saveTimers() {
        Map<String, Object> map = this.timersJson.getValues();
        map.clear();
        for (PlayerTimer timer : this.getInstance().getTimerManager().getPlayerTimers().values()) {
            map.put((timer.isPausable() ? "Normal:" : "") + timer.getName(), timer.getTimerCache().entrySet().stream().collect(Collectors.toMap(s -> s.getKey().toString(), s -> s.getValue().toString())));
            if (timer.isPausable()) {
                map.put("Paused:" + timer.getName(), timer.getPausedCache().entrySet().stream().collect(Collectors.toMap(s -> s.getKey().toString(), s -> s.getValue().toString())));
            }
        }
        map.put("SOTW:", String.valueOf(String.valueOf(this.getInstance().getTimerManager().getSotwTimer().getRemaining())));
        map.put("SOTW_ENABLED:", this.getInstance().getTimerManager().getSotwTimer().getEnabled().stream().map(UUID::toString).collect(Collectors.toList()));
        for (CustomTimer timer : this.getInstance().getTimerManager().getCustomTimers().values()) {
            map.put("CTimer:" + timer.getName(), timer.getRemaining().toString() + ":" + timer.getDisplayName());
        }
        this.timersJson.save();
    }

    @Override
    public void loadUsers() {
        Map<String, Object> map = this.usersJson.getValues();
        for (String s : map.keySet()) {
            Map<String, Object> fMap = (Map<String, Object>) map.get(s);
            new User(this.getInstance().getUserManager(), fMap);
        }
    }

    @Override
    public void saveUser(User user, boolean async) {
        if (async) {
            Tasks.executeAsync(this.getManager(), () -> this.saveUser(user, false));
            return;
        }
        Map<String, Object> map = user.serialize();
        this.usersJson.getValues().put(user.getUniqueID().toString(), map);
        this.usersJson.save();
    }

    @Override
    public void load() {
        this.loadTeams();
        this.loadUsers();
        this.loadTimers();
    }

    @Override
    public void saveTeams() {
        for (Team team : this.getInstance().getTeamManager().getTeams().values()) {
            this.teamsJson.getValues().put(team.getUniqueID().toString(), team.serialize());
        }
        this.teamsJson.save();
    }

    @Override
    public void deleteTeam(Team team) {
        this.teamsJson.getValues().remove(team.getUniqueID().toString());
        this.teamsJson.save();
    }

    @Override
    public void loadTeams() {
        Map<String, Object> teams = this.teamsJson.getValues();
        for (String name : teams.keySet()) {
            Map<String, Object> map = (Map<String, Object>) teams.get(name);
            Team team = null;
            switch (TeamType.valueOf((String) map.get("teamType"))) {
                case PLAYER: {
                    team = new PlayerTeam(this.getInstance().getTeamManager(), map);
                    break;
                }
                case SAFEZONE: {
                    team = new SafezoneTeam(this.getInstance().getTeamManager(), map);
                    break;
                }
                case ROAD: {
                    team = new RoadTeam(this.getInstance().getTeamManager(), map);
                    break;
                }
                case MOUNTAIN: {
                    team = new GlowstoneMountainTeam(this.getInstance().getTeamManager(), map);
                    break;
                }
                case EVENT: {
                    team = new EventTeam(this.getInstance().getTeamManager(), map);
                    break;
                }
                case CITADEL: {
                    team = new CitadelTeam(this.getInstance().getTeamManager(), map);
                    break;
                }
            }
            if (team == null) {
                this.getInstance().getLogger().log(Level.SEVERE, "[Azurite] Error occurred while loading a team! Report with teams.json immediately. - " + name);
            } else {
                this.getInstance().getTeamManager().getTeams().put(team.getUniqueID(), team);
                this.getInstance().getTeamManager().getStringTeams().put(team.getName(), team);
                for (Claim claim : team.getClaims()) {
                    this.getInstance().getTeamManager().getClaimManager().saveClaim(claim);
                }
            }
        }
    }
}
