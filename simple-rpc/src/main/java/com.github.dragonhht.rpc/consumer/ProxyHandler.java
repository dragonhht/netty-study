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

    public ProxyHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static Object newInstance(String host, int port, Class target) {
        Class[] interfaces = {target};
        return Proxy.newProxyInstance(ProxyHandler.class.getClassLoader(),
                interfaces, new ProxyHandler(host, port));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamsType(method.getParameterTypes());
        request.setParams(args);
        RpcResponse result = new RpcConsumer().send(request, host, port);
        if (result.getError() != null) {
            throw result.getError();
        }
        return result;
    }
}
