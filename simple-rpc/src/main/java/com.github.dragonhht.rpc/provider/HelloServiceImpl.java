package com.github.dragonhht.rpc.provider;

import com.github.dragonhht.rpc.api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-28
 */
@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        log.info("调用服务提供者: " + name);
        return "hello " + name;
    }
}
