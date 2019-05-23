package hht.dragon.protocol.codec;

import hht.dragon.protocol.model.Header;
import hht.dragon.protocol.model.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-21
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, -8, 0);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buf = (ByteBuf) super.decode(ctx, in);
        if (buf == null) {
            return null;
        }
        ProtocolMessage message = new ProtocolMessage();
        Header header = new Header();
        header.setCrcCode(buf.readInt());
        header.setLength(buf.readInt());
        header.setSessionId(buf.readLong());
        header.setType(buf.readByte());
        header.setPriority(buf.readByte());

        int size = buf.readInt();
        if (size > 0) {
            Map<String , Object> attachment = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = buf.readInt();
                keyArray = new byte[keySize];
                buf.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attachment.put(key, marshallingDecoder.decoder(buf));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attachment);
        }

        if (buf.readableBytes() > 4) {
            message.setBody(marshallingDecoder.decoder(buf));
        }
        message.setHeader(header);
        return message;
    }
}
