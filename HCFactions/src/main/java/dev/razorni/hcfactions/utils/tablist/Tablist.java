package dev.razorni.hcfactions.utils.tablist;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.utils.tablist.extra.TablistEntry;
import dev.razorni.hcfactions.utils.tablist.packet.TablistPacket;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class Tablist extends Module<TablistManager> {
    private static final TablistEntry EMPTY_ENTRY;

    static {
        EMPTY_ENTRY = new TablistEntry("", -1);
    }

    private final Table<Integer, Integer, TablistEntry> entries;
    private final TablistPacket packet;

    public Tablist(TablistManager manager, Player player) {
        super(manager);
        this.entries = HashBasedTable.create();
        this.packet = manager.createPacket(player);
    }

    public void update() {
        this.entries.clear();
        this.packet.update();
    }

    public TablistEntry getEntries(int x, int y) {
        TablistEntry entry = this.entries.get(x, y);
        if (entry == null) {
            this.entries.put(x, y, Tablist.EMPTY_ENTRY);
            return Tablist.EMPTY_ENTRY;
        }
        return entry;
    }

    public void add(int x, int y, String name) {
        this.entries.put(x, y, new TablistEntry(name, -1));
    }

    public TablistPacket getPacket() {
        return this.packet;
    }

    public void add(int x, int y, String name, int ping) {
        this.entries.put(x, y, new TablistEntry(name, ping));
    }
}
