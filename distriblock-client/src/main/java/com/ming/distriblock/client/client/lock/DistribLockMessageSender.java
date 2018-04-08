package com.ming.distriblock.client.client.lock;

import com.ming.distriblock.client.client.io.DistribLockClient;
import com.ming.distriblock.client.client.io.loadbalance.ILoadBalancer;
import com.ming.distriblock.client.client.io.loadbalance.LockIdHashLoadBalancer;
import com.ming.distriblock.core.ObjectHandler;
import com.ming.distriblock.core.RequestDistrbLockInfo;
import com.ming.distriblock.core.method.DistribMethod;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

/**
 * Created by xueming on 2018/4/8.
 */
public class DistribLockMessageSender {
    private final ILoadBalancer loadBalancer;
    /**
     * 请求申请分布式锁的服务地址信息
     */
    private InetSocketAddress lockServerSocketAddress;
    /**
     * 请求释放分布式锁的服务地址信息
     */
    private InetSocketAddress unlockServerSocketAddress;

    public DistribLockMessageSender(){
        this.loadBalancer = new LockIdHashLoadBalancer();
    }

    public DistribLockMessageSender(ILoadBalancer loadBalancer){
        this.loadBalancer = loadBalancer;
    }

    public void sendLockRequest(String primaryId, DistribMethod distribMethod) throws Exception {
        InetSocketAddress socketAddress = loadBalancer.loadBalance(primaryId,
                DistribLockClient.getClient().
                        getConnectionConfig().getServerSocketAddress());
        if(socketAddress != null){
            ChannelFuture channelFuture = DistribLockClient.getClient().
                    getChannelFuture(socketAddress);
            RequestDistrbLockInfo reqDisLockInfo = new RequestDistrbLockInfo();
            reqDisLockInfo.setSourceRequestMethod(distribMethod);
            reqDisLockInfo.setOpType(0);
            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(ObjectHandler.writeObject(reqDisLockInfo)));
            lockServerSocketAddress = socketAddress;
            unlockServerSocketAddress = socketAddress;
        }
    }
}
