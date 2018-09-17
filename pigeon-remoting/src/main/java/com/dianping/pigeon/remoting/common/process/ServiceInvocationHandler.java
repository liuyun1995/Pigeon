package com.dianping.pigeon.remoting.common.process;

import com.dianping.pigeon.remoting.common.domain.InvocationContext;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;

//服务调用处理器
public interface ServiceInvocationHandler {

	//处理方法
	InvocationResponse handle(InvocationContext invocationContext) throws Throwable;

}
