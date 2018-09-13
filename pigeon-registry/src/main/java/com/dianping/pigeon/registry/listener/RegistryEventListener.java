package com.dianping.pigeon.registry.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.registry.RegistryManager;
import com.dianping.pigeon.registry.config.RegistryConfig;

/**
 * 注册事件监听器
 * 将lion推送的动态服务信息发送到感兴趣的listener
 * @author marsqing
 * 
 */
public class RegistryEventListener {

	private static final Logger logger = LoggerLoader.getLogger(RegistryEventListener.class);

	private static List<ServiceProviderChangeListener> serviceProviderChangeListeners = new ArrayList<ServiceProviderChangeListener>();

	private static List<RegistryConnectionListener> registryConnectionListeners = new ArrayList<RegistryConnectionListener>();

	private static List<ServerInfoListener> serverInfoListeners = new ArrayList<ServerInfoListener>();

	private static List<GroupChangeListener> groupChangeListeners = new ArrayList<GroupChangeListener>();

	//添加服务提供者改变监听者
	public synchronized static void addListener(ServiceProviderChangeListener listener) {
		serviceProviderChangeListeners.add(listener);
	}

	//移除服务提供者改变监听者
	public synchronized static void removeListener(ServiceProviderChangeListener listener) {
		serviceProviderChangeListeners.remove(listener);
	}

	//添加注册连接监听者
	public synchronized static void addListener(RegistryConnectionListener listener) {
		registryConnectionListeners.add(listener);
	}

	//添加服务信息监听者
	public synchronized static void addListener(ServerInfoListener listener) {
		serverInfoListeners.add(listener);
	}

	//添加集群改变监听者
	public synchronized static void addListener(GroupChangeListener listener) {
		groupChangeListeners.add(listener);
	}

	//提供者移除事件
	public static void providerRemoved(String serviceName, String host, int port) {
		List<ServiceProviderChangeListener> listeners = new ArrayList<ServiceProviderChangeListener>();
		listeners.addAll(serviceProviderChangeListeners);
		for (ServiceProviderChangeListener listener : listeners) {
			listener.providerRemoved(new ServiceProviderChangeEvent(serviceName, host, port, -1));
		}
	}

	//提供者添加事件
	public static void providerAdded(String serviceName, String host, int port, int weight) {
		List<ServiceProviderChangeListener> listeners = new ArrayList<ServiceProviderChangeListener>();
		listeners.addAll(serviceProviderChangeListeners);
		for (ServiceProviderChangeListener listener : listeners) {
			ServiceProviderChangeEvent event = new ServiceProviderChangeEvent(serviceName, host, port, weight);
			listener.providerAdded(event);
		}
	}

	//主机权重改变事件
	public static void hostWeightChanged(String host, int port, int weight) {
		List<ServiceProviderChangeListener> listeners = new ArrayList<ServiceProviderChangeListener>();
		listeners.addAll(serviceProviderChangeListeners);
		for (ServiceProviderChangeListener listener : listeners) {
			listener.hostWeightChanged(new ServiceProviderChangeEvent(null, host, port, weight));
		}
	}

	public static void connectionReconnected() {
		for (RegistryConnectionListener listener : registryConnectionListeners) {
			listener.reconnected();
		}
	}

	public static void serverAppChanged(String serverAddress, String app) {
		for (ServerInfoListener listener : serverInfoListeners) {
			listener.onServerAppChange(serverAddress, app);
		}
	}

	//服务版本改变事件
	public static void serverVersionChanged(String serverAddress, String version) {
		for (ServerInfoListener listener : serverInfoListeners) {
			listener.onServerVersionChange(serverAddress, version);
		}
	}

	public static void serverHeartBeatSupportChanged(String serverAddress, byte heartBeatSupport) {
		for (ServerInfoListener listener : serverInfoListeners) {
			listener.onServerHeartBeatSupportChange(serverAddress, heartBeatSupport);
		}
	}

	//服务协议改变事件
	public static void serverProtocolChanged(String serverAddress, Map<String, Boolean> protocolInfoMap) {
		for (ServerInfoListener listener : serverInfoListeners) {
			listener.onServerProtocolChange(serverAddress, protocolInfoMap);
		}
	}

	//服务信息改变事件
	public static void serverInfoChanged(String serviceName, String serverAddress) {
		RegistryManager.getInstance().getReferencedApp(serverAddress, serviceName);
		RegistryManager.getInstance().getReferencedVersion(serverAddress, serviceName);
		RegistryManager.getInstance().getServerHeartBeatSupport(serverAddress, serviceName);
		RegistryManager.getInstance().getReferencedProtocol(serverAddress, serviceName);
	}

	//注册配置改变事件
	public static void registryConfigChanged(String ip, RegistryConfig oldRegistryConfig, RegistryConfig newRegistryConfig) {
		for (GroupChangeListener listener : groupChangeListeners) {
			listener.onGroupChange(ip, oldRegistryConfig, newRegistryConfig);
		}
	}

}
