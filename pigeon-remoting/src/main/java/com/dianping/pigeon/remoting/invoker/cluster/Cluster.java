package com.dianping.pigeon.remoting.invoker.cluster;

import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationHandler;
import com.dianping.pigeon.remoting.invoker.domain.InvokerContext;

//集群接口
public interface Cluster {

	public String getName();

	public InvocationResponse invoke(final ServiceInvocationHandler handler, final InvokerContext invocationContext)
			throws Throwable;

}
