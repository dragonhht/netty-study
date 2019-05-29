package com.github.dragonhht.rpc.annos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标志对外服务的实现类.
 *
 * @author: huang
 * @Date: 2019-5-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    /** 接口名. */
    Class interfaceName();
}
