package com.dianping.pigeon.remoting.invoker.route;

import java.util.List;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;

//路由管理器接口
public interface RouteManager {

	public Client route(List<Client> clientList, InvokerConfig<?> invokerConfig, InvocationRequest request);

	public List<Client> getAvailableClients(List<Client> clientList, InvokerConfig<?> invokerConfig,
			InvocationRequest request);

}
