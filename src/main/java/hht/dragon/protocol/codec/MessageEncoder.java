package hht.dragon.protocol.codec;

import hht.dragon.protocol.model.Header;
import hht.dragon.protocol.model.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 消息编码器.
 *
 * @author: huang
 * @Date: 2019-5-21
 */
public class MessageEncoder extends MessageToMessageEncoder<ProtocolMessage> {

    MarshallingEncoder marshallingEncoder;

    public MessageEncoder() throws IOException {
        marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          ProtocolMessage protocolMessage, List<Object> list) throws Exception {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            throw new Exception("需编码的信息为空");
        }
        Header header = protocolMessage.getHeader();
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(header.getCrcCode());
        byteBuf.writeInt(header.getLength());
        byteBuf.writeLong(header.getSessionId());
        byteBuf.writeByte(header.getType());
        byteBuf.writeByte(header.getPriority());
        byteBuf.writeInt(header.getAttachment().size());
        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> entry : header.getAttachment().entrySet()) {
            key = entry.getKey();
            keyArray = key.getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(keyArray.length);
            byteBuf.writeBytes(keyArray);
            value = entry.getValue();
            marshallingEncoder.encode(value, byteBuf);
        }

        if (protocolMessage.getBody() != null) {
            marshallingEncoder.encode(protocolMessage.getBody(), byteBuf);
        } else {
            byteBuf.writeInt(0);
        }
        byteBuf.setInt(4, byteBuf.readableBytes());
        list.add(byteBuf);
    }
}
