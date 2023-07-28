package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ServerConnection {

    public static final LazyInitVar<NioEventLoopGroup> a = new LazyInitVar() {
        protected NioEventLoopGroup init() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyInitVar<EpollEventLoopGroup> b = new LazyInitVar() {
        protected EpollEventLoopGroup init() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
        }
    };
    public static final LazyInitVar<LocalEventLoopGroup> c = new LazyInitVar() {
        protected LocalEventLoopGroup init() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
        }
    };
    private static final Logger e = LogManager.getLogger();
    private static final WriteBufferWaterMark SERVER_WRITE_MARK = new WriteBufferWaterMark(1 << 20, 1 << 21);
    public static EventLoopGroup parentGroup, childGroup;

    private final MinecraftServer f;
    private final List<ChannelFuture> g = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkManager> h = Collections.synchronizedList(Lists.newArrayList());
    public volatile boolean d;

    public ServerConnection(MinecraftServer minecraftserver) {
        f = minecraftserver;
        d = true;
    }

    public void a(InetAddress inetaddress, int i) throws IOException {
        synchronized (g) {
            int workerThreadCount = Runtime.getRuntime().availableProcessors();

            Class<? extends ServerChannel> channel = null;
            if (f.ai()) {
                if (Epoll.isAvailable()) {
                    channel = EpollServerSocketChannel.class;
                    parentGroup = new EpollEventLoopGroup(0);
                    childGroup = new EpollEventLoopGroup(workerThreadCount);
                    e.info("[Connection] Using epoll channel type");
                } else if (KQueue.isAvailable()) {
                    channel = KQueueServerSocketChannel.class;
                    parentGroup = new KQueueEventLoopGroup(0);
                    childGroup = new KQueueEventLoopGroup(workerThreadCount);
                    e.info("[Connection] Using kqueue channel type");
                }
            }
            if (channel == null) {
                channel = NioServerSocketChannel.class;
                parentGroup = new NioEventLoopGroup(0);
                childGroup = new NioEventLoopGroup(workerThreadCount);
                e.info("[Connection] Using default channel type");
            }

            g.add(new ServerBootstrap().channel(channel).childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, SERVER_WRITE_MARK).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) {
                    try {
                        ChannelConfig config = channel.config();
                        config.setOption(ChannelOption.TCP_NODELAY, true);
                        config.setOption(ChannelOption.IP_TOS, 24); // Vortex
                        config.setOption(ChannelOption.TCP_FASTOPEN, 1);
                        config.setOption(ChannelOption.TCP_FASTOPEN_CONNECT, true);
                        config.setAllocator(ByteBufAllocator.DEFAULT);
                    } catch (ChannelException ignored) {
                    }

                    channel.pipeline()
                            .addLast("timeout", new ReadTimeoutHandler(30))
                            .addLast("legacy_query", new LegacyPingHandler(ServerConnection.this))
                            .addLast("splitter", new PacketSplitter())
                            .addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                            .addLast("prepender", PacketPrepender.INSTANCE)
                            .addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));

                    NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);
                    h.add(networkmanager);
                    channel.pipeline().addLast("packet_handler", networkmanager);

                    networkmanager.a(new HandshakeListener(f, networkmanager));
                }
            }).group(parentGroup, childGroup).localAddress(inetaddress, i).bind().syncUninterruptibly());
        }
    }

    public void b() {
        this.d = false;
        for (ChannelFuture future : g) {
            try {
                future.channel().close().sync();
            } catch (InterruptedException interruptedexception) {
                e.error("Interrupted whilst closing channel");
            } finally {
                parentGroup.shutdownGracefully();
                childGroup.shutdownGracefully();
            }
        }
    }

    public void c() {
        synchronized (h) {
            Iterator<NetworkManager> iterator = h.iterator();
            while (iterator.hasNext()) {
                NetworkManager networkmanager = iterator.next();

                if (!networkmanager.h()) {
                    if (!networkmanager.g()) {
                        // Spigot Start
                        // Fix a race condition where a NetworkManager could be unregistered just before connection.
                        if (networkmanager.preparing) continue;
                        // Spigot End
                        iterator.remove();
                        networkmanager.l();
                    } else {
                        try {
                            networkmanager.a();
                        } catch (Exception exception) {
                            if (networkmanager.c()) {
                                CrashReport crashreport = CrashReport.a(exception, "Ticking memory connection");
                                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");

                                crashreportsystemdetails.a("Connection", networkmanager::toString);
                                throw new ReportedException(crashreport);
                            }

                            ServerConnection.e.warn("Failed to handle packet for " + networkmanager.getSocketAddress(), exception);
                            ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                            networkmanager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), future -> networkmanager.close(chatcomponenttext));
                            networkmanager.k();
                        }
                    }
                }
            }

        }
    }

    public MinecraftServer d() {
        return this.f;
    }
}
