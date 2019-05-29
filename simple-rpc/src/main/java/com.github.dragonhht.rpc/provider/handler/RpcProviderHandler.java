package com.github.dragonhht.rpc.provider.handler;

import com.github.dragonhht.rpc.model.ProviderRequest;
import com.github.dragonhht.rpc.model.ServiceModel;
import com.github.dragonhht.rpc.utils.ServiceUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-29
 */
public class RpcProviderHandler extends ChannelInboundHandlerAdapter {

    /** 用于保存服务提供者提供的服务. */
    public static Map<String, Object> services = new ConcurrentHashMap<>();

    /** 需对外服务的包. */
    private static final String PACKAGE_PATH = "com.github.dragonhht.rpc.provider";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 发送服务信息到注册中心
        ProviderRequest request = new ProviderRequest();
        request.setHost(InetAddress.getLocalHost().getHostAddress());

        Set<Class> classes = ServiceUtil.INSTANCE.getServiceClass(PACKAGE_PATH, true);
        for (Class clazz : classes) {
            ServiceModel model = ServiceUtil.INSTANCE.getServiceMsg(clazz);
            if (model != null) {
                services.put(model.getInterfaceName(), model.getInstance());
                request.getInterfaceNames().add(model.getInterfaceName());
            }
        }
        // 将提供的服务发送给注册中心
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
