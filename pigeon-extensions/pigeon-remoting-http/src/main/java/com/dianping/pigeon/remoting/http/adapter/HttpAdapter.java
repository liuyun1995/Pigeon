package com.dianping.pigeon.remoting.http.adapter;

import javax.servlet.http.HttpServletRequest;

//http适配器
public interface HttpAdapter {

    public HttpAdapterRequest convert(HttpServletRequest request) throws Exception;

}
