package com.dianping.pigeon.remoting.provider;

import com.dianping.pigeon.extension.ExtensionLoader;
import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.registry.RegistryManager;
import com.dianping.pigeon.registry.exception.RegistryException;
import com.dianping.pigeon.remoting.common.codec.SerializerFactory;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.provider.config.ProviderConfig;
import com.dianping.pigeon.remoting.provider.config.ServerConfig;
import com.dianping.pigeon.remoting.provider.listener.ShutdownHookListener;
import com.dianping.pigeon.remoting.provider.process.ProviderProcessHandlerFactory;
import com.dianping.pigeon.remoting.provider.publish.ServicePublisher;
import com.dianping.pigeon.util.ClassUtils;
import com.dianping.pigeon.util.NetUtils;
import com.dianping.pigeon.util.VersionUtils;

import java.util.*;

//服务提供者引导器
public final class ProviderBootStrap {

    private static Logger logger = LoggerLoader.getLogger(ServicePublisher.class);     //日志类
    static Server httpServer = null;                                                   //HTTP服务类
    static volatile Map<String, Server> serversMap = new HashMap<String, Server>();    //服务类映射
    static volatile boolean isInitialized = false;                                     //是否已经实例化
    static Date startTime = new Date();                                                //开始时间

    //获取开始时间
    public static Date getStartTime() {
        return startTime;
    }

    //初始化方法
    public static void init() {
        if (!isInitialized) {
            synchronized (ProviderBootStrap.class) {
                if (!isInitialized) {
                    //初始化服务处理器工厂
                    ProviderProcessHandlerFactory.init();
                    //初始化序列化工厂
                    SerializerFactory.init();
                    ClassUtils.loadClasses("com.dianping.pigeon");
                    //开启服务关闭监控线程
                    Thread shutdownHook = new Thread(new ShutdownHookListener());
                    shutdownHook.setDaemon(true);
                    shutdownHook.setPriority(Thread.MAX_PRIORITY);
                    Runtime.getRuntime().addShutdownHook(shutdownHook);
                    //新建服务配置器
                    ServerConfig config = new ServerConfig();
                    //设置HTTP代理方式
                    config.setProtocol(Constants.PROTOCOL_HTTP);
                    RegistryManager.getInstance();
                    List<Server> servers = ExtensionLoader.getExtensionList(Server.class);
                    for (Server server : servers) {
                        if (!server.isStarted()) {
                            if (server.support(config)) {
                                server.start(config);
                                registerConsoleServer(config);
                                initRegistryConfig(config);

                                httpServer = server;
                                serversMap.put(server.getProtocol() + server.getPort(), server);
                                logger.warn("pigeon " + server + "[version:" + VersionUtils.VERSION + "] has been started");
                            }
                        }
                    }
                    isInitialized = true;
                }
            }
        }
    }

    //服务开启方法
    public static ServerConfig startup(ProviderConfig<?> providerConfig) {
        ServerConfig serverConfig = providerConfig.getServerConfig();
        if (serverConfig == null) {
            throw new IllegalArgumentException("server config is required");
        }
        Server server = serversMap.get(serverConfig.getProtocol() + serverConfig.getPort());
        if (server != null) {
            server.addService(providerConfig);
            return server.getServerConfig();
        } else {
            synchronized (ProviderBootStrap.class) {
                List<Server> servers = ExtensionLoader.newExtensionList(Server.class);
                for (Server s : servers) {
                    if (!s.isStarted()) {
                        if (s.support(serverConfig)) {
                            s.start(serverConfig);
                            s.addService(providerConfig);
                            serversMap.put(s.getProtocol() + serverConfig.getPort(), s);
                            logger.warn("pigeon " + s + "[version:" + VersionUtils.VERSION + "] has been started");
                            break;
                        }
                    }
                }
                server = serversMap.get(serverConfig.getProtocol() + serverConfig.getPort());
                if (server != null) {
                    server.getRequestProcessor().getRequestProcessThreadPool().prestartAllCoreThreads();
                    return server.getServerConfig();
                }
                return null;
            }
        }
    }

    //服务关闭方法
    public static void shutdown() {
        for (Server server : serversMap.values()) {
            if (server != null) {
                logger.info("start to stop " + server);
                try {
                    unregisterConsoleServer(server.getServerConfig());
                    server.stop();
                } catch (Throwable e) {
                }
                if (logger.isInfoEnabled()) {
                    logger.info(server + " has been shutdown");
                }
            }
        }
        try {
            ProviderProcessHandlerFactory.destroy();
        } catch (Throwable e) {
        }
    }

    public static List<Server> getServers(ProviderConfig<?> providerConfig) {
        List<Server> servers = new ArrayList<Server>();
        servers.add(httpServer);
        String protocol = providerConfig.getServerConfig().getProtocol();
        int port = providerConfig.getServerConfig().getPort();
        servers.add(serversMap.get(protocol + port));

        return servers;
    }

    public static Map<String, Server> getServersMap() {
        return serversMap;
    }

    public static Server getHttpServer() {
        return httpServer;
    }

    private static void initRegistryConfig(ServerConfig config) {
        try {
            RegistryManager.getInstance().initRegistryConfig(config.getIp());
        } catch (RegistryException e) {
            logger.warn("failed to init registry config, set config to blank, please check!", e);
        }
    }

    public static void registerConsoleServer(ServerConfig config) {
        RegistryManager.getInstance().setConsoleAddress(NetUtils.toAddress(config.getIp(), config.getHttpPort()));
    }

    public static void unregisterConsoleServer(ServerConfig config) {
        if (Constants.PROTOCOL_HTTP.equals(config.getProtocol())) {
            RegistryManager.getInstance().unregisterConsoleAddress(NetUtils.toAddress(config.getIp(), config.getHttpPort()));
        }
    }

}
