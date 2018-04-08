package com.ming.distriblock.client.client;

import com.ming.distriblock.core.ObjectHandler;
import com.ming.distriblock.core.RequestDistrbLockInfo;
import com.ming.distriblock.core.ResponseDistrbLockInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * Created by xueming on 2018/4/6.
 */
public class ClientHandler extends ChannelHandlerAdapter {
    private static final Logger logger = Logger.getLogger(ClientHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        ByteBuf buf = (ByteBuf) msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        ResponseDistrbLockInfo repDisLockInfo = ObjectHandler.readObject(data);
        logger.info("获取从服务传回的消息，锁标志位: " + repDisLockInfo.getPrimaryId()
                + "状态为:" + repDisLockInfo.isSuccess() + " serviceId:" + repDisLockInfo.getRequestedDistribMethod().getServiceInstance().getServiceId());

        RequestDistrbLockInfo reqDisLockInfo = new RequestDistrbLockInfo();
        reqDisLockInfo.setOpType(1);
        reqDisLockInfo.setSourceRequestMethod(repDisLockInfo.getRequestedDistribMethod());
        reqDisLockInfo.setPrimaryId(repDisLockInfo.getPrimaryId());
        logger.info("请求释放锁");
        ctx.writeAndFlush(Unpooled.copiedBuffer(ObjectHandler.writeObject(reqDisLockInfo)));

    }

}
