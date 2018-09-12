package com.dianping.pigeon.remoting.invoker.domain;

import com.dianping.pigeon.remoting.common.monitor.trace.InvokerMonitorData;
import com.dianping.pigeon.remoting.common.domain.InvocationContext;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;

//调用者上下文
public interface InvokerContext<M extends InvokerMonitorData> extends InvocationContext<M> {

    //获取调用者配置
    InvokerConfig<?> getInvokerConfig();

    //获取方法名称
    String getMethodName();

    //获取参数类型集合
    Class<?>[] getParameterTypes();

    //获取参数集合
    Object[] getArguments();

    //获取客户端
    Client getClient();

    //设置客户端
    void setClient(Client client);

    DegradeInfo getDegradeInfo();

}
