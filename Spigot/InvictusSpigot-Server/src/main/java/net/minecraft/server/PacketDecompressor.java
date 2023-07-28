package net.minecraft.server;

import eu.vortexdev.invictusspigot.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.zip.Inflater;

public class PacketDecompressor extends ByteToMessageDecoder {

    private static final ReusableByteArray reusableCompressedData = new ReusableByteArray(8192);
    private final Inflater a = new Inflater();
    private int b;

    public PacketDecompressor(int compressionThreshold) {
        this.b = compressionThreshold;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.readableBytes() != 0) {
            int uncompressedSize = ProtocolUtil.e(bytebuf);
            if (uncompressedSize == 0) {
                list.add(bytebuf.readBytes(bytebuf.readableBytes()));
            } else {
                if (uncompressedSize < b) {
                    throw new DecoderException("Badly compressed packet - size of " + uncompressedSize + " is below server threshold of " + b);
                } else if (uncompressedSize > 2097152) {
                    throw new DecoderException("Badly compressed packet - size of " + uncompressedSize + " is larger than protocol maximum of " + 2097152);
                }
                int compressedSize = bytebuf.readableBytes();
                byte[] compressedData = reusableCompressedData.get(compressedSize);
                bytebuf.readBytes(compressedData, 0, compressedSize);
                a.setInput(compressedData, 0, compressedSize);
                byte[] data = new byte[uncompressedSize];
                a.inflate(data);
                list.add(Unpooled.wrappedBuffer(data));
                a.reset();
            }

        }
    }

    public void a(int compressorThreshold) {
        b = compressorThreshold;
    }
}
