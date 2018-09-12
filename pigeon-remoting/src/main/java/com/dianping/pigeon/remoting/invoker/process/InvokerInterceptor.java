package com.dianping.pigeon.remoting.invoker.process;

import com.dianping.pigeon.remoting.invoker.domain.InvokerContext;

//调用者拦截器
public interface InvokerInterceptor {

	//调用之前执行
	public void preInvoke(InvokerContext invokerContext);

	//调用之后执行
	public void postInvoke(InvokerContext invokerContext);

	//报错后执行
	public void afterThrowing(InvokerContext invokerContext, Throwable throwable);

}
