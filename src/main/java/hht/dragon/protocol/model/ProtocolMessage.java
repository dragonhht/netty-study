package hht.dragon.protocol.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 私有协议.
 *
 * @author: huang
 * @Date: 2019-5-21
 */
@Getter
@Setter
@ToString
public final class ProtocolMessage {
    /** 消息头. */
    private Header header;
    /** 消息体. */
    private Object body;
}
