package net.minecraft.server;

import eu.vortexdev.invictusspigot.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private final EnumProtocolDirection c;

    public PacketDecoder(EnumProtocolDirection enumprotocoldirection) {
        this.c = enumprotocoldirection;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.readableBytes() != 0) {
            int i = ProtocolUtil.e(bytebuf);
            Packet<?> packet = channelhandlercontext.channel().attr(NetworkManager.c).get().a(this.c, i);
            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            } else {
                packet.a(new PacketDataSerializer(bytebuf));
                if (bytebuf.readableBytes() > 0) {
                    throw new IOException("Packet " + channelhandlercontext.channel().attr(NetworkManager.c).get().a() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + bytebuf.readableBytes() + " bytes extra whilst reading packet " + i);
                } else {
                    list.add(packet);
                }
            }
        }
    }
}
