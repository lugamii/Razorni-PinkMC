package net.minecraft.server;

import eu.vortexdev.invictusspigot.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.zip.Deflater;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {

    private static final ReusableByteArray reusableData = new ReusableByteArray(0x70000);
    private final byte[] a = new byte[8192];
    private final Deflater b = new Deflater();
    private int c;

    public PacketCompressor(int i) {
        this.c = i;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out) throws Exception {
        int origSize = in.readableBytes();

        if (origSize < c) {
            ProtocolUtil.b(0, out);
            out.writeBytes(in);
        } else {
            byte[] data = reusableData.get(origSize);
            in.readBytes(data, 0, origSize);

            ProtocolUtil.b(origSize, out);

            b.setInput(data, 0, origSize);
            b.finish();
            while (!b.finished()) {
                int count = b.deflate(a);
                out.writeBytes(a, 0, count);
            }
            b.reset();
        }

    }

    public void a(int compressionThreshold) {
        this.c = compressionThreshold;
    }
}
