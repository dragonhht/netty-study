package hht.dragon.http.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Http文件服务器.
 *
 * @author: huang
 * Date: 18-6-15
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "/home/huang/CodeTest/";

    public void run(int port, final String url) {
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // Http消息解码器
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            // 将多个消息转换为单一的FullHttpRequest或FullHttpResponse
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            // Http响应编码器，对HTTP响应消息进行编码
                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            // 新增Chunker handler，支持异步发送大的码流，但不占用过多内存
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            // 添加http文件服务器的业务逻辑处理器
                            socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture future = server.bind("127.0.0.1", port).sync();
            System.out.println("服务已启动...");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        new HttpFileServer().run(port, HttpFileServer.DEFAULT_URL);
    }

}
