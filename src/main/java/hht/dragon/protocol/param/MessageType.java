package hht.dragon.protocol.param;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-21
 */
public enum MessageType {
    /** 登录请求 */
    LOGIN_REQ((byte)3),
    /** 登录应答. */
    LOGIN_RESP((byte)4),
    /** 心跳请求. */
    HEARTBEAT_REQ((byte)5),
    /** 心跳相应. */
    HEARTBEAT_RESP((byte)6),

    ;

    private byte value;

    MessageType(byte v){
        this.value = v;
    }

    public byte value(){
        return value;
    }

}
