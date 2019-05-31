package com.github.dragonhht.rpc.consumer;

import com.github.dragonhht.rpc.model.RpcRequest;
import com.github.dragonhht.rpc.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 使用动态代理调用远程服务.
 *
 * @author: huang
 * @Date: 2019-5-29
 */
public class ProxyHandler implements InvocationHandler {

    private String host;
    private int port;

    private int registryPort = -1;
    private String registryHost;

    public ProxyHandler(String host, int port, String registryHost, int registryPort) {
        this.host = host;
        this.port = port;
        this.registryPort = registryPort;
        this.registryHost = registryHost;
    }

    public ProxyHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static Object newInstance(String host, int port, String registryHost, int registryPort, Class target) {
        Class[] interfaces = {target};
        return Proxy.newProxyInstance(ProxyHandler.class.getClassLoader(),
                interfaces, new ProxyHandler(host, port, registryHost, registryPort));
    }

    public static Object newInstance(String host, int port, Class target) {
        Class[] interfaces = {target};
        return Proxy.newProxyInstance(ProxyHandler.class.getClassLoader(),
                interfaces, new ProxyHandler(host, port));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        setProviderHost(method.getDeclaringClass().getName());
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamsType(method.getParameterTypes());
        request.setParams(args);
        RpcResponse response = new RpcConsumer().send(request, host, port);
        return getRpcResponse(response);
    }

    private Object getRpcResponse(RpcResponse response) throws Throwable {
        if (response.getError() != null) {
            throw response.getError();
        }
        return response.getResult();
    }

    private void setProviderHost(String interfaceName) throws Throwable {
        if (this.registryHost != null && this.registryPort != -1 && this.host == null) {
            String host = RpcConsumer.getProviderHost(this.registryHost, this.registryPort, interfaceName);
            if (host == null) {
                throw new RuntimeException("从注册中心获取的服务提供者信息异常");
            }
            this.host = host;
        }
    }
}
