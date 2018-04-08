package com.ming.distriblock.client.client.io.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by xueming on 2018/4/8.
 */
public class LockIdHashLoadBalancer implements ILoadBalancer {
    @Override
    public InetSocketAddress loadBalance(String primaryId, List<InetSocketAddress> addressList) {
        if(addressList == null || addressList.size() == 0){
            return null;
        }
        int index = primaryId.hashCode() % addressList.size();
        return addressList.get(index);
    }
}
