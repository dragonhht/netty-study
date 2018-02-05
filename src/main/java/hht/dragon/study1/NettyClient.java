package hht.dragon.study1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * Netty客户端.
 *
 * @author: huang
 * Date: 18-2-5
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        Bootstrap client = new Bootstrap();
        // 1、绑定线程组, 只处理读写和连接事件
        EventLoopGroup group = new NioEventLoopGroup();
        client.group(group);
        // 2、绑定通道
        client.channel(NioSocketChannel.class);
        // 绑定handler处理读写事件
        client.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof ByteBuf) {
                            System.out.println(((ByteBuf) msg).toString(Charset.defaultCharset()));
                        }
                        ctx.channel().close();
                    }
                });
                // 添加编码器
                nioSocketChannel.pipeline().addLast(new StringEncoder());
            }
        });
        // 连接,并同步返回
        ChannelFuture future = client.connect("localhost", 8080).sync();

        // 发送数据
        future.channel().writeAndFlush("客户端发送一条信息");

        future.channel().closeFuture().sync();
        System.out.println("客户端已关闭");
    }
}
