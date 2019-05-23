package hht.dragon.protocol.codec;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-21
 */
public class MarshallingCodecFactory {

    private static final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
    private static final MarshallingConfiguration configuration = new MarshallingConfiguration();

    static {
        configuration.setVersion(5);
    }

    public static io.netty.handler.codec.marshalling.MarshallingDecoder buildMarshallingDecoder() {
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, configuration);
        io.netty.handler.codec.marshalling.MarshallingDecoder decoder = new io.netty.handler.codec.marshalling.MarshallingDecoder(provider, 1024*1024);
        return decoder;
    }

    public static Marshaller buildMarshaller() throws IOException {
        return factory.createMarshaller(configuration);
    }

    public static Unmarshaller buildUnmarshaller() throws IOException {
        return factory.createUnmarshaller(configuration);
    }

    public static io.netty.handler.codec.marshalling.MarshallingEncoder buildMarshallingEncoder() {
        MarshallerProvider provider = new DefaultMarshallerProvider(factory, configuration);
        io.netty.handler.codec.marshalling.MarshallingEncoder encoder = new io.netty.handler.codec.marshalling.MarshallingEncoder(provider);
        return encoder;
    }
}
