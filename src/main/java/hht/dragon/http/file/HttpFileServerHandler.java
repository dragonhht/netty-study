package hht.dragon.http.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * Description.
 *
 * @author: huang
 * Date: 18-6-15
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (!fullHttpRequest.decoderResult().isSuccess()) {

            return;
        }
        if (fullHttpRequest.method() != HttpMethod.GET) {

            return;
        }
        final String url = fullHttpRequest.uri();
        final String path = saniti(url);
        System.out.println("PATH: " + path);

        File[] files = getFiles(path);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-type", "text/html;charset=UTF-8");
        StringBuffer stringBuffer = new StringBuffer();
        if (files != null) {
            stringBuffer.append("<html><body><ul>");
            stringBuffer.append("<li><a href='../'>../</a></li>");
            for (File file : files) {
                String uri = file.getPath();
                stringBuffer.append("<li><a href='" + uri + "'>" + file.getName() + "</a></li>");
            }
            stringBuffer.append("</ul></body></html>");
            ByteBuf byteBuf = Unpooled.copiedBuffer(stringBuffer, CharsetUtil.UTF_8);
            response.content().writeBytes(byteBuf);
            byteBuf.release();
        } else {
            // TODO 发送文件至客户端
        }

        channelHandlerContext.writeAndFlush(response);
    }

    private String saniti(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url = url.replace('/', File.separatorChar);
        if (url.contains(File.separator + ".")
                || url.contains('.' + File.separator) || url.startsWith(".")
                || url.endsWith(".")) {
            return null;
        }
        if ("/".equals(url)) {
            return this.url;
        }
        return url;
    }

    /**
     * 获取指定路径下的文件及文件夹.
     * @param path 路径
     * @return
     */
    public File[] getFiles(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("路径不存在...");
            return null;
        }
        if (file.isDirectory()) {
            return file.listFiles();
        }
        return null;
    }
}
