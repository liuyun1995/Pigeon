package com.dianping.pigeon.remoting.invoker.proxy;

import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.remoting.ServiceFactory;
import com.dianping.pigeon.remoting.common.codec.SerializerFactory;
import com.dianping.pigeon.remoting.common.exception.RpcException;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.invoker.ClientManager;
import com.dianping.pigeon.remoting.invoker.InvokerBootStrap;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.exception.RouteException;
import com.dianping.pigeon.remoting.invoker.route.balance.LoadBalanceManager;
import com.dianping.pigeon.remoting.invoker.route.region.RegionPolicyManager;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import org.apache.commons.lang.StringUtils;
import com.dianping.pigeon.log.Logger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//抽象服务代理
public abstract class AbstractServiceProxy implements ServiceProxy {

    protected Logger logger = LoggerLoader.getLogger(this.getClass());

    //服务提供者对象映射
    protected final static Map<InvokerConfig<?>, Object> services = new ConcurrentHashMap<InvokerConfig<?>, Object>();

    private static final Interner<InvokerConfig<?>> interner = Interners.newWeakInterner();

    private final RegionPolicyManager regionPolicyManager = RegionPolicyManager.INSTANCE;

    @Override
    public void init() {}

    //获取代理对象
    @Override
    public <T> T getProxy(InvokerConfig<T> invokerConfig) {
        //如果服务提供者接口为空，则抛出异常
        if (invokerConfig.getServiceInterface() == null) {
            throw new IllegalArgumentException("service interface is required");
        }
        //若服务提供者的url为空，根据接口名称获取url
        if (StringUtils.isBlank(invokerConfig.getUrl())) {
            invokerConfig.setUrl(ServiceFactory.getServiceUrl(invokerConfig));
        }
        //如果网络协议不为空
        if (!StringUtils.isBlank(invokerConfig.getProtocol())
                && !invokerConfig.getProtocol().equalsIgnoreCase(Constants.PROTOCOL_DEFAULT)) {
            String protocolPrefix = "@" + invokerConfig.getProtocol().toUpperCase() + "@";
            if (!invokerConfig.getUrl().startsWith(protocolPrefix)) {
                invokerConfig.setUrl(protocolPrefix + invokerConfig.getUrl());
            }
        }
        //从缓存中获取服务代理对象
        Object service = null;
        service = services.get(invokerConfig);
        //若服务代理对象为空
        if (service == null) {
            synchronized (interner.intern(invokerConfig)) {
                service = services.get(invokerConfig);
                if (service == null) {
                    try {
                        //启动调用者引导程序
                        InvokerBootStrap.startup();
                        //获取提供者代理对象
                        service = SerializerFactory.getSerializer(invokerConfig.getSerialize()).proxyRequest(invokerConfig);
                        //若负载均衡策略不为空，则注册负载均衡策略
                        if (StringUtils.isNotBlank(invokerConfig.getLoadbalance())) {
                            LoadBalanceManager.register(invokerConfig.getUrl(), invokerConfig.getSuffix(),
                                    invokerConfig.getLoadbalance());
                        }
                    } catch (Throwable t) {
                        throw new RpcException("error while trying to get service:" + invokerConfig, t);
                    }

                    // setup region policy for service
                    try {
                        regionPolicyManager.register(invokerConfig.getUrl(), invokerConfig.getSuffix(),
                                invokerConfig.getRegionPolicy());
                    } catch (Throwable t) {
                        throw new RouteException("error while setup region route policy: " + invokerConfig, t);
                    }

                    try {
                        //根据配置信息注册服务提供者机器
                        ClientManager.getInstance().registerClients(invokerConfig);
                    } catch (Throwable t) {
                        logger.warn("error while trying to setup service client:" + invokerConfig, t);
                    }
                    services.put(invokerConfig, service);
                }
            }
        }
        return (T) service;
    }

    @Override
    public Map<InvokerConfig<?>, Object> getAllServiceInvokers() {
        return services;
    }
}
