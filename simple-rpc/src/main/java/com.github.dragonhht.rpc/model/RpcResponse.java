package com.github.dragonhht.rpc.model;

import lombok.*;

import java.io.Serializable;

/**
 * 服务响应信息.
 *
 * @author: huang
 * @Date: 2019-5-29
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -8978433541363031720L;

    private Object result;
}
