package com.dianping.pigeon.remoting.common.codec;

import java.lang.reflect.Proxy;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.exception.SerializationException;
import com.dianping.pigeon.remoting.common.util.InvocationUtils;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.domain.InvokerContext;
import com.dianping.pigeon.remoting.invoker.process.InvokerProcessHandlerFactory;
import com.dianping.pigeon.remoting.invoker.service.ServiceInvocationProxy;
import com.dianping.pigeon.util.ClassUtils;

//抽象的序列化器
public abstract class AbstractSerializer implements Serializer {

	//根据调用配置获取代理请求对象
	@Override
	public Object proxyRequest(InvokerConfig<?> invokerConfig) throws SerializationException {
		//使用jdk动态代理
		return Proxy.newProxyInstance(ClassUtils.getCurrentClassLoader(invokerConfig.getClassLoader()),
				new Class[] { invokerConfig.getServiceInterface() }, new ServiceInvocationProxy(invokerConfig,
						//通过工厂获取调用处理器
						InvokerProcessHandlerFactory.selectInvocationHandler(invokerConfig)));
	}

	@Override
	public InvocationResponse newResponse() throws SerializationException {
		return InvocationUtils.newResponse();
	}

	@Override
	public InvocationRequest newRequest(InvokerContext invokerContext) throws SerializationException {
		return InvocationUtils.newRequest(invokerContext);
	}
}
