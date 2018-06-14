package hht.dragon.time.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * Description.
 * User: huang
 * Date: 18-6-14
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            // 注意 此处为 ByteBuf对象
            ByteBuf buffer = (ByteBuf) msg;
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            String str = new String(bytes, "UTF-8");
            System.out.println("信息：" + str);
            String currentime = "QUERY TIME ORDER".equalsIgnoreCase(str) ? new Date(
                    System.currentTimeMillis()
            ).toString() : "BAD ORDER";
            ByteBuf req = Unpooled.copiedBuffer(currentime.getBytes());
            // 异步发送消息给客户端
            ctx.write(req);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将信息发送队列中的消息写入到SocketChannel中发送给对方
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
