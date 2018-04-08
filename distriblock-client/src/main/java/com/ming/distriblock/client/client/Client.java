package com.ming.distriblock.client.client;

import com.ming.distriblock.core.ObjectHandler;
import com.ming.distriblock.core.RequestDistrbLockInfo;
import com.ming.distriblock.core.method.DistribMethod;
import com.ming.distriblock.core.service.ServiceInstance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by xueming on 2018/4/6.
 */
public class Client {
    public void run(int port, String serviceId) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        ChannelFuture future = bootstrap.connect("127.0.0.1", 3500).sync();
        RequestDistrbLockInfo reqDisLockInfo = new RequestDistrbLockInfo();
        reqDisLockInfo.setPrimaryId("0x000001");
        DistribMethod distribMethod = new DistribMethod();
        distribMethod.setClassFullName(Client.class.getName());
        distribMethod.setMethodDescriptor("run");
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceId(serviceId);
        serviceInstance.setAvailable(true);
        serviceInstance.setServiceIp("127.0.0.1");
        serviceInstance.setServicePort(String.valueOf(port));
        distribMethod.setServiceInstance(serviceInstance);
        reqDisLockInfo.setSourceRequestMethod(distribMethod);
        reqDisLockInfo.setOpType(0);
        future.channel().writeAndFlush(Unpooled.copiedBuffer(ObjectHandler.writeObject(reqDisLockInfo)));
    }
}
