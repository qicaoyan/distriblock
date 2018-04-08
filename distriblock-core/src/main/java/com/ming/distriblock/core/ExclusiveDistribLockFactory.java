package com.ming.distriblock.core;

/**
 * Created by xueming on 2018/4/2.
 */
public class ExclusiveDistribLockFactory implements DistribLockFactory {

    @Override
    public DistribLock createLock(String primaryId) {
        DistribLock distribLock = new DistribLock(primaryId, true);
        return distribLock;
    }

//    @Override
//    public DistribLock createLock(String primaryId, DistribMethod method) {
//        DistribLock distribLock = new DistribLock(primaryId, true);
//        distribLock.setDistribMethod(method);
//        return distribLock;
//    }
}
