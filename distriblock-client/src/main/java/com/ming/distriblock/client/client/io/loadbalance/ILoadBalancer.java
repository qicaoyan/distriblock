package com.ming.distriblock.client.client.io.loadbalance;

import com.ming.distriblock.core.method.DistribMethod;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by xueming on 2018/4/8.
 * 根据分布式锁ID进行负载均衡
 */
public interface ILoadBalancer {
    InetSocketAddress loadBalance(String primaryId,
                                  List<InetSocketAddress> addressList);
}
