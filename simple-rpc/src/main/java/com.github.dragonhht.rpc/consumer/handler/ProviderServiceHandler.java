package com.github.dragonhht.rpc.consumer.handler;

import com.github.dragonhht.rpc.model.ConsumerResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * 消费者从注册中心获取相应服务提供者信息
 * (本地不缓存).
 *
 * @author: huang
 * @Date: 2019-5-31
 */
@Slf4j
public class ProviderServiceHandler extends ChannelInboundHandlerAdapter {

    private ConsumerResponse response;
    private CountDownLatch latch;

    public ProviderServiceHandler(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ConsumerResponse) {
            response = (ConsumerResponse) msg;
        }
        latch.countDown();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("从注册中心获取服务信息失败", cause);
    }

    public ConsumerResponse getResponse() {
        return this.response;
    }
}
