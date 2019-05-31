package com.github.dragonhht.rpc.registry.handler;

import com.github.dragonhht.rpc.model.ConsumerRequest;
import com.github.dragonhht.rpc.model.ConsumerResponse;
import com.github.dragonhht.rpc.model.ProviderRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.*;
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
        // 判断是否为服务消费者的信息
        if (msg instanceof ConsumerRequest) {
            ConsumerResponse response = getServiceMsg((ConsumerRequest) msg);
            ctx.writeAndFlush(response);
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

    /**
     * 获取服务信息
     * @param request
     */
    private ConsumerResponse getServiceMsg(ConsumerRequest request) {
        ConsumerResponse response = new ConsumerResponse();
        response.setInterfaceName(request.getInterfaceName());
        Random random = new Random();
        Set<String> hosts = services.get(request.getInterfaceName());
        if (hosts != null && hosts.size() > 0) {
            List<String> providers = new ArrayList<>(hosts.size());
            providers.addAll(hosts);
            String host = providers.get(random.nextInt(hosts.size()));
            response.setHost(host);
        }
        return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
