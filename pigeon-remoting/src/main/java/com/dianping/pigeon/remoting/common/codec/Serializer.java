package com.dianping.pigeon.remoting.common.codec;

import java.io.InputStream;
import java.io.OutputStream;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.exception.SerializationException;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.domain.InvokerContext;

//序列化器接口
public interface Serializer {

	//反序列化请求
	Object deserializeRequest(InputStream is) throws SerializationException;

	//序列化请求
	void serializeRequest(OutputStream os, Object obj) throws SerializationException;

	//反序列化响应
	Object deserializeResponse(InputStream is) throws SerializationException;

	//序列化响应
	void serializeResponse(OutputStream os, Object obj) throws SerializationException;

	//代理请求
	Object proxyRequest(InvokerConfig<?> invokerConfig) throws SerializationException;

	//返回新的响应
	InvocationResponse newResponse() throws SerializationException;

	//返回新的请求
	InvocationRequest newRequest(InvokerContext invokerContext) throws SerializationException;

}
