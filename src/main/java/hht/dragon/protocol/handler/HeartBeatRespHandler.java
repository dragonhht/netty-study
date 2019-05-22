package hht.dragon.protocol.handler;

import hht.dragon.protocol.model.Header;
import hht.dragon.protocol.model.ProtocolMessage;
import hht.dragon.protocol.param.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端应答心跳.
 *
 * @author: huang
 * @Date: 2019-5-22
 */
@Slf4j
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolMessage message = (ProtocolMessage) msg;
        // 返回心跳应答
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            log.info("接收客户端心跳信息: " + message);
            ProtocolMessage heartBeat = buildHeatBeat();
            log.info("发送心跳回应: {}", message);
            ctx.writeAndFlush(heartBeat);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private ProtocolMessage buildHeatBeat() {
        ProtocolMessage message = new ProtocolMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }
}
