package com.dianping.pigeon.remoting.http.provider;

import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.remoting.common.codec.Serializer;
import com.dianping.pigeon.remoting.common.codec.SerializerFactory;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.provider.domain.ProviderChannel;
import com.dianping.pigeon.remoting.provider.domain.ProviderContext;
import org.apache.commons.lang.SerializationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpChannel implements ProviderChannel {

	private static final Logger logger = LoggerLoader.getLogger(HttpChannel.class);
	HttpServletRequest request;
	HttpServletResponse response;
	public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
	private String contentType = CONTENT_TYPE_SERIALIZED_OBJECT;
	private static final String protocol = "http";

	public void setContentType(String contentType) {
		if (contentType == null) {
			throw new IllegalArgumentException("'contentType' must not be null");
		}
		this.contentType = contentType;
	}

	public String getContentType() {
		return this.contentType;
	}

	public HttpChannel(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@Override
	public void write(ProviderContext context, InvocationResponse invocationResponse) {
		response.setContentType(getContentType());
		Serializer serializer = SerializerFactory.getSerializer(invocationResponse.getSerialize());
		try {
			serializer.serializeResponse(response.getOutputStream(), invocationResponse);
			response.flushBuffer();
			if (Constants.REPLY_MANUAL || context.isAsync()) {
				HttpCallbackFuture httpCallbackFuture = HttpServerHandler.callbacks.get(invocationResponse.getSequence());
				if(httpCallbackFuture != null) {
					httpCallbackFuture.run();
				}
			}
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public String getRemoteAddress() {
		return this.request.getRemoteAddr();
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public int getPort() {
		return request.getLocalPort();
	}

}
