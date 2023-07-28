package eu.vortexdev.invictusspigot.util;

import io.netty.buffer.ByteBuf;

public class ProtocolUtil {

    public static int e(final ByteBuf byteBuf) {
        int i = 0;
        int j = 0;
        byte b0;
        do {
            b0 = byteBuf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);
        return i;
    }

    public static void b(int i, final ByteBuf bytebuf1) {
        while ((i & -128) != 0) {
            bytebuf1.writeByte(i & Byte.MAX_VALUE | 128);
            i >>>= 7;
        }

        bytebuf1.writeByte(i);
    }

}
