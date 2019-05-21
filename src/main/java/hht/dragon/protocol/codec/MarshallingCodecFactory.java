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
    public static io.netty.handler.codec.marshalling.MarshallingDecoder buildMarshallingDecoder() {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory, configuration);
        io.netty.handler.codec.marshalling.MarshallingDecoder decoder = new io.netty.handler.codec.marshalling.MarshallingDecoder(provider, 1024*1024);
        return decoder;
    }

    public static Marshaller buildMarshaller() throws IOException {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return factory.createMarshaller(configuration);
    }

    public static Unmarshaller buildUnmarshaller() throws IOException {
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        return factory.createUnmarshaller(configuration);
    }

    public static io.netty.handler.codec.marshalling.MarshallingEncoder buildMarshallingEncoder() {

        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(factory, configuration);
        io.netty.handler.codec.marshalling.MarshallingEncoder encoder = new io.netty.handler.codec.marshalling.MarshallingEncoder(provider);
        return encoder;
    }
}
