package dev.razorni.hcfactions.extras.nametags;

import org.bukkit.entity.Player;

public interface NametagAdapter {
    String getAndUpdate(Player from, Player to);
}