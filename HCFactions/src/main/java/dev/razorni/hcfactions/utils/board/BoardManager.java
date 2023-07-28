package dev.razorni.hcfactions.utils.board;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.providers.rBoard;
import dev.razorni.hcfactions.utils.board.listener.BoardListener;
import dev.razorni.hcfactions.utils.board.thread.BoardThread;
import dev.razorni.hcfactions.extras.framework.Manager;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BoardManager extends Manager {
    private final long titleChangerTicks;
    private final BoardAdapter adapter;
    private final List<String> titleChanges;
    private final Map<UUID, Board> boards;

    public BoardManager(HCF plugin) {
        super(plugin);
        this.boards = new ConcurrentHashMap<>();
        this.adapter = new rBoard(this);
        this.titleChangerTicks = this.getScoreboardConfig().getLong("TITLE_CONFIG.CHANGER_TICKS");
        this.titleChanges = this.getScoreboardConfig().getStringList("TITLE_CONFIG.CHANGES");
        new BoardListener(this);
        new BoardThread(this);
    }
}
