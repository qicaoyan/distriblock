package com.ming.distriblock.core;


import com.ming.distriblock.core.method.DistribMethod;

import java.io.Serializable;

/**
 * Created by xueming on 2018/4/6.
 * 请求分布式锁的信息
 */
public class RequestDistrbLockInfo implements Serializable{
    /**
     * 请求的分布式锁ID
     */
    private String primaryId;
    /**
     * 哪个方法被请求
     */
    private DistribMethod sourceRequestMethod;
    /**
     * 操作类型，释放还是释放
     * 0:锁定
     * 1:释放
     */
    private int opType;

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public DistribMethod getSourceRequestMethod() {
        return sourceRequestMethod;
    }

    public void setSourceRequestMethod(DistribMethod sourceRequestMethod) {
        this.sourceRequestMethod = sourceRequestMethod;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }
}
