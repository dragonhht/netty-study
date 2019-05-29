package com.github.dragonhht.rpc.model;

import lombok.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务提供者发送该注册中心的信息.
 *
 * @author: huang
 * @Date: 2019-5-29
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRequest implements Serializable {
    private static final long serialVersionUID = -8939925702418771359L;
    /** 服务提供者域名. */
    private String host;
    /** 服务提供者提供的服务. */
    private List<String> interfaceNames = new LinkedList<>();
}
