package com.github.dragonhht.rpc.provider;

import com.github.dragonhht.rpc.common.CommonConstants;
import com.github.dragonhht.rpc.provider.handler.RpcProviderHandler;
import com.github.dragonhht.rpc.provider.handler.RpcProviderMethodHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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

    /**
     * 提供服务.
     * @param port
     */
    public void provider(int port) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap provider = new ServerBootstrap();

        try {
            provider.group(parentGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // 使用Java自带序列化
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            // 处理消费端的调用
                            pipeline.addLast(new RpcProviderMethodHandler());
                        }
                    });
            ChannelFuture future = provider.bind(port).sync();
            log.info("消费提供者对外服务启动成功，端口 {}", port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("消费提供者对外服务启动失败", e);
        }
    }

    public static void main(String[] args) {
        RpcProvider provider = new RpcProvider();
        // 连接注册中心
        new Thread(() -> {
            provider.connect(CommonConstants.HOST, CommonConstants.REGISTRY_PORT);
        }).start();
        // 对外服务
        new Thread(() -> {
            provider.provider(CommonConstants.PROVIDER_PORT);
        }).start();
    }

}
