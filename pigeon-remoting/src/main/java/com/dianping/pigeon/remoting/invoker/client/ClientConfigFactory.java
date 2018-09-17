package com.dianping.pigeon.remoting.invoker.client;

import com.dianping.pigeon.config.ConfigManager;

//客户端配置工厂
public class ClientConfigFactory {

    public static volatile ClientConfig clientConfig;

    public static ClientConfig createClientConfig(ConfigManager configManager) {
        if (clientConfig == null) {
            synchronized (ClientConfigFactory.class) {
                if (clientConfig == null) {
                    clientConfig = new ClientConfig(configManager);
                }
            }
        }
        return clientConfig;
    }

}
