package com.ming.distriblock.core.service;

import java.util.List;

/**
 * Created by xueming on 2018/4/2.
 * 服务获取接口
 */
public interface ServiceGet {
    List<ServiceInstance> getServiceInstances(String groupId, String serviceId);
    ServiceInstance getServiceInstance(String groupId, String serviceId, String serviceIp, String servicePort);
}
