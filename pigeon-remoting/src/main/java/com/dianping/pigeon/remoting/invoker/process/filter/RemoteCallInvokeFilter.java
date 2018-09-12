package com.dianping.pigeon.remoting.invoker.process.filter;

import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.monitor.MonitorLoader;
import com.dianping.pigeon.monitor.MonitorTransaction;
import com.dianping.pigeon.remoting.common.monitor.trace.InvokerMonitorData;
import com.dianping.pigeon.remoting.common.domain.CallMethod;
import com.dianping.pigeon.remoting.common.domain.InvocationContext.TimePhase;
import com.dianping.pigeon.remoting.common.domain.InvocationContext.TimePoint;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.exception.BadRequestException;
import com.dianping.pigeon.remoting.common.process.ServiceInvocationHandler;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.concurrent.CallbackFuture;
import com.dianping.pigeon.remoting.invoker.concurrent.FutureFactory;
import com.dianping.pigeon.remoting.invoker.concurrent.InvocationCallback;
import com.dianping.pigeon.remoting.invoker.concurrent.ServiceCallbackWrapper;
import com.dianping.pigeon.remoting.invoker.concurrent.ServiceFutureImpl;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.domain.DefaultInvokerContext;
import com.dianping.pigeon.remoting.invoker.domain.InvokerContext;
import com.dianping.pigeon.remoting.invoker.route.region.Region;
import com.dianping.pigeon.remoting.invoker.util.InvokerHelper;
import com.dianping.pigeon.remoting.invoker.util.InvokerUtils;

/**
 * 远程调用过滤器
 * 执行实际的Remote Call，包括Sync, Future，Callback，Oneway
 * @author danson.liu
 */
public class RemoteCallInvokeFilter extends InvocationInvokeFilter {

    private static final Logger logger = LoggerLoader.getLogger(RemoteCallInvokeFilter.class);
    private static final InvocationResponse NO_RETURN_RESPONSE = InvokerUtils.createNoReturnResponse();

    //调用方法
    @Override
    public InvocationResponse invoke(ServiceInvocationHandler handler, InvokerContext invocationContext)
            throws Throwable {
        //记录时间点
        invocationContext.getTimeline().add(new TimePoint(TimePhase.Q));
        //获取客户端
        Client client = invocationContext.getClient();
        //获取调用请求
        InvocationRequest request = invocationContext.getRequest();
        //获取调用配置
        InvokerConfig<?> invokerConfig = invocationContext.getInvokerConfig();
        //获取目标调用方法代码
        byte callMethodCode = invokerConfig.getCallMethod(invocationContext.getMethodName());
        //调用前预处理
        beforeInvoke(invocationContext);
        //判断是否取消调用
        boolean isCancel = InvokerHelper.getCancel();
        if (isCancel) {
            return InvokerUtils.createDefaultResponse(InvokerHelper.getDefaultResult());
        }
        InvocationResponse response = null;
        Integer timeoutThreadLocal = InvokerHelper.getTimeout();
        if (timeoutThreadLocal != null) {
            request.setTimeout(timeoutThreadLocal.intValue());
        }

        MonitorTransaction transaction = MonitorLoader.getMonitor().getCurrentCallTransaction();
        if (transaction != null) {
            transaction.addData("CurrentTimeout", request.getTimeout());
        }

        //获取调用方法
        CallMethod callMethod = CallMethod.getCallMethod(callMethodCode);
        //获取监控数据
        InvokerMonitorData monitorData = (InvokerMonitorData) invocationContext.getMonitorData();

        if (monitorData != null) {
            monitorData.setCallMethod(callMethodCode);
            monitorData.setSerialize(request.getSerialize());
            monitorData.setTimeout(request.getTimeout());

            Region region = client.getRegion();

            monitorData.setRegion(region == null ? null : region.getName());
            monitorData.add();
        }

        try {
            switch (callMethod) {
                //同步调用
                case SYNC:
                    CallbackFuture future = new CallbackFuture();
                    //发送请求
                    response = InvokerUtils.sendRequest(client, invocationContext.getRequest(), future);
                    invocationContext.getTimeline().add(new TimePoint(TimePhase.Q));
                    if (response == null) {
                        response = future.getResponse(request.getTimeout());
                    }
                    break;
                //异步调用
                case CALLBACK:
                    InvocationCallback callback = invokerConfig.getCallback();
                    InvocationCallback tlCallback = InvokerHelper.getCallback();
                    if (tlCallback != null) {
                        callback = tlCallback;
                        InvokerHelper.clearCallback();
                    }
                    InvokerUtils.sendRequest(client, invocationContext.getRequest(), new ServiceCallbackWrapper(
                            invocationContext, callback));
                    response = NO_RETURN_RESPONSE;
                    invocationContext.getTimeline().add(new TimePoint(TimePhase.Q));
                    break;
                //未来执行
                case FUTURE:
                    ServiceFutureImpl futureImpl = new ServiceFutureImpl(invocationContext, request.getTimeout());
                    InvokerUtils.sendRequest(client, invocationContext.getRequest(), futureImpl);
                    FutureFactory.setFuture(futureImpl);
                    response = InvokerUtils.createFutureResponse(futureImpl);
                    invocationContext.getTimeline().add(new TimePoint(TimePhase.Q));
                    break;
                //一次性调用
                case ONEWAY:
                    InvokerUtils.sendRequest(client, invocationContext.getRequest(), null);
                    response = NO_RETURN_RESPONSE;
                    invocationContext.getTimeline().add(new TimePoint(TimePhase.Q));
                    break;
                default:
                    throw new BadRequestException("Call type[" + callMethod.getName() + "] is not supported!");

            }

            ((DefaultInvokerContext) invocationContext).setResponse(response);
            //调用后执行方法
            afterInvoke(invocationContext);
        } catch (Throwable t) {
            //抛异常前执行方法
            afterThrowing(invocationContext, t);
            throw t;
        }
        //返回调用结果
        return response;
    }

}
