package com.dianping.pigeon.remoting.invoker.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.dianping.pigeon.remoting.common.domain.InvocationContext;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationFilter;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationHandler;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.process.filter.*;

//调用过程处理器工厂
public final class InvokerProcessHandlerFactory {

	//内部过滤器集合
	private static List<InvocationInvokeFilter> bizProcessFilters = new LinkedList<InvocationInvokeFilter>();

	//调用过程处理器
	private static ServiceInvocationHandler bizInvocationHandler = null;

	//是否已经初始化
	private static volatile boolean isInitialized = false;

	//初始化方法
	public static void init() {
		if (!isInitialized) {
			//注册各种调用前的过滤器
			if (Constants.MONITOR_ENABLE) {
				registerBizProcessFilter(new RemoteCallMonitorInvokeFilter());
			}
			registerBizProcessFilter(new TraceFilter());
			registerBizProcessFilter(new FaultInjectionFilter());
			registerBizProcessFilter(new DegradationFilter());
			registerBizProcessFilter(new ClusterInvokeFilter());
			registerBizProcessFilter(new GatewayInvokeFilter());
			registerBizProcessFilter(new ContextPrepareInvokeFilter());
			registerBizProcessFilter(new SecurityFilter());
			registerBizProcessFilter(new RemoteCallInvokeFilter());
			bizInvocationHandler = createInvocationHandler(bizProcessFilters);
			isInitialized = true;
		}
	}

	//选取调用结果处理器
	public static ServiceInvocationHandler selectInvocationHandler(InvokerConfig<?> invokerConfig) {
		return bizInvocationHandler;
	}

	//创建调用过程处理器
	@SuppressWarnings({ "rawtypes" })
	private static <V extends ServiceInvocationFilter> ServiceInvocationHandler createInvocationHandler(
			List<V> internalFilters) {
		ServiceInvocationHandler last = null;
		List<V> filterList = new ArrayList<V>();
		filterList.addAll(internalFilters);
		//从后往前遍历过滤器
		for (int i = filterList.size() - 1; i >= 0; i--) {
			//获取调用前的过滤器
			final V filter = filterList.get(i);
			//设置上一个调用处理器
			final ServiceInvocationHandler next = last;
			//设置当前调用处理器
			last = new ServiceInvocationHandler() {
				@SuppressWarnings("unchecked")
				@Override
				public InvocationResponse handle(InvocationContext invocationContext) throws Throwable {
					//调用每个过滤器的invoke方法
					InvocationResponse resp = filter.invoke(next, invocationContext);
					return resp;
				}
			};
		}
		//此时调用处理器已经串成一条链
		return last;
	}

	//注册过滤器
	public static void registerBizProcessFilter(InvocationInvokeFilter filter) {
		bizProcessFilters.add(filter);
	}

	//清除所有过滤器
	public static void clearClientFilters() {
		bizProcessFilters.clear();
	}

}
