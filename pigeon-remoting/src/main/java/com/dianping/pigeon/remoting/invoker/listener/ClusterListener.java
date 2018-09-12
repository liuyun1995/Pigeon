package com.dianping.pigeon.remoting.invoker.listener;

import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.domain.ConnectInfo;

//集群监听器
public interface ClusterListener {

	//添加连接
	void addConnect(ConnectInfo cmd, String serviceName);

	//移除连接
	void removeConnect(Client client);

	//标志不被使用
	void doNotUse(String serviceName, String host, int port);

}
