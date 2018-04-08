package com.ming.distriblock.client.client.io;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by xueming on 2018/4/7.
 */
public class ConnectionConfig {
    private List<InetSocketAddress> serverSocketAddress;
    private int selfPort;
    private String selfHostName;

    public List<InetSocketAddress> getServerSocketAddress() {
        return serverSocketAddress;
    }

    public void setServerSocketAddress(List<InetSocketAddress> serverSocketAddress) {
        this.serverSocketAddress = serverSocketAddress;
    }

    public int getSelfPort() {
        return selfPort;
    }

    public void setSelfPort(int selfPort) {
        this.selfPort = selfPort;
    }

    public String getSelfHostName() {
        return selfHostName;
    }

    public void setSelfHostName(String selfHostName) {
        this.selfHostName = selfHostName;
    }
}
