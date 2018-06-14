# Netty学习

## 1、简介

> Netty是一个NIO客户端服务器框架，可以快速简单地开发协议服务器和客户端等网络应用程序。它极大地简化TCP和UDP套接字服务器等网络编程。

## 2、使用

-   maven依赖

```
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.20.Final</version>
</dependency>
```

-   [事例一](./src/main/java/hht/dragon/study1)

-   服务器端
 
    -   通过创建`EventLoopGroup`实例创建NIO线程组
    
    -   创建`ServerBootstrap`对象，它是Netty用于启动NIO服务端的辅助启动类，并调用`ServerBootstrap`对象的`group`方法，将线程组传递到`ServerBootstrap`实例
    
    -   设置创建的`channel`为`NioServerSocketChannel`，`NioServerSocketChannel`相当于JDK NIO中的`ServerSocketChannel`
    
    -   设置`NioServerSocketChannel`的TCP参数
       
    -   绑定I/O事件的处理类，用于处理网络I/O事件
    
    -   服务端启动辅助类配置完成之后，调用`bind`方法绑定监听端口，随后调用同步阻塞方法`sync`等待任务完成，并返回一个`ChannelFuture`对象，用于异步操作的通知回调
    
    -   使用`channelFuture.channel().closeFuture().sync();`方法进行阻塞，等待服务端链路关闭之后main函数才退出
    
    -   使用NIO线程组的`shutdownGracefully`，释放资源

-   客户端操作与服务端基本类似，不过启动辅助类为`Bootstrap`,且`channel`设置为`NioSocketChannel`

## 3、使用Netty的半包解码器解决TCP粘包/拆包问题

-   [利用`LineBasedFrameDecoder` + `StringDecoder`(处理按行切换的文本)解决TCP粘包/拆包问题](./src/main/java/hht/dragon/stickyandupack)

    -   分别在服务端和客户端添加两个解码器
    
        -   服务端
        
        ```
        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new StringDecoder());
        ```
        
        -   客户端
        
        ```
        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        socketChannel.pipeline().addLast(new StringDecoder());
        ```
        
    -   分别在服务端和客户端的处理类中进行修改
    
    -   服务端
    
    ```
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String str = (String) msg;
            System.out.println("信息：" + str + "; the counter is " + ++counter);
            String currentime = "QUERY TIME ORDER".equalsIgnoreCase(str) ? new Date(
                    System.currentTimeMillis()
            ).toString() : "BAD ORDER";
            currentime = currentime + System.getProperty("line.separator");
            ByteBuf req = Unpooled.copiedBuffer(currentime.getBytes());
            // 异步发送消息给客户端
            ctx.writeAndFlush(req);
        }
    ```
    
    -   客户端
    
    ```
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String body = (String) msg;
            System.out.println("Now is : " + body + "; the counter is " + ++counter);
        }
    ```
   
-   说明

> `LineBasedFrameDecoder`会依次便利`ByteBuf`中的可读字节，判断是否有`\n`或`\r\n`,如果有，就以次位置为结束位置，`LineBasedFrameDecoder`是以换行符为结束标志的解码器  
> `StringDecoder`将接收到的对象转换为字符串

-   [利用`DelimiterBasedFrameDecoder`(以指定的分隔符作为结束标志)+`StringDecoder`解决TCP粘包/拆包问题](./src/main/java/hht/dragon/echo)

-   利用`FixedLengthFrameDecoder`(以指定长度)+`StringDecoder`解决TCP粘包/拆包问题


