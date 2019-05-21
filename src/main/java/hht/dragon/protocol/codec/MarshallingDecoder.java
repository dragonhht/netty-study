package hht.dragon.protocol.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-21
 */
public class MarshallingDecoder {
    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingCodecFactory.buildUnmarshaller();
    }

    protected Object decoder(ByteBuf in) throws Exception {
        int size = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), size);
        ByteInput input = new ChannelBufferByteInput(buf);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + size);
            return obj;
        } finally {
            unmarshaller.close();
        }
    }
}
