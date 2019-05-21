package hht.dragon.protocol.handler;

import hht.dragon.protocol.model.Header;
import hht.dragon.protocol.model.ProtocolMessage;
import hht.dragon.protocol.param.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;

/**
 * 客户端发送心跳请求.
 *
 * @author: huang
 * @Date: 2019-5-21
 */
@Slf4j
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolMessage message = (ProtocolMessage) msg;
        // 握手成功，发送心跳信息
        if (message.getHeader() != null &&
        message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {

        }
    }

    private void runHeartBeatTask(ChannelHandlerContext ctx) {
        ProtocolMessage heartBeat = buildHeartBeat();
        log.info("客户端发送心跳检测到服务端: {}", heartBeat);
        ctx.writeAndFlush(heartBeat);
    }

    private ProtocolMessage buildHeartBeat() {
        ProtocolMessage message = new ProtocolMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_REQ.value());
        message.setHeader(header);
        return message;
    }
}
