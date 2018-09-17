package com.dianping.pigeon.config;

//配置异常
public class ConfigException extends RuntimeException {

	private static final long serialVersionUID = -277294587317829825L;

	public ConfigException(String msg) {
		super(msg);
	}

	public ConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}

}
