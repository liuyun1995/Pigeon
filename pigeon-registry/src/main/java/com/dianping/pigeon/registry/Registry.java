package com.dianping.pigeon.registry;

import java.util.List;
import java.util.Map;
import com.dianping.pigeon.registry.config.RegistryConfig;
import com.dianping.pigeon.registry.exception.RegistryException;

//服务注册中心接口
public interface Registry {

    //初始化方法
    void init();

    //是否可用
    boolean isEnable();

    //获取名称
    String getName();

    //-------------------------------------------------服务调用者方法-------------------------------------------------

    // for invoker
    String getServiceAddress(String serviceName) throws RegistryException;

    // for invoker
    String getServiceAddress(String serviceName, String group) throws RegistryException;

    // for invoker
    String getServiceAddress(String serviceName, String group, boolean fallbackDefaultGroup) throws RegistryException;

    // for invoker
    String getServiceAddress(String remoteAppkey, String serviceName, String group,
                             boolean fallbackDefaultGroup) throws RegistryException;

    // for invoker
    int getServerWeight(String serverAddress, String serviceName) throws RegistryException;

    // for invoker
    String getServerApp(String serverAddress, String serviceName) throws RegistryException;

    // for invoker
    String getServerVersion(String serverAddress, String serviceName) throws RegistryException;

    // for invoker
    byte getServerHeartBeatSupport(String serviceAddress, String serviceName) throws RegistryException;

    // for invoker
    Map<String,Boolean> getServiceProtocols(String serviceAddress, String serviceName) throws RegistryException;

    // for invoker
    boolean isSupportNewProtocol(String serviceAddress) throws RegistryException;

    // for invoker
    boolean isSupportNewProtocol(String serviceAddress, String serviceName) throws RegistryException;

    //-------------------------------------------------服务提供者方法-------------------------------------------------

    // for provider
    void registerService(String serviceName, String group, String serviceAddress, int weight) throws RegistryException;

    // for provider
    void unregisterService(String serviceName, String serviceAddress) throws RegistryException;

    // for provider
    void unregisterService(String serviceName, String group, String serviceAddress) throws RegistryException;

    // for provider
    void setServerWeight(String serverAddress, int weight) throws RegistryException;


    // for provider
    void setServerApp(String serverAddress, String app);

    // for provider
    void unregisterServerApp(String serverAddress);


    // for provider
    void setServerVersion(String serverAddress, String version);

    // for provider
    void unregisterServerVersion(String serverAddress);

    // for provider
    void setSupportNewProtocol(String serviceAddress, String serviceName, boolean support) throws RegistryException;

    // for provider
    void unregisterSupportNewProtocol(String serviceAddress, String serviceName, boolean support) throws RegistryException;

    // for provider
    void updateHeartBeat(String serviceAddress, Long heartBeatTimeMillis);

    // for provider
    void deleteHeartBeat(String serviceAddress);

    //-------------------------------------------------服务管理员方法-------------------------------------------------

    // for governor
    void setServerService(String serviceName, String group, String hosts) throws RegistryException;

    // for governor
    void delServerService(String serviceName, String group) throws RegistryException;

    // for governor
    void setHostsWeight(String serviceName, String group, String hosts, int weight) throws RegistryException;

    // for governor
    String getServiceAddress(String remoteAppkey, String serviceName, String group,
                             boolean fallbackDefaultGroup, boolean needListener) throws RegistryException;

    // for governor
    String getServiceAddress(String serviceName, String group,
                             boolean fallbackDefaultGroup, boolean needListener) throws RegistryException;


    String getStatistics();

    List<String> getChildren(String key) throws RegistryException;

    void setConsoleAddress(String consoleAddress);

    void unregisterConsoleAddress(String consoleAddress);

    List<String> getConsoleAddresses();

    // for invoker/provider
    RegistryConfig getRegistryConfig(String ip) throws RegistryException;
}
