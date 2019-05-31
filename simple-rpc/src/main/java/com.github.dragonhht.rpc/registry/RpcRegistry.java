package com.github.dragonhht.rpc.registry;

import com.github.dragonhht.rpc.common.CommonConstants;
import com.github.dragonhht.rpc.registry.handler.RpcRegistryHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心.
 *
 * @author: huang
 * @Date: 2019-5-28
 */
@Slf4j
public class RpcRegistry {


    /** 注册中心端口号. */
    private int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    /**
     * 启动注册中心服务
     */
    public void start() {
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            server.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // 使用Java自带序列化
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            // 主要业务
                            pipeline.addLast(new RpcRegistryHandler());

                        }
                    });

            // 启动
            ChannelFuture future = server.bind(this.port).sync();
            log.info("注册中心在端口 {} 成功启动", this.port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("注册中心服务异常: ", e);
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        new RpcRegistry(CommonConstants.REGISTRY_PORT).start();
    }

}
