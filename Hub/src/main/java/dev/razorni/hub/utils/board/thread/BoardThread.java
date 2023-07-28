package dev.razorni.hub.utils.board.thread;

import dev.razorni.hub.utils.board.Board;
import dev.razorni.hub.utils.board.BoardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BoardThread extends Thread {
    private final BoardManager manager;

    public BoardThread(BoardManager manager) {
        super("Azurite - BoardThread");
        this.manager = manager;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Board board = this.manager.getBoards().get(player.getUniqueId());
                if (board == null) {
                    continue;
                }
                try {
                    board.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
