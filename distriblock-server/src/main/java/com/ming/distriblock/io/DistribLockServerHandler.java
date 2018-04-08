package com.ming.distriblock.io;

import com.ming.distriblock.core.DistribLockSynchronizer;
import com.ming.distriblock.core.ObjectHandler;
import com.ming.distriblock.core.RequestDistrbLockInfo;
import com.ming.distriblock.core.ResponseDistrbLockInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * Created by xueming on 2018/4/3.
 */
public class DistribLockServerHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(DistribLockServerHandler.class);
    DistribLockSynchronizer synchronizer = DistribLockSynchronizer.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        ByteBuf buf = (ByteBuf) msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        RequestDistrbLockInfo reqDisLockInfo = ObjectHandler.readObject(data);
        logger.info("收到客户端" + reqDisLockInfo.getSourceRequestMethod().getServiceInstance().getServiceId() + "发来的消息");
        //只有接收到的请求对象不为空时，才进行处理
        if(reqDisLockInfo != null){
            if(reqDisLockInfo.getOpType() == 0){
                synchronizer.lock(reqDisLockInfo.getPrimaryId(), reqDisLockInfo.getSourceRequestMethod());
                ResponseDistrbLockInfo repDisLockInfo = new ResponseDistrbLockInfo();
                repDisLockInfo.setPrimaryId(reqDisLockInfo.getPrimaryId());
                repDisLockInfo.setRequestedDistribMethod(reqDisLockInfo.getSourceRequestMethod());
                repDisLockInfo.setSuccess(true);
                ctx.writeAndFlush(Unpooled.copiedBuffer(ObjectHandler.writeObject(repDisLockInfo)));
            }else {
                synchronizer.unlock(reqDisLockInfo.getPrimaryId(), reqDisLockInfo.getSourceRequestMethod());
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        cause.printStackTrace();
        ctx.close();
    }


//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelRegistered();
//    }
}
