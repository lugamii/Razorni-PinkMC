package dev.razorni.hub.utils.board;

import dev.razorni.hub.Hub;
import dev.razorni.hub.framework.Manager;
import dev.razorni.hub.providers.rBoard;
import dev.razorni.hub.utils.shits.CC;
import dev.razorni.hub.utils.board.listener.BoardListener;
import dev.razorni.hub.utils.board.thread.BoardThread;
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

    public BoardManager(Hub plugin) {
        super(plugin);
        this.boards = new ConcurrentHashMap<>();
        this.adapter = new rBoard(this);
        this.titleChangerTicks = Hub.getInstance().getSettingsConfig().getConfig().getLong("TITLE_CONFIG.CHANGER_TICKS");
        this.titleChanges = Hub.getInstance().getSettingsConfig().getConfig().getStringList(CC.translate("TITLE_CONFIG.CHANGES"));
        new BoardListener(this);
        new BoardThread(this);
    }
}
