package com.github.dragonhht.rpc.consumer;

import com.github.dragonhht.rpc.consumer.handler.MethodCallHandler;
import com.github.dragonhht.rpc.consumer.handler.ProviderServiceHandler;
import com.github.dragonhht.rpc.model.ConsumerRequest;
import com.github.dragonhht.rpc.model.ConsumerResponse;
import com.github.dragonhht.rpc.model.RpcRequest;
import com.github.dragonhht.rpc.model.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.CountDownLatch;

/**
 * 服务消费者.
 *
 * @author: huang
 * @Date: 2019-5-28
 */
public class RpcConsumer {

    /**
     * 发送调用信息
     * @param request
     * @return
     */
    public RpcResponse send(RpcRequest request, String host, int port) {
        Bootstrap client = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        CountDownLatch latch = new CountDownLatch(1);
        RpcResponse response = new RpcResponse();
        MethodCallHandler methodCallHandler = new MethodCallHandler(latch);
        try {
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // 使用Java自带序列化
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            // 远程方法调用
                            pipeline.addLast(methodCallHandler);
                        }
                    });
            ChannelFuture future = client.connect(host, port).sync();
            future.channel().writeAndFlush(request);
            // 等待响应结果
            latch.await();
            future.channel().closeFuture().sync();
            return methodCallHandler.getResult();
        } catch (InterruptedException e) {
            response.setError(e);
        }
        return response;
    }

    /**
     * 从注册中心获取服务提供者信息.
     * @param host
     * @param port
     * @param interfaceName
     * @return
     */
    public ConsumerResponse getProviderMsg(String host, int port, String interfaceName) {
        Bootstrap client = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        CountDownLatch latch = new CountDownLatch(1);
        ProviderServiceHandler providerServiceHandler = new ProviderServiceHandler(latch);
        ConsumerResponse response = new ConsumerResponse();
        try {
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            // 使用Java自带序列化
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            // 远程方法调用
                            pipeline.addLast(providerServiceHandler);
                        }
                    });
            ChannelFuture future = client.connect(host, port).sync();
            future.channel().writeAndFlush(new ConsumerRequest(interfaceName));
            // 等待响应结果
            latch.await();
            future.channel().closeFuture().sync();
            return providerServiceHandler.getResponse();
        } catch (InterruptedException e) {
            response.setError(e);
        }
        return response;
    }

    /**
     * 从注册中心获取服务的消费提供者信息
     * @param interfaceName
     * @return
     */
    public static String getProviderHost(String host, int port, String interfaceName) throws Throwable {
        ConsumerResponse response = new RpcConsumer().getProviderMsg(host, port, interfaceName);
        if (response.getError() != null) {
            throw response.getError();
        }
        return response.getHost();
    }

}
