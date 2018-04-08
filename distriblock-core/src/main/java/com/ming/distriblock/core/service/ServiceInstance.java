package com.ming.distriblock.core.service;

import java.io.Serializable;

/**
 * Created by xueming on 2018/4/2.
 * 被协调的服务实例，包含一些服务的元数据
 */
public class ServiceInstance implements Serializable{
    /**
     *  默认分组
     */
    public static final String DEFAULT_SERVICE_GROUP = "_DEFAULT";
    /**
     * 协调服务分组
     */
    public static final String COOP_SERVICE_GROUP = "_COOPER";

    /**
     * 服务实例名称
     */
    private String serviceId;
    /**
     * 服务所属分组
     */
    private String groupId = DEFAULT_SERVICE_GROUP;
    /**
     * 服务所在的地址和端口信息
     */
    private String serviceIp;
    private String servicePort;


    /**
     * 服务实例是否可用
     */

    private boolean isAvailable = true;

    /**
     * 联合构造hashcode
     * @return
     */
    @Override
    public int hashCode(){
        return new StringBuilder(groupId).append("_")
                .append(serviceId).append("_").append(serviceIp)
                .append("_").append(servicePort).toString().hashCode();
    }

    @Override
    public boolean equals(Object other){
        ServiceInstance serviceInstance = (ServiceInstance) other;
        return this.hashCode() == serviceInstance.hashCode();
    }


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
