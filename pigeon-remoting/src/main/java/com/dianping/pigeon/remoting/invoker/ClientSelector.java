package com.dianping.pigeon.remoting.invoker;

import java.util.List;
import com.dianping.pigeon.extension.ExtensionLoader;
import com.dianping.pigeon.remoting.invoker.domain.ConnectInfo;
import com.dianping.pigeon.remoting.invoker.exception.ServiceUnavailableException;

//客户端选择器
public class ClientSelector {

	//从连接信息中选出客户端
	public static Client selectClient(ConnectInfo connectInfo) {
		//获取客户端工厂列表
		List<ClientFactory> clientFactories = ExtensionLoader.getExtensionList(ClientFactory.class);
		//遍历客户端工厂
		for (ClientFactory clientFactory : clientFactories) {
			if (clientFactory.support(connectInfo)) {
				//从连接信息中创建客户端
				return clientFactory.createClient(connectInfo);
			}
		}
		throw new ServiceUnavailableException("no available client been created from client factory:" + connectInfo);
	}
}
