package com.github.dragonhht.rpc.registry.handler;

import com.github.dragonhht.rpc.model.ProviderRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-28
 */
public class RpcRegistryHandler extends ChannelInboundHandlerAdapter {

    /** 用于保存各服务信息. */
    public static Map<String, Set<String>> services = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断是否为服务提供者发送的服务信息
        if (msg instanceof ProviderRequest) {
            saveProviderService((ProviderRequest) msg);
        }
    }

    /**
     * 保存服务提供者发布的服务信息.
     * @param request
     */
    private void saveProviderService(ProviderRequest request) {
        for (String interfaceName : request.getInterfaceNames()) {
            Set<String> hosts = services.get(interfaceName);
            if (hosts != null) {
                hosts.add(request.getHost());
            } else {
                hosts = new HashSet<>();
                hosts.add(request.getHost());
            }
            services.put(interfaceName, hosts);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
