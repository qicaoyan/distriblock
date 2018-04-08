package com.ming.distriblock.client.client.lock;

import com.ming.distriblock.client.client.io.DistribLockClient;
import com.ming.distriblock.client.client.io.loadbalance.ILoadBalancer;
import com.ming.distriblock.client.client.io.loadbalance.LockIdHashLoadBalancer;
import com.ming.distriblock.core.DistribLock;
import com.ming.distriblock.core.method.DistribMethod;
import com.ming.distriblock.core.service.ServiceInstance;
import com.sun.org.apache.bcel.internal.generic.ILOAD;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xueming on 2018/4/8.
 * 分布式锁的客户端代理
 */
public class DistribLockStub {
    private String primaryId;
    private final ILoadBalancer loadBalancer;

    public DistribLockStub(){
        loadBalancer = new LockIdHashLoadBalancer();
    }

    public DistribLockStub(String primaryId, ILoadBalancer loadBalancer){
        this.primaryId = primaryId;
        this.loadBalancer = loadBalancer;
    }

    /**
     * 客户端锁，对多线程并发获取同一个primaryId的分布式锁进行并发控制
     */
    private Lock clientLock = new ReentrantLock();

    public void lock(){
        clientLock.lock();
        DistribMethod distribMethod = new DistribMethod();
        /**
         * 获取方法调用栈信息：
         * 为了获取调用当前lock的方法信息
         */
        StackTraceElement[] stackTraceElements =
                Thread.currentThread().getStackTrace();
        if(stackTraceElements != null && stackTraceElements.length > 2){
            StackTraceElement stackTraceElement = stackTraceElements[2];
            String classFullName = stackTraceElement.getClassName();
            String methodDesc = stackTraceElement.getMethodName() + "_line_" +
                    stackTraceElement.getLineNumber();
            distribMethod.setClassFullName(classFullName);
            distribMethod.setMethodDesc(methodDesc);
        }
        /**
         * 设置DistribMethod的ServiceInstance信息
         */
        distribMethod.setServiceInstance(DistribLockClient.getClient().getServiceInstance());
    }


    public void lock(long timeout){

    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }
}
