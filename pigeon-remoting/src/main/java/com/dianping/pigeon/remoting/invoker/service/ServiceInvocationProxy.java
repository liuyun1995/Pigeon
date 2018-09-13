package com.dianping.pigeon.remoting.invoker.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.exception.BadResponseException;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationHandler;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.domain.DefaultInvokerContext;
import com.dianping.pigeon.remoting.invoker.util.InvokerUtils;

//服务调用代理类
public class ServiceInvocationProxy implements InvocationHandler {

	private static final Logger logger = LoggerLoader.getLogger(ServiceInvocationProxy.class);
	private InvokerConfig<?> invokerConfig;     //调用配置
	private ServiceInvocationHandler handler;   //服务调用处理器

	//构造器
	public ServiceInvocationProxy(InvokerConfig<?> invokerConfig, ServiceInvocationHandler handler) {
		this.invokerConfig = invokerConfig;
		this.handler = handler;
	}

	//调用方法
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (method.getDeclaringClass() == Object.class) {
			return method.invoke(handler, args);
		}
		if ("toString".equals(methodName) && parameterTypes.length == 0) {
			return handler.toString();
		}
		if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
			return handler.hashCode();
		}
		if ("equals".equals(methodName) && parameterTypes.length == 1) {
			return handler.equals(args[0]);
		}
		//新建调用上下文，使用调用处理器进行调用，此后会执行一连串过滤器
		return extractResult(handler.handle(new DefaultInvokerContext(invokerConfig, methodName, parameterTypes, args)),
				method.getReturnType());
	}

	//检验响应对象
	public Object extractResult(InvocationResponse response, Class<?> returnType) throws Throwable {
		//获取响应返回对象
		Object responseReturn = response.getReturn();
		//若响应返回对象不为空
		if (responseReturn != null) {
			//获取消息类型
			int messageType = response.getMessageType();
			//若是服务正确返回消息
			if (messageType == Constants.MESSAGE_TYPE_SERVICE) {
				//则直接返回该对象
				return responseReturn;
			} else if (messageType == Constants.MESSAGE_TYPE_EXCEPTION) {
				throw InvokerUtils.toRpcException(response);
			} else if (messageType == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
				throw InvokerUtils.toApplicationException(response);
			}
			throw new BadResponseException(response.toString());
		}
		return getReturn(returnType);
	}

	private Object getReturn(Class<?> returnType) {
		if (returnType == byte.class) {
			return (byte) 0;
		} else if (returnType == short.class) {
			return (short) 0;
		} else if (returnType == int.class) {
			return 0;
		} else if (returnType == boolean.class) {
			return false;
		} else if (returnType == long.class) {
			return 0l;
		} else if (returnType == float.class) {
			return 0.0f;
		} else if (returnType == double.class) {
			return 0.0d;
		} else {
			return null;
		}
	}

}
