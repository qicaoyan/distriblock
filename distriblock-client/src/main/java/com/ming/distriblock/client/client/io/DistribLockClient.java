package com.ming.distriblock.client.client.io;

import com.ming.distriblock.core.service.ServiceInstance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xueming on 2018/4/7.
 */
public class DistribLockClient {
    /**
     * 该客户端生成的ServiceInstance实例
     */
    private ServiceInstance serviceInstance;
    private ConnectionConfig connectionConfig;
    private Map<InetSocketAddress, ChannelFuture> connectMap = new HashMap<>();

    private static DistribLockClient client = null;

    public void init(ConnectionConfig config) throws Exception{
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
        if(config != null && config.getServerSocketAddress() != null){
            this.connectionConfig = config;
            for(InetSocketAddress socketAddress : config.getServerSocketAddress()){
                ChannelFuture future = connect(bootstrap, socketAddress);
                if(future != null){
                    connectMap.put(socketAddress, future);
                }
            }
        }
        client = this;

    }

    private ChannelFuture connect(Bootstrap bootstrap,
                                  InetSocketAddress address) throws InterruptedException {
        ChannelFuture future = null;
        if(address.isUnresolved()){
            future = bootstrap.connect(address).sync();
        }
        return future;
    }

    public ConnectionConfig getConnectionConfig() {
        return connectionConfig;
    }

    public void setConnectionConfig(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public Map<InetSocketAddress, ChannelFuture> getConnectMap() {
        return connectMap;
    }

    public void setConnectMap(Map<InetSocketAddress, ChannelFuture> connectMap) {
        this.connectMap = connectMap;
    }

    public ServiceInstance wrapClientServiceInstance(String serviceId){
        this.serviceInstance = new ServiceInstance();
        this.serviceInstance.setServiceId(serviceId);
        this.serviceInstance.setServiceIp(connectionConfig.getSelfHostName());
        this.serviceInstance.setServicePort(String.valueOf(connectionConfig.getSelfPort()));
        return this.serviceInstance;
    }

    public static DistribLockClient getClient() {
        return client;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public ChannelFuture getChannelFuture(InetSocketAddress socketAddress){
        if(this.connectMap != null){
            return this.connectMap.get(socketAddress);
        }
        return null;
    }
}
