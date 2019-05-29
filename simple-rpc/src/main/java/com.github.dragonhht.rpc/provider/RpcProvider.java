package com.github.dragonhht.rpc.provider;

import com.github.dragonhht.rpc.provider.handler.RpcProviderHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-28
 */
@Slf4j
public class RpcProvider {

    /**
     * 连接注册中心.
     * @param host 注册中心域名
     * @param port 注册中心端口号
     */
    public void connect(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap provider = new Bootstrap();

        try {
            provider.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // 使用Java自带序列化
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            // 发送服务信息至注册中心
                            pipeline.addLast(new RpcProviderHandler());
                        }
                    });
            ChannelFuture future = provider.connect(host, port).sync();
            log.info("服务提供者启动");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("连接注册中心失败", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            new RpcProvider().connect("127.0.0.1", 8888);
        }).start();
    }

}
