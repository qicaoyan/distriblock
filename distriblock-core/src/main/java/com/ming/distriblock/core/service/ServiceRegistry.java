package com.ming.distriblock.core.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xueming on 2018/4/2.
 * 服务注册表
 */
public class ServiceRegistry implements ServiceGet, ServiceRegist, Serializable {
    /**
     * 按照组来划分服务注册信息
     */
    private ConcurrentHashMap<String, Set<ServiceInstance>> groupServiceRegistry;

    private static ServiceRegistry serviceRegistry;

    private ServiceRegistry(){

    }

    @Override
    public boolean registService(ServiceInstance serviceInstance) {
        /**
         * 单例模式构造服务注册器
         */
        if(this.groupServiceRegistry == null){
            synchronized (this.groupServiceRegistry){
                if(this.groupServiceRegistry == null){
                    this.groupServiceRegistry = new ConcurrentHashMap<>();
                }
            }
        }
        String groupId = serviceInstance.getGroupId();
        Set<ServiceInstance> serviceInstanceSet;
        if(this.groupServiceRegistry.containsKey(groupId)){
            serviceInstanceSet = this.groupServiceRegistry.get(groupId);
        }else{
            serviceInstanceSet = new HashSet<>();
        }

        /**
         * 此处是否需要考虑并发
         */
        if(serviceInstanceSet.contains(serviceInstance)){
            return false;
        }
        serviceInstanceSet.add(serviceInstance);
        return true;
    }

    /**
     * 获取服务实例（多个）
     * @param groupId
     * @param serviceId
     * @return
     */
    @Override
    public List<ServiceInstance> getServiceInstances(String groupId, String serviceId) {
        if(groupId == null){
            groupId = ServiceInstance.DEFAULT_SERVICE_GROUP;
        }
        List<ServiceInstance> serviceInstances = null;
        Set<ServiceInstance> serviceInstanceSet = this.groupServiceRegistry.get(groupId);
        if(serviceInstanceSet == null){
            return null;
        }
        if(serviceId == null){
            serviceInstances = Collections.emptyList();
            for(ServiceInstance serviceInstance : serviceInstanceSet){
                serviceInstances.add(serviceInstance);
            }
        }else{
            for(ServiceInstance serviceInstance : serviceInstanceSet){
                if(serviceInstances == null){
                    serviceInstances = Collections.emptyList();
                }
                serviceInstances.add(serviceInstance);
            }
        }
        return serviceInstances;
    }

    /**
     * 获取服务实例（单个）
     * @param groupId
     * @param serviceId
     * @param serviceIp
     * @param servicePort
     * @return
     */
    @Override
    public ServiceInstance getServiceInstance(String groupId, String serviceId, String serviceIp, String servicePort) {
        if(groupId == null){
            groupId = ServiceInstance.DEFAULT_SERVICE_GROUP;
        }
        if(serviceId == null || serviceIp == null || servicePort == null){
            return null;
        }
        Set<ServiceInstance> serviceInstanceSet = this.groupServiceRegistry.get(groupId);
        if(serviceInstanceSet != null){
            for(ServiceInstance serviceInstance : serviceInstanceSet){
                if(serviceId.equals(serviceInstance.getServiceId())
                        && serviceIp.equals(serviceInstance.getServiceIp())
                        && servicePort.equals(serviceInstance.getServicePort())){
                    return serviceInstance;
                }

            }
        }
        return null;
    }

    public static ServiceRegistry getInstance(){
        if(serviceRegistry == null){
            synchronized (serviceRegistry){
                if(serviceRegistry == null){
                    serviceRegistry = new ServiceRegistry();
                }
            }
        }
        return serviceRegistry;
    }
}
