package eu.vortexdev.invictusspigot.util;

import java.nio.Buffer;

public class NioUtil {

    public static void clear(Buffer buffer) {
        buffer.clear();
    }

    public static void flip(Buffer buffer) {
        buffer.flip();
    }

}
