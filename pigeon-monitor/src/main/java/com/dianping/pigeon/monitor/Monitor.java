package com.dianping.pigeon.monitor;

//监控器接口
public interface Monitor {

	void init();

	void logError(String msg, Throwable t);

	void logError(Throwable t);

	void logEvent(String name, String event, String desc);

	/**
	 * write monitor's own error to local log files or console
	 * 
	 * @param t
	 */
	void logMonitorError(Throwable t);

	MonitorTransaction createTransaction(String name, String uri, Object invocationContext);

	MonitorTransaction getCurrentCallTransaction();

	void setCurrentCallTransaction(MonitorTransaction transaction);

	void clearCallTransaction();

	MonitorTransaction getCurrentServiceTransaction();

	void setCurrentServiceTransaction(MonitorTransaction transaction);

	void clearServiceTransaction();

}
