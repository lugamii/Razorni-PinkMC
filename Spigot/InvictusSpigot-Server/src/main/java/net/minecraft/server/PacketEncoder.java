package net.minecraft.server;

import eu.vortexdev.invictusspigot.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

//[Vortex] - removed less object creation
public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {

    private final EnumProtocolDirection c;

    public PacketEncoder(EnumProtocolDirection enumprotocoldirection) {
        this.c = enumprotocoldirection;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
        Integer integer = channelHandlerContext.channel().attr(NetworkManager.c).get().a(this.c, packet);
        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            ProtocolUtil.b(integer, byteBuf);
            try {
                packet.b(new PacketDataSerializer(byteBuf));
            } catch (Throwable ignored) {
            }

        }
    }
}
