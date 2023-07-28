package net.minecraft.server;

public class PlayerConnectionUtils {
    public static <T extends PacketListener> void ensureMainThread(Packet<T> paramPacket, T paramT, IAsyncTaskHandler paramIAsyncTaskHandler) throws CancelledPacketHandleException {
        if (!paramIAsyncTaskHandler.isMainThread()) {
            paramIAsyncTaskHandler.postToMainThread(() -> paramPacket.a(paramT));
            throw CancelledPacketHandleException.INSTANCE;
        }
    }
}
