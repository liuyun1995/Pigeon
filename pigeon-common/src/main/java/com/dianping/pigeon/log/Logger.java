package com.dianping.pigeon.log;

//日志接口
public interface Logger {

	void debug(Object message);

	void debug(Object message, Throwable t);

	void debug(String message);

	void debug(String message, Throwable t);

	void error(Object message);

	void error(Object message, Throwable t);

	void error(String message);

	void error(String message, Throwable t);

	void fatal(Object message);

	void fatal(Object message, Throwable t);

	void fatal(String message);

	void fatal(String message, Throwable t);

	String getName();

	void info(Object message);

	void info(Object message, Throwable t);

	void info(String message);

	void info(String message, Throwable t);

	boolean isDebugEnabled();

	boolean isErrorEnabled();

	boolean isFatalEnabled();

	boolean isInfoEnabled();

	boolean isTraceEnabled();

	boolean isWarnEnabled();

	void trace(Object message);

	void trace(Object message, Throwable t);

	void trace(String message);

	void trace(String message, Throwable t);

	void warn(Object message);

	void warn(Object message, Throwable t);

	void warn(String message);

	void warn(String message, Throwable t);

}
