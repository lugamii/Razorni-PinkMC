package org.spigotmc;

import net.minecraft.server.MinecraftServer;

public class AsyncCatcher
{

    public static boolean enabled = true;

    public static void catchOp(String reason) {
        if (enabled && (MinecraftServer.getServer().primaryThread != null && Thread.currentThread() != MinecraftServer.getServer().primaryThread)) {
            throw new IllegalStateException("Asynchronous " + reason + "!");
        }
    }
}
