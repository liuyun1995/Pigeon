package com.dianping.pigeon.remoting.provider;

import java.util.List;
import java.util.concurrent.Future;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.provider.config.ProviderConfig;
import com.dianping.pigeon.remoting.provider.config.ServerConfig;
import com.dianping.pigeon.remoting.provider.domain.ProviderContext;
import com.dianping.pigeon.remoting.provider.process.RequestProcessor;

//服务提供者接口
public interface Server {

	//是否启动服务
	public boolean isStarted();

	//是否支持服务配置
	public boolean support(ServerConfig serverConfig);

	//开启服务
	public void start(ServerConfig serverConfig);

	//关闭服务
	public void stop();

	//获取服务配置
	public ServerConfig getServerConfig();

	//获取端口
	public int getPort();

	//获取注册的URL
	public String getRegistryUrl(String url);

	//处理请求
	public Future<InvocationResponse> processRequest(final InvocationRequest request,
			final ProviderContext providerContext);

	//添加服务
	public <T> void addService(ProviderConfig<T> providerConfig);

	//移除服务
	public <T> void removeService(ProviderConfig<T> providerConfig);

	//获取调用者元信息
	public List<String> getInvokerMetaInfo();

	//获取网络协议
	public String getProtocol();

	//获取请求处理者
	public RequestProcessor getRequestProcessor();

}
