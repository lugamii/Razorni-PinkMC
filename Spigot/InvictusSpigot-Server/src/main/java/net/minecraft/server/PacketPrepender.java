package net.minecraft.server;

import eu.vortexdev.invictusspigot.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

@ChannelHandler.Sharable
public class PacketPrepender extends MessageToMessageEncoder<ByteBuf> {
    public static final PacketPrepender INSTANCE = new PacketPrepender();

    public PacketPrepender() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ByteBuf lengthBuf = ctx.alloc().buffer(5);
        ProtocolUtil.b(in.readableBytes(), lengthBuf);
        out.add(lengthBuf);
        out.add(in.retain());
    }
}
