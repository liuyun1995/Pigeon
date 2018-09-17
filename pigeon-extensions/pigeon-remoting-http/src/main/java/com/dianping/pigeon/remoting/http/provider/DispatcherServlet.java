package com.dianping.pigeon.remoting.http.provider;

import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Servlet分配器
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 5766349180380479888L;

	private static final Logger logger = LoggerLoader.getLogger(DispatcherServlet.class);

	private static DispatcherServlet INSTANCE;

	private static final Map<Integer, HttpHandler> handlers = new ConcurrentHashMap<Integer, HttpHandler>();

	//添加http处理器
	public static void addHttpHandler(int port, HttpHandler processor) {
		handlers.put(port, processor);
	}

	//移除http处理器
	public static void removeHttpHandler(int port) {
		handlers.remove(port);
	}

	//获取实例
	public static DispatcherServlet getInstance() {
		return INSTANCE;
	}

	//构造器
	public DispatcherServlet() {
		DispatcherServlet.INSTANCE = this;
	}

	//核心服务方法
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		HttpHandler handler = handlers.get(request.getLocalPort());
		if (handler == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Service not found.");
		} else {
			try {
				handler.handle(request, response);
			} catch (Throwable e) {
				logger.error("Error with http handler", e);
				throw new ServletException(e);
			}
		}
	}

}