package dev.razorni.hub.utils.tab.manager;


import dev.razorni.hub.utils.tab.versions.PlayerVersionManager;
import dev.razorni.hub.utils.tab.versions.module.PlayerVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */
public enum TabColumn {

    LEFT(-2, 1, 3),
    MIDDLE(-1, 21, 3),
    RIGHT(0, 41, 3),
    FAR_RIGHT(60, 61, 1);

    private final int startNumber;
    private final int incrementBy;
    private final int rawStart;

    @Getter
    private final List<Integer> slotList = new ArrayList<>();


    TabColumn(int rawStart, int startNumber, int incrementBy) {
        this.rawStart = rawStart;
        this.startNumber = startNumber;
        this.incrementBy = incrementBy;

        for (int i = 1; i <= 20; i++) {
            slotList.add(rawStart + (i * incrementBy));
        }
    }


    public static TabColumn getColumn(int ordinal) {
        return Arrays.stream(values()).filter(column -> column.ordinal() == ordinal).findFirst().orElse(null);
    }


    private static boolean isBetween(int input, int min, int max) {
        return input >= min && input <= max;
    }

    public static TabColumn getColumn(Player player, Integer slot) {
        if (PlayerVersionManager.getPlayerVersion(player) == PlayerVersion.v1_7) {
            return Arrays.stream(values()).filter(column -> column.getSlotList().contains(slot)).findFirst().orElse(null);
        } else {
            if (isBetween(slot, 1, 20)) return LEFT;
            if (isBetween(slot, 21, 40)) return MIDDLE;
            if (isBetween(slot, 41, 60)) return RIGHT;
            if (isBetween(slot, 61, 80)) return FAR_RIGHT;
            return null;
        }
    }



    public Integer getSlot(Player player, int raw) {
        if (PlayerVersionManager.getPlayerVersion(player) != PlayerVersion.v1_7) return raw - startNumber + 1;

        int result  = 0;

        for (int slot : slotList) {
            result++;
            if (slot == raw) return result;
        }

        return result;
    }
}

