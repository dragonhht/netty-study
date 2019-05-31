package com.github.dragonhht.rpc.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 服务消费者请求注册中心获取服务信息的实体.
 *
 * @author: huang
 * @Date: 2019-5-31
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerRequest implements Serializable {
    private static final long serialVersionUID = -6075658419883116804L;

    private String interfaceName;

}
