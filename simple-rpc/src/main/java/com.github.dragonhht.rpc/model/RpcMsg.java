package com.github.dragonhht.rpc.model;

import lombok.*;

import java.io.Serializable;

/**
 * 调用信息.
 *
 * @author: huang
 * @Date: 2019-5-28
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RpcMsg implements Serializable {
    private static final long serialVersionUID = -3387583808363913669L;
    /** 类名. */
    private String className;
    /** 方法名. */
    private String methodName;
    /** 方法参数类型. */
    private Class[] paramsType;
    /** 调用参数. */
    private Object[] params;
}
