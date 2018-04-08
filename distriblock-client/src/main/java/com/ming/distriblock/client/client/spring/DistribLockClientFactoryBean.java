package com.ming.distriblock.client.client.spring;

import com.ming.distriblock.client.client.io.Address;
import com.ming.distriblock.client.client.io.ConnectionConfig;
import com.ming.distriblock.client.client.io.DistribLockClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xueming on 2018/4/8.
 */
@PropertySource("classpath:servers.properties")
public class DistribLockClientFactoryBean implements FactoryBean<DistribLockClient> {
    /**
     * 连接的分布式锁服务的地址信息
     */
    @Value("${servers.hostname}")
    private String[] hostnames;
    /**
     * 连接的分布式锁服务的端口信息
     */
    @Value("${servers.port}")
    private int[] ports;
    /**
     * 客户端绑定的端口
     */
    private int selfPort;
    /**
     * 客户端生成ServiceInstance所需的ID信息
     */
    private String selfServiceId;
    @Override
    public DistribLockClient getObject() throws Exception {
        DistribLockClient client = new DistribLockClient();
        ConnectionConfig connectionConfig = new ConnectionConfig();
        connectionConfig.setSelfPort(selfPort);
        String hostname = InetAddress.getLocalHost().getHostName();
        connectionConfig.setSelfHostName(hostname);
        connectionConfig.setSelfPort(selfPort);
        if(hostnames == null || ports == null || (hostnames.length != ports.length)){
            throw new IllegalStateException("连接的分布式服务集群地址和端口数量不一致");
        }
        List<InetSocketAddress> socketAddresses = new ArrayList<>();
        for(int i = 0; i < hostnames.length; i++){
            InetSocketAddress inetSocketAddress =
                    new InetSocketAddress(hostnames[i], ports[i]);
            socketAddresses.add(inetSocketAddress);
        }
        connectionConfig.setServerSocketAddress(socketAddresses);
        client.wrapClientServiceInstance(selfServiceId);
        client.init(connectionConfig);
        return client;
    }


    @Override
    public Class<?> getObjectType() {
        return DistribLockClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String[] getHostnames() {
        return hostnames;
    }

    public void setHostnames(String[] hostnames) {
        this.hostnames = hostnames;
    }

    public int[] getPorts() {
        return ports;
    }

    public void setPorts(int[] ports) {
        this.ports = ports;
    }

    public int getSelfPort() {
        return selfPort;
    }

    public void setSelfPort(int selfPort) {
        this.selfPort = selfPort;
    }

    public String getSelfServiceId() {
        return selfServiceId;
    }

    public void setSelfServiceId(String selfServiceId) {
        this.selfServiceId = selfServiceId;
    }
}
