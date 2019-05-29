package com.github.dragonhht.rpc.model;

import lombok.*;

import java.io.Serializable;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-29
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServiceModel implements Serializable {
    private static final long serialVersionUID = -2067742320640065065L;

    /** 接口名. */
    private String interfaceName;
    /** 实例. */
    private Object instance;
}
