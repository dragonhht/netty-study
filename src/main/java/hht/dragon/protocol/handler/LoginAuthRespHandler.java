package hht.dragon.protocol.handler;

import hht.dragon.protocol.model.Header;
import hht.dragon.protocol.model.ProtocolMessage;
import hht.dragon.protocol.param.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 握手认证服务端.
 *
 * @author: huang
 * @Date: 2019-5-21
 */
@Slf4j
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    /** 记录认证的客户端. */
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
    /** 白名单. */
    private String[] whiteList = {"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolMessage message = (ProtocolMessage) msg;
        // 判断是否为握手请求信息
        if (message.getHeader() != null &&
        message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            ProtocolMessage loginResp = null;
            // 重复登录
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean ok = false;
                // 是否为白名单
                for (String whiteIp : whiteList) {
                    if (whiteIp.equals(ip)) {
                        ok = true;
                        break;
                    }
                }
                loginResp = ok ? buildResponse((byte) 0) : buildResponse((byte) -1);
                if (ok) {
                    nodeCheck.put(nodeIndex, ok);
                }
            }
            log.info("登录应答为: {}， 消息体为: {}", loginResp, loginResp.getBody());
            ctx.writeAndFlush(loginResp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private ProtocolMessage buildResponse(byte body) {
        ProtocolMessage message = new ProtocolMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(body);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
