package hht.dragon.protocol.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 私有协议.
 *
 * @author: huang
 * @Date: 2019-5-21
 */
@Getter
@Setter
@ToString
public final class ProtocolMessage implements Serializable {
    private static final long serialVersionUID = -8947644415144038549L;
    /** 消息头. */
    private Header header;
    /** 消息体. */
    private Object body;
}
