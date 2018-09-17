package com.dianping.pigeon.remoting;

import java.util.List;
import java.util.Map;
import com.dianping.pigeon.remoting.provider.publish.PublishPolicy;
import com.dianping.pigeon.remoting.provider.publish.PublishPolicyLoader;
import com.dianping.pigeon.util.ThriftUtils;
import org.apache.commons.lang.StringUtils;
import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.registry.exception.RegistryException;
import com.dianping.pigeon.remoting.common.exception.RpcException;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.invoker.concurrent.InvocationCallback;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.proxy.ServiceProxy;
import com.dianping.pigeon.remoting.invoker.proxy.ServiceProxyLoader;
import com.dianping.pigeon.remoting.provider.ProviderBootStrap;
import com.dianping.pigeon.remoting.provider.config.ProviderConfig;
import com.dianping.pigeon.remoting.provider.config.ServerConfig;
import com.dianping.pigeon.remoting.provider.publish.ServiceOnlineTask;
import com.dianping.pigeon.remoting.provider.publish.ServicePublisher;

//服务工厂
public class ServiceFactory {

	private static Logger logger = LoggerLoader.getLogger(ServiceFactory.class);
	private static ServiceProxy serviceProxy = ServiceProxyLoader.getServiceProxy();
	private static PublishPolicy publishPolicy = PublishPolicyLoader.getPublishPolicy();

	static {
		try {
			//初始化服务提供者引导程序
			ProviderBootStrap.init();
		} catch (Throwable t) {
			logger.error("error while initializing service factory:", t);
			System.exit(1);
		}
	}

	//获取全部服务调用者
	public static Map<InvokerConfig<?>, Object> getAllServiceInvokers() {
		return serviceProxy.getAllServiceInvokers();
	}

	//获取全部服务提供者
	public static Map<String, ProviderConfig<?>> getAllServiceProviders() {
		return ServicePublisher.getAllServiceProviders();
	}

	//根据服务接口获取url地址
	public static <T> String getServiceUrl(Class<T> serviceInterface) {
		//判断是否是Thrift接口
		if (ThriftUtils.isIDL(serviceInterface)) {
			return serviceInterface.getEnclosingClass().getName();
		} else {
			//如果不是，则直接将接口全限定名作为url
			return serviceInterface.getName();
		}
	}

	//根据调用者配置获取url地址
	public static <T> String getServiceUrl(InvokerConfig<T> invokerConfig) {
		return getServiceUrl(invokerConfig.getServiceInterface());
	}

	//根据提供者配置获取url地址
	public static <T> String getServiceUrl(ProviderConfig<T> providerConfig) {
		return getServiceUrl(providerConfig.getServiceInterface());
	}

	//获取服务提供者
	public static <T> T getService(Class<T> serviceInterface) throws RpcException {
		return getService(null, serviceInterface);
	}

	//获取服务提供者
	public static <T> T getService(Class<T> serviceInterface, int timeout) throws RpcException {
		InvokerConfig<T> invokerConfig = new InvokerConfig<T>(serviceInterface);
		invokerConfig.setTimeout(timeout);
		return getService(invokerConfig);
	}

	//获取服务提供者
	public static <T> T getService(Class<T> serviceInterface, InvocationCallback callback) throws RpcException {
		InvokerConfig<T> invokerConfig = new InvokerConfig<T>(serviceInterface);
		invokerConfig.setCallback(callback);
		return getService(invokerConfig);
	}

	//获取服务提供者
	public static <T> T getService(Class<T> serviceInterface, InvocationCallback callback, int timeout)
			throws RpcException {
		InvokerConfig<T> invokerConfig = new InvokerConfig<T>(serviceInterface);
		invokerConfig.setCallback(callback);
		invokerConfig.setTimeout(timeout);
		return getService(invokerConfig);
	}

	//获取服务提供者
	public static <T> T getService(String url, Class<T> serviceInterface) throws RpcException {
		InvokerConfig<T> invokerConfig = new InvokerConfig<T>(url, serviceInterface);
		return getService(invokerConfig);
	}

	//获取服务提供者
	public static <T> T getService(String url, Class<T> serviceInterface, int timeout) throws RpcException {
		InvokerConfig<T> invokerConfig = new InvokerConfig<T>(url, serviceInterface);
		invokerConfig.setTimeout(timeout);
		return getService(invokerConfig);
	}

	//获取服务提供者
	public static <T> T getService(String url, Class<T> serviceInterface, InvocationCallback callback) throws RpcException {
		return getService(url, serviceInterface, callback, Constants.DEFAULT_INVOKER_TIMEOUT);
	}

	//获取服务提供者
	public static <T> T getService(String url, Class<T> serviceInterface, InvocationCallback callback, int timeout)
			throws RpcException {
		InvokerConfig<T> invokerConfig = new InvokerConfig<T>(url, serviceInterface);
		invokerConfig.setTimeout(timeout);
		invokerConfig.setCallback(callback);
		return getService(invokerConfig);
	}

	//获取服务提供者
	public static <T> T getService(InvokerConfig<T> invokerConfig) throws RpcException {
		return serviceProxy.getProxy(invokerConfig);
	}

	//启动服务
	public static void startupServer(ServerConfig serverConfig) throws RpcException {
		// ProviderBootStrap.setServerConfig(serverConfig);
		// ProviderBootStrap.startup(serverConfig);
	}

	//关闭服务
	public static void shutdownServer() throws RpcException {
		ProviderBootStrap.shutdown();
	}

	//添加服务
	public static <T> void addService(Class<T> serviceInterface, T service) throws RpcException {
		addService(null, serviceInterface, service, ServerConfig.DEFAULT_PORT);
	}

	//添加服务
	public static <T> void addService(String url, Class<T> serviceInterface, T service) throws RpcException {
		addService(url, serviceInterface, service, ServerConfig.DEFAULT_PORT);
	}

	//添加服务
	public static <T> void addService(String url, Class<T> serviceInterface, T service, int port) throws RpcException {
		ProviderConfig<T> providerConfig = new ProviderConfig<T>(serviceInterface, service);
		providerConfig.setUrl(url);
		providerConfig.getServerConfig().setPort(port);
		addService(providerConfig);
	}

	//添加服务
	public static <T> void addService(ProviderConfig<T> providerConfig) throws RpcException {
		publishPolicy.doAddService(providerConfig);
	}

	//添加服务
	public static void addServices(List<ProviderConfig<?>> providerConfigList) throws RpcException {
		if (logger.isInfoEnabled()) {
			logger.info("add services:" + providerConfigList);
		}
		if (providerConfigList != null && !providerConfigList.isEmpty()) {
			for (ProviderConfig<?> providerConfig : providerConfigList) {
				addService(providerConfig);
			}
		}
	}

	//发布服务到注册中心
	public static <T> void publishService(ProviderConfig<T> providerConfig) throws RpcException {
		if (StringUtils.isBlank(providerConfig.getUrl())) {
			providerConfig.setUrl(getServiceUrl(providerConfig));
		}
		try {
			ServicePublisher.publishService(providerConfig, true);
		} catch (RegistryException t) {
			throw new RpcException("error while publishing service:" + providerConfig, t);
		}
	}

	//发布服务到注册中心
	public static <T> void publishService(String url) throws RpcException {
		try {
			ServicePublisher.publishService(url);
		} catch (RegistryException t) {
			throw new RpcException("error while publishing service:" + url, t);
		}
	}

	//取消发布
	public static <T> void unpublishService(ProviderConfig<T> providerConfig) throws RpcException {
		try {
			ServicePublisher.unpublishService(providerConfig);
		} catch (RegistryException e) {
			throw new RpcException("error while unpublishing service:" + providerConfig, e);
		}
	}

	//取消发布
	public static <T> void unpublishService(String url) throws RpcException {
		try {
			ServicePublisher.unpublishService(url);
		} catch (RegistryException e) {
			throw new RpcException("error while unpublishing service:" + url, e);
		}
	}

	//取消发布所有服务
	public static void unpublishAllServices() throws RpcException {
		try {
			ServicePublisher.unpublishAllServices();
		} catch (RegistryException e) {
			throw new RpcException("error while unpublishing all services", e);
		}
	}

	//发布所有服务
	public static void publishAllServices() throws RpcException {
		try {
			ServicePublisher.publishAllServices();
		} catch (RegistryException e) {
			throw new RpcException("error while publishing all services", e);
		}
	}

	//移除所有服务
	public static void removeAllServices() throws RpcException {
		try {
			ServicePublisher.removeAllServices();
		} catch (RegistryException e) {
			throw new RpcException("error while removing all services", e);
		}
	}

	//移除所有服务
	public static void removeService(String url) throws RpcException {
		try {
			ServicePublisher.removeService(url);
		} catch (RegistryException e) {
			throw new RpcException("error while removing service:" + url, e);
		}
	}

	//移除所有服务
	public static <T> void removeService(ProviderConfig<T> providerConfig) throws RpcException {
		removeService(providerConfig.getUrl());
	}

	public static ProviderConfig<?> getServiceConfig(String url) {
		return ServicePublisher.getServiceConfig(url);
	}

	public static void setServerWeight(int weight) throws RegistryException {
		logger.info("set weight:" + weight);
		ServicePublisher.setServerWeight(weight);
	}

	public static void online() throws RegistryException {
		logger.info("online");
		ServicePublisher.setServerWeight(Constants.WEIGHT_DEFAULT);
	}

	public static void offline() throws RegistryException {
		logger.info("offline");
		ServiceOnlineTask.stop();
		ServicePublisher.setServerWeight(0);
	}

	public static boolean isAutoPublish() {
		return ServicePublisher.isAutoPublish();
	}

}
