package hht.dragon.protocol;

import hht.dragon.protocol.codec.MessageDecoder;
import hht.dragon.protocol.codec.MessageEncoder;
import hht.dragon.protocol.handler.HeartBeatRespHandler;
import hht.dragon.protocol.handler.LoginAuthRespHandler;
import hht.dragon.protocol.param.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-22
 */
@Slf4j
public class Server {

    public void bind() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        try {
            server.group(group, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 10)
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageDecoder(1024 * 1024,
                                    4, 4));
                            socketChannel.pipeline().addLast(new MessageEncoder());
                            socketChannel.pipeline().addLast("read-time-handler", new ReadTimeoutHandler(5));
                            socketChannel.pipeline().addLast("login-auth-handler", new LoginAuthRespHandler());
                            socketChannel.pipeline().addLast("heart-beat-handler", new HeartBeatRespHandler());
                        }
                    });


            ChannelFuture future = server.bind(Constants.SERVER_PORT).sync();
            log.info("服务启动成功...");
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().bind();
    }

}
