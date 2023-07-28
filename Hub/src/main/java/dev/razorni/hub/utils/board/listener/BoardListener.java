package dev.razorni.hub.utils.board.listener;

import dev.razorni.hub.framework.Module;
import dev.razorni.hub.utils.board.Board;
import dev.razorni.hub.utils.board.BoardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BoardListener extends Module<BoardManager> {
    public BoardListener(BoardManager manager) {
        super(manager);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.getManager().getBoards().remove(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.getManager().getBoards().put(player.getUniqueId(), new Board(this.getManager(), player));
    }
}
