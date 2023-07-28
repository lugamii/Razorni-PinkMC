package dev.razorni.core.extras.namemc.handlers;

import dev.razorni.core.Core;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VerificationHandler {

    private final Set<UUID> likedUsers;
    private final Core core;

    private final Executor newThread = Executors.newFixedThreadPool(1);

    public VerificationHandler() {
        this.likedUsers = new HashSet<>();
        this.core = JavaPlugin.getPlugin(Core.class);
    }

    public void load() {
        this.core.getConfig().getStringList("NAMEMC.USERS-LIKED").forEach(s -> {
            likedUsers.add(UUID.fromString(s));
        });
    }

    public boolean isEmpty() {
        return this.likedUsers.isEmpty();
    }

    public int getSize() {
        return this.likedUsers.size();
    }

    public Set<UUID> getVerifiedUsers() {
        return this.likedUsers;
    }

    public void addUser(UUID uuid) {
       likedUsers.add(uuid);
    }

    public void removeUser(UUID uuid) {
        likedUsers.remove(uuid);
    }

    public boolean containsUser(UUID uuid) {
        return this.likedUsers.contains(uuid);
    }

    public void removeAll() {
        newThread.execute(() -> {
            likedUsers.clear();
            core.getConfig().set("NAMEMC.USERS-LIKED", null);
            core.saveConfig();
        });
    }

    public void save() {
        newThread.execute(() -> {
            List<String> strings = new ArrayList<>();
            likedUsers.forEach(uuid -> strings.add(uuid.toString()));
            core.getConfig().set("NAMEMC.USERS-LIKED", strings);
            core.saveConfig();
        });
    }

}
