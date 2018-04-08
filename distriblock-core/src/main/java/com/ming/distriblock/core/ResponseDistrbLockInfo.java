package com.ming.distriblock.core;


import com.ming.distriblock.core.method.DistribMethod;

import java.io.Serializable;

/**
 * Created by xueming on 2018/4/6.
 * 请求分布式锁的响应信息
 */
public class ResponseDistrbLockInfo implements Serializable{
    /**
     * 分布式锁唯一标准
     */
    private String primaryId;
    /**
     * 锁请求状态标志
     */
    private boolean success;
    /**
     * 当前等待该分布式锁的请求数量快照信息
     */
    private int snapshotNumOfWaiters;
    /**
     * 请求分布式锁的方法对象（包含所在的服务实例信息）
     */
    private DistribMethod requestedDistribMethod;

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getSnapshotNumOfWaiters() {
        return snapshotNumOfWaiters;
    }

    public void setSnapshotNumOfWaiters(int snapshotNumOfWaiters) {
        this.snapshotNumOfWaiters = snapshotNumOfWaiters;
    }

    public DistribMethod getRequestedDistribMethod() {
        return requestedDistribMethod;
    }

    public void setRequestedDistribMethod(DistribMethod requestedDistribMethod) {
        this.requestedDistribMethod = requestedDistribMethod;
    }
}
