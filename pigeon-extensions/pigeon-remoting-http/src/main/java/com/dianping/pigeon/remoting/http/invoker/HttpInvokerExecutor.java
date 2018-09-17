package com.dianping.pigeon.remoting.http.invoker;

import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import com.dianping.pigeon.remoting.common.codec.Serializer;
import com.dianping.pigeon.remoting.common.codec.SerializerFactory;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

//http调用执行器
public class HttpInvokerExecutor {

	private static final Logger logger = LoggerLoader.getLogger(HttpInvokerExecutor.class);

	private HttpClient httpClient;

	private String contentType;

	protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";

	protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";

	protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

	protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";

	protected static final String ENCODING_GZIP = "gzip";

	public String getContentType() {
		return contentType;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void setReadTimeout(int timeout) {
		if (!(timeout >= 0)) {
			throw new IllegalArgumentException("Timeout must be a non-negative value");
		}
		this.httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
	}

	public void setConnectTimeout(int timeout) {
		if (!(timeout >= 0)) {
			throw new IllegalArgumentException("Timeout must be a non-negative value");
		}
		this.httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
	}

	public void setContentType(String contentType) {
		if (contentType == null) {
			throw new IllegalArgumentException("'contentType' must not be null");
		}
		this.contentType = contentType;
	}

	//创建post方法
	protected PostMethod createPostMethod(String url) throws IOException {
		PostMethod postMethod = new PostMethod(url);
		postMethod.addRequestHeader(HTTP_HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
		return postMethod;
	}

	protected boolean isGzipResponse(PostMethod postMethod) {
		Header encodingHeader = postMethod.getResponseHeader(HTTP_HEADER_CONTENT_ENCODING);
		return (encodingHeader != null && encodingHeader.getValue() != null && encodingHeader.getValue().toLowerCase()
				.contains(ENCODING_GZIP));
	}

	//获取响应实体
	protected InputStream getResponseBody(PostMethod postMethod) throws IOException {
		if (isGzipResponse(postMethod)) {
			return new GZIPInputStream(postMethod.getResponseBodyAsStream());
		} else {
			return postMethod.getResponseBodyAsStream();
		}
	}

	//发送http请求
	public final InvocationResponse executeRequest(String url, InvocationRequest invocationRequest) throws Exception {
		byte serialize = invocationRequest.getSerialize();
		//获取序列化器
		Serializer serializer = SerializerFactory.getSerializer(serialize);
		PostMethod postMethod = null;
		try {
			//创建post方法
			postMethod = createPostMethod(url);
			postMethod.addParameter("serialize", serialize + "");
			postMethod.addRequestHeader("serialize", serialize + "");
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			try {
				serializer.serializeRequest(baos, invocationRequest);
				if (logger.isDebugEnabled()) {
					logger.debug("serialize:" + new String(baos.toByteArray()));
				}
				//设置请求实体
				postMethod.setRequestEntity(new ByteArrayRequestEntity(baos.toByteArray(), this.getContentType()));
			} finally {
				baos.close();
			}
			//执行post请求
			httpClient.executeMethod(postMethod);
			if (postMethod.getStatusCode() >= 300) {
				throw new HttpException("Did not receive successful HTTP response: status code = "
						+ postMethod.getStatusCode() + ", status message = [" + postMethod.getStatusText() + "]");
			}
			//获取响应实体
			InputStream responseBody = getResponseBody(postMethod);
			//执行反序列化
			return (InvocationResponse) serializer.deserializeResponse(responseBody);
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
	}
}
