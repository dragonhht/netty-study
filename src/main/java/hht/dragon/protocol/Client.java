package hht.dragon.protocol;

import hht.dragon.protocol.codec.MessageDecoder;
import hht.dragon.protocol.codec.MessageEncoder;
import hht.dragon.protocol.handler.HeartBeatReqHandler;
import hht.dragon.protocol.handler.LoginAuthReqHandler;
import hht.dragon.protocol.param.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 客户端.
 *
 * @author: huang
 * @Date: 2019-5-22
 */
@Slf4j
public class Client {

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("private-protocol-client-%d").daemon(true).build());

    EventLoopGroup group = new NioEventLoopGroup();

    /**
     * 连接
     * @param host
     * @param port
     */
    public void connect(String host, int port) throws Exception {
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageDecoder(1024 * 1024,
                                    4, 4));
                            socketChannel.pipeline().addLast("message-encoder", new MessageEncoder());
                            socketChannel.pipeline().addLast("read-time-handler", new ReadTimeoutHandler(5));
                            socketChannel.pipeline().addLast("login-auth-handler", new LoginAuthReqHandler());
                            socketChannel.pipeline().addLast("heart-beat-handler", new HeartBeatReqHandler());
                        }
                    });

            // 异步连接操作
            ChannelFuture future = client.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    try {
                        // 发起重新连接
                        log.info("重新连接");
                        connect(Constants.SERVER_HOST, Constants.SERVER_PORT);
                    } catch (Exception e) {
                        log.error("重新连接失败: {}", e);
                    }
                } catch (InterruptedException e) {
                    log.error("等待连接失败: {}", e);
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new Client().connect(Constants.SERVER_HOST, Constants.SERVER_PORT);
    }

}
