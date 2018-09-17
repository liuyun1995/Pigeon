package com.dianping.pigeon.remoting.common.process;

import com.dianping.pigeon.remoting.common.domain.InvocationContext;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;

public interface ServiceInvocationFilter<I extends InvocationContext> {

	//调用方法
	InvocationResponse invoke(ServiceInvocationHandler handler, I invocationContext) throws Throwable;

}
