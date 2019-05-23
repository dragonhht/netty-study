package hht.dragon.protocol.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-21
 */
@Getter
@Setter
@ToString
public final class Header implements Serializable {
    private static final long serialVersionUID = 4112047464555000164L;
    /** 校验码. */
    private int crcCode = 0xabef0101;
    /** 消息长度. */
    private int length;
    /** 会话ID. */
    private long sessionId;
    /** 消息类型. */
    private byte type;
    /** 消息优先级. */
    private byte priority;
    /** 附件. */
    private Map<String, Object> attachment = new HashMap<>();
}
