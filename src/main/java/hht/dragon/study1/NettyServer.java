package hht.dragon.study1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Netty服务端.
 *
 * @author: huang
 * Date: 18-2-5
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap server = new ServerBootstrap();
        // 1、绑定两个线程组分别处理客户端通道的ACCEPT和读写事件
        EventLoopGroup parentGroup = new NioEventLoopGroup();   //  用于处理ACCEPT事件
        EventLoopGroup childGroup = new NioEventLoopGroup();    //  用于处理读写事件
        server.group(parentGroup, childGroup);
        // 2、绑定服务通道NioServerSocketChannel
        server.channel(NioServerSocketChannel.class);
        // 3、为读写事件线程通道绑定handler区处理读写
        server.childHandler(new ChannelInitializer<SocketChannel>() {   // 初始化
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                // 获取管道并在管道中追加handler
                socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    /**
                     * 读取客户通道的数据.
                     * @param ctx
                     * @param msg
                     * @throws Exception
                     */
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof ByteBuf) {
                            String str = ((ByteBuf) msg).toString(Charset.defaultCharset());
                            System.out.println(str);
                        }
                        // 通知客户端
                        ctx.channel().writeAndFlush("信息已接受");
                    }
                });
                socketChannel.pipeline().addLast(new StringEncoder());

            }
        });
        // 4、监听端口，sync用于同步
        ChannelFuture future = server.bind(8080).sync();
        future.channel().closeFuture().sync();      // 当通道关闭后继续运行
    }

}
