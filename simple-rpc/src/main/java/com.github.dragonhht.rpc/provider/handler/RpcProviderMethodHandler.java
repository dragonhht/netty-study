package com.github.dragonhht.rpc.provider.handler;

import com.github.dragonhht.rpc.model.RpcRequest;
import com.github.dragonhht.rpc.model.RpcResponse;
import com.github.dragonhht.rpc.utils.ReflectionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端调用服务的处理类.
 *
 * @author: huang
 * @Date: 2019-5-31
 */
@Slf4j
public class RpcProviderMethodHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcResponse response = new RpcResponse();
            RpcRequest request = (RpcRequest) msg;
            String interfaceName = request.getClassName();
            Object targetInstance = RpcProviderHandler.services.get(interfaceName);
            if (targetInstance != null) {
                try {
                    Object result = ReflectionUtil.INSTANCE.invokeMethod(targetInstance, request.getMethodName(),
                            request.getParamsType(), request.getParams());
                    response.setResult(result);
                } catch (Exception e) {
                    response.setError(e);
                }
            } else {
                response.setError(new Exception("服务的接口未实例化..."));
            }
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("远程调用方法异常, ", cause);
    }
}
