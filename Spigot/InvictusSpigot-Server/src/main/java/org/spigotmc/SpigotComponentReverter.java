package org.spigotmc;

import net.minecraft.server.ChatModifier;
import net.minecraft.server.EnumChatFormat;
import net.minecraft.server.IChatBaseComponent;
import org.bukkit.ChatColor;

public final class SpigotComponentReverter {
    public static String toLegacy(IChatBaseComponent s) {
        StringBuilder builder = new StringBuilder();
        legacy(builder, s);
        return builder.toString();
    }

    private static void legacy(StringBuilder builder, IChatBaseComponent s) {
        ChatModifier modifier = s.getChatModifier();
        colorize(builder, modifier);
        if (s instanceof net.minecraft.server.ChatComponentText) {
            builder.append(s.getText());
        } else {
            throw new RuntimeException("Unhandled type: " + s.getClass().getSimpleName());
        }
        for (IChatBaseComponent c : s.a())
            legacy(builder, c);
    }

    private static void colorize(StringBuilder builder, ChatModifier modifier) {
        if (modifier == null)
            return;
        EnumChatFormat color = modifier.getColor();
        if (color == null)
            color = EnumChatFormat.BLACK;
        builder.append(color);
        if (modifier.isBold())
            builder.append(ChatColor.BOLD);
        if (modifier.isItalic())
            builder.append(ChatColor.ITALIC);
        if (modifier.isRandom())
            builder.append(ChatColor.MAGIC);
        if (modifier.isStrikethrough())
            builder.append(ChatColor.STRIKETHROUGH);
        if (modifier.isUnderlined())
            builder.append(ChatColor.UNDERLINE);
    }
}
