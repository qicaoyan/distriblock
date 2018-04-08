package com.ming.distriblock.core.method;


import com.ming.distriblock.core.service.ServiceInstance;

import java.io.Serializable;

/**
 * Created by xueming on 2018/4/2.
 * 分布式方法，用以标志该方法属于哪个服务实例，
 * 以及方法的坐标（全限定名称 + 方法描述符）
 */
public class DistribMethod implements Serializable{
    /**
     * 该方法所属的服务实例
     */
    private ServiceInstance serviceInstance;
    /**
     * 方法所属的类全限定名称
     */
    private String classFullName;
    /**
     * 方法的描述符
     */
    private String methodDescriptor;

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    public void setMethodDescriptor(String methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        DistribMethod distribMethod = (DistribMethod) other;
        return this.serviceInstance.equals(distribMethod.getServiceInstance())
                && this.classFullName.equals(distribMethod.getClassFullName())
                && this.methodDescriptor.equals(distribMethod.getMethodDescriptor());
    }
}
