package com.ming.distriblock.core;

import com.ming.distriblock.core.method.DistribMethod;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xueming on 2018/4/2.
 * 分布式锁管理类，负责创建锁，并维护锁对象
 * 必须是单例的
 */
public class DistribLockSynchronizer {
    private Logger logger = Logger.getLogger(DistribLockSynchronizer.class);
    private DistribLockFactory factory;

    /**
     * 分布式锁Map, primaryId作为key，相应的锁对象作为Value
     */
    private final Map<String, DistribLock> distribLockMap = new ConcurrentHashMap<>();

    private static DistribLockSynchronizer synchronizer = null;

    /**
     * 默认是排他锁创建工厂
     */
    private DistribLockSynchronizer(){
        if(this.factory == null){
            this.factory = new ExclusiveDistribLockFactory();
        }
    }

    private DistribLockSynchronizer(DistribLockFactory factory){
        this.factory = factory;
    }


    public  void lock(String primaryId, DistribMethod distribMethod){
        /**
         * 不包含primaryKey
         */
        DistribLock distribLock = null;
        synchronized (this.distribLockMap){
            if(!this.distribLockMap.containsKey(primaryId)){
//                synchronized (this.distribLockMap){
                    distribLock = factory.createLock(primaryId);
                    this.distribLockMap.put(primaryId, distribLock);
//                }
//                return;
            }else{
                distribLock = this.distribLockMap.get(primaryId);
            }
        }

        if(distribLock != null){
            logger.info("服务实例" + distribMethod.getServiceInstance().getServiceId() + "正在获取锁");
            distribLock.lock(distribMethod);
            logger.info("服务实例" + distribMethod.getServiceInstance().getServiceId() +  "获取锁成功");
            //此处仅做调试，让线程暂停2秒，供测试
            try {
                logger.info("休眠2秒");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void unlock(String primaryId, DistribMethod distribMethod){
        DistribLock distribLock = this.distribLockMap.get(primaryId);
        if(distribLock != null){
            logger.info("服务实例" + distribMethod.getServiceInstance().getServiceId() + "正在释放锁");
            distribLock.unlock(distribMethod);
            logger.info("服务实例" + distribMethod.getServiceInstance().getServiceId() + "释放锁成功");
        }

    }


    public static DistribLockSynchronizer getInstance(){
        if(synchronizer == null){
            synchronized (DistribLockSynchronizer.class){
                if(synchronizer == null){
                    synchronizer = new DistribLockSynchronizer();
                }
            }
        }
        return synchronizer;
    }

}
