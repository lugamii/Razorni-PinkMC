package dev.razorni.core.util;

import dev.razorni.core.Core;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.text.MessageFormat;

@AllArgsConstructor
public enum Locale {

    FAILED_TO_LOAD_PROFILE("COMMON_ERRORS.FAILED_TO_LOAD_PROFILE"),
    COULD_NOT_RESOLVE_PLAYER("COMMON_ERRORS.COULD_NOT_RESOLVE_PLAYER"),
    PLAYER_NOT_FOUND("COMMON_ERRORS.PLAYER_NOT_FOUND"),
    RANK_NOT_FOUND("COMMON_ERRORS.RANK_NOT_FOUND");

    @Getter private String path;

    public String format(Object... objects) {
        return new MessageFormat(ChatColor.translateAlternateColorCodes('&',
                Core.getInstance().getConfig().getString(path))).format(objects);
    }
}
