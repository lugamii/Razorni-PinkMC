package net.minecraft.server;

import com.mojang.authlib.properties.Property;
import eu.vortexdev.api.protocol.PacketListenerAdapter;
import eu.vortexdev.invictusspigot.InvictusSpigot;
import eu.vortexdev.invictusspigot.config.InvictusConfig;
import io.netty.channel.*;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.net.SocketAddress;
import java.util.UUID;

public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    public static final AttributeKey<EnumProtocol> c = AttributeKey.valueOf("protocol");

    private static final Logger g = LogManager.getLogger();
    public final EnumProtocolDirection h;
    public Channel channel;
    public SocketAddress l;
    public UUID spoofedUUID;
    public Property[] spoofedProfile;
    public boolean preparing = true;
    private PacketListener m;
    private IChatBaseComponent n;
    private boolean p;

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        h = enumprotocoldirection;
    }

    private static void write(Channel channel, boolean change, Attribute<EnumProtocol> attribute, EnumProtocol enumprotocol, Packet packet, GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        if (change) {
            attribute.set(enumprotocol);
            channel.config().setAutoRead(true);
        }
        if (agenericfuturelistener == null || agenericfuturelistener.length == 0) {
            channel.writeAndFlush(packet);
        } else {
            channel.writeAndFlush(packet).addListeners(agenericfuturelistener).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        channel = channelhandlercontext.channel();
        l = channel.remoteAddress();
        preparing = false;
        try {
            a(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void a(EnumProtocol enumprotocol) {
        channel.attr(c).set(enumprotocol);
        channel.config().setAutoRead(true);
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) {
        close(new ChatMessage("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
        ChatMessage chatmessage;
        if (throwable instanceof TimeoutException) {
            chatmessage = new ChatMessage("disconnect.timeout");
        } else {
            chatmessage = new ChatMessage("disconnect.genericReason", "Internal Exception: " + throwable);
        }
        close(chatmessage);
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) throws Exception {
        if (channel.isOpen()) {
            try {
                packet.a(m);
            } catch (CancelledPacketHandleException ignored) {
            }
            if (m instanceof PlayerConnection) {
                try {
                    for (PacketListenerAdapter adapter : InvictusSpigot.INSTANCE.getPacketListeners()) {
                        adapter.onReceive((PlayerConnection) m, packet);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void a(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener");
        m = packetlistener;
    }

    public void handle(Packet<?> packet) {
        if (channel != null && channel.isOpen()) {
            a(packet, null);
        }
    }

    public void a(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        if (channel != null && channel.isOpen()) {
            a(packet, ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener));
        }
    }

    // Vortex - Avoid checking protocol & inEventLoop
    public void a(final Packet<?> packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        EnumProtocol enumprotocol = EnumProtocol.a(packet);
        Attribute<EnumProtocol> attribute = channel.attr(c);
        boolean change = enumprotocol != attribute.get();
        if (change) {
            channel.config().setAutoRead(false);
        }

        if (channel.eventLoop().inEventLoop()) {
            write(channel, change, attribute, enumprotocol, packet, agenericfuturelistener);
        } else {
            channel.eventLoop().execute(() -> write(channel, change, attribute, enumprotocol, packet, agenericfuturelistener));
        }
    }

    public void a() {
        if (m instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox) m).c();
        }
        channel.flush();
    }

    public void close(IChatBaseComponent ichatbasecomponent) {
        preparing = false;
        if (channel.isOpen()) {
            channel.close();
            n = ichatbasecomponent;
        }
    }

    public void a(SecretKey secretkey) {
        final ChannelPipeline pp = channel.pipeline();
        pp.addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        pp.addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }

    public void a(int i) {
        final ChannelPipeline pp = channel.pipeline();
        if (i >= 0) {
            if (pp.get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) pp.get("decompress")).a(i);
            } else {
                pp.addBefore("decoder", "decompress", new PacketDecompressor(i));
            }

            if (pp.get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) pp.get("decompress")).a(i);
            } else {
                pp.addBefore("encoder", "compress", new PacketCompressor(i));
            }
        } else {
            if (pp.get("decompress") instanceof PacketDecompressor) {
                pp.remove("decompress");
            }

            if (pp.get("compress") instanceof PacketCompressor) {
                pp.remove("compress");
            }
        }
    }

    public void l() {
        if (channel != null && !channel.isOpen()) {
            if (!p) {
                p = true;
                if (n != null) {
                    m.a(n);
                } else if (m != null) {
                    m.a(new ChatComponentText("Disconnected"));
                }
            } else if (InvictusConfig.connectionLogs) {
                g.warn("handleDisconnection() called twice");
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet<?> object) throws Exception {
        a(channelhandlercontext, object);
    }

    public boolean c() {
        return channel instanceof LocalChannel || channel instanceof LocalServerChannel;
    }

    public boolean g() {
        return channel != null && channel.isOpen();
    }

    public boolean h() {
        return channel == null;
    }

    public PacketListener getPacketListener() {
        return m;
    }

    public IChatBaseComponent j() {
        return n;
    }

    public void k() {
        channel.config().setAutoRead(false);
    }

    public SocketAddress getSocketAddress() {
        return l;
    }

    public SocketAddress getRawAddress() {
        return channel.remoteAddress();
    }
}