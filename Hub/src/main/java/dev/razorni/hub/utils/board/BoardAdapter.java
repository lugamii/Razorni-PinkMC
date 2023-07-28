package dev.razorni.hub.utils.board;

import org.bukkit.entity.Player;

import java.util.List;

public interface BoardAdapter {
    List<String> getLines(Player p0);

    String getTitle(Player p0);
}