package com.github.dragonhht.rpc.consumer;

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
                        }
                    });
            ChannelFuture future = client.connect(host, port).sync();
            future.channel().write(request).sync();
            // TODO 等待响应结果
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
