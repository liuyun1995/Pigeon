package com.dianping.pigeon.remoting.invoker.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.dianping.pigeon.util.NetUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.CollectionUtils;

//连接信息
public class ConnectInfo {

    private String host;                                   //本机地址
    private int port;                                      //本机端口
    private ConcurrentMap<String, Integer> serviceNames;   //服务名称集合

    //构造器1
    public ConnectInfo(String serviceName, String host, int port, int weight) {
        this(new ConcurrentHashMap<String, Integer>(), host, port);
        this.serviceNames.put(serviceName, weight);
    }

    //构造器2
    private ConnectInfo(ConcurrentMap<String, Integer> serviceNames, String host, int port) {
        this.serviceNames = serviceNames;
        this.host = host;
        this.port = port;
    }

    //添加服务名称
    public void addServiceNames(Map<String, Integer> serviceNames) {
        if (!CollectionUtils.isEmpty(serviceNames)) {
            this.serviceNames.putAll(serviceNames);
        }
    }

    //获取服务名称
    public Map<String, Integer> getServiceNames() {
        return serviceNames;
    }

    //获取连接信息
    public String getConnect() {
        return NetUtils.toAddress(host, port);
    }

    //获取主机信息
    public String getHost() {
        return host;
    }

    //获取端口信息
    public int getPort() {
        return port;
    }

    //设置主机信息
    public void setHost(String host) {
        this.host = host;
    }

    //设置端口信息
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
