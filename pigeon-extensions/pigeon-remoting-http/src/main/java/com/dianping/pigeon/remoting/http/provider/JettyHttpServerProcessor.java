package com.dianping.pigeon.remoting.http.provider;

import com.dianping.pigeon.remoting.provider.config.ServerConfig;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

public interface JettyHttpServerProcessor {

	public void preStart(ServerConfig serverConfig, Server server, Context context);

}
