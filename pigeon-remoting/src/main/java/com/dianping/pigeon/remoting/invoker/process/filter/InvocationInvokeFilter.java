package com.dianping.pigeon.remoting.invoker.process.filter;

import java.util.List;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationFilter;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.invoker.domain.InvokerContext;
import com.dianping.pigeon.remoting.invoker.process.InvokerInterceptor;
import com.dianping.pigeon.remoting.invoker.process.InvokerInterceptorFactory;
import com.dianping.pigeon.remoting.invoker.process.InvokerProcessInterceptor;
import com.dianping.pigeon.remoting.invoker.process.InvokerProcessInterceptorFactory;

//调用过滤器
public abstract class InvocationInvokeFilter implements ServiceInvocationFilter<InvokerContext> {

	public void beforeInvoke(InvokerContext invokerContext) {
		InvocationRequest request = invokerContext.getRequest();
		if (request.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
			List<InvokerProcessInterceptor> interceptors = InvokerProcessInterceptorFactory.getInterceptors();
			for (InvokerProcessInterceptor interceptor : interceptors) {
				interceptor.preInvoke(request);
			}
			List<InvokerInterceptor> contextInterceptors = InvokerInterceptorFactory.getInterceptors();
			//遍历所有拦截器
			for (InvokerInterceptor interceptor : contextInterceptors) {
				//调用拦截器的预执行方法
				interceptor.preInvoke(invokerContext);
			}
		}
	}

	public void afterInvoke(InvokerContext invokerContext) {
		InvocationRequest request = invokerContext.getRequest();
		if (request.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
			List<InvokerProcessInterceptor> interceptors = InvokerProcessInterceptorFactory.getInterceptors();
			for (InvokerProcessInterceptor interceptor : interceptors) {
				interceptor.postInvoke(request, invokerContext.getResponse());
			}
			List<InvokerInterceptor> contextInterceptors = InvokerInterceptorFactory.getInterceptors();
			for (InvokerInterceptor interceptor : contextInterceptors) {
				interceptor.postInvoke(invokerContext);
			}
		}
	}

	public void afterThrowing(InvokerContext invokerContext, Throwable throwable) {
		InvocationRequest request = invokerContext.getRequest();
		if (request.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
			List<InvokerInterceptor> contextInterceptors = InvokerInterceptorFactory.getInterceptors();
			for (InvokerInterceptor interceptor : contextInterceptors) {
				interceptor.afterThrowing(invokerContext, throwable);
			}
		}
	}

}
