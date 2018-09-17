package com.dianping.pigeon.registry.exception;

//注册中心异常
public class RegistryException extends Exception {

	private static final long serialVersionUID = -277294587317829825L;

	public RegistryException(String msg) {
		super(msg);
	}

	public RegistryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public RegistryException(Throwable cause) {
		super(cause);
	}

}
