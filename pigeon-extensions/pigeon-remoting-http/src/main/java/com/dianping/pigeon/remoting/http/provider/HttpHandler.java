package com.dianping.pigeon.remoting.http.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//http请求处理器
public interface HttpHandler {

	void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;

}