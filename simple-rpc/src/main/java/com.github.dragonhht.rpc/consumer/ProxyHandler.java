package com.github.dragonhht.rpc.consumer;

import com.github.dragonhht.rpc.model.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 使用动态代理调用远程服务.
 *
 * @author: huang
 * @Date: 2019-5-29
 */
public class ProxyHandler implements InvocationHandler {

    private Class interfaceClass;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        return null;
    }
}
