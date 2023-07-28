package dev.razorni.hcfactions.utils.storage;

import dev.razorni.hcfactions.teams.Team;
import dev.razorni.hcfactions.users.User;

public interface Storage {
    void saveTeam(Team team, boolean async);

    void close();

    void loadTeams();

    void loadTimers();

    void saveTimers();

    void saveUsers();

    void load();

    void saveTeams();

    void saveUser(User user, boolean async);

    void deleteTeam(Team team);

    void loadUsers();
}