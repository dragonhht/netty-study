package com.github.dragonhht.rpc.consumer.handler;

import com.github.dragonhht.rpc.model.RpcRequest;
import com.github.dragonhht.rpc.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-31
 */
@Slf4j
public class MethodCallHandler extends ChannelInboundHandlerAdapter {

    private RpcResponse response;
    private CountDownLatch latch;

    public MethodCallHandler(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 接收调用结果
        if (msg instanceof RpcResponse) {
            response = (RpcResponse) msg;
        }
        latch.countDown();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 关闭链接
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("调用远程方法错误", cause);
    }

    /**
     * 获取远程方法调用的结果.
     * @return
     */
    public RpcResponse getResult() {
        return response;
    }
}
