package com.github.dragonhht.rpc.model;

import lombok.*;

import java.io.Serializable;

/**
 * 注册中心返回给服务消费者的服务提供者信息.
 *
 * @author: huang
 * @Date: 2019-5-31
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerResponse implements Serializable {
    private static final long serialVersionUID = -6075658419883116804L;

    private String interfaceName;
    private String host;

    private Throwable error;

}
