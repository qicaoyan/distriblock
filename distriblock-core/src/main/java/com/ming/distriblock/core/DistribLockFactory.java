package com.ming.distriblock.core;

/**
 * Created by xueming on 2018/4/2.
 */
public interface DistribLockFactory {
    DistribLock createLock(String primaryId);
}
