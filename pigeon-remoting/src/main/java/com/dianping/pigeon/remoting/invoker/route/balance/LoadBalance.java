package com.dianping.pigeon.remoting.invoker.route.balance;

import java.util.List;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;

//负载均衡策略接口
public interface LoadBalance {

	Client select(List<Client> clients, InvokerConfig<?> invokerConfig, InvocationRequest request);

}
