package com.dianping.pigeon.remoting.common.exception;

public class RejectedException extends RpcException {

	private static final long serialVersionUID = -4052834884778586750L;

	public RejectedException() {
		super();
	}

	public RejectedException(String msg) {
		super(msg);
	}

	public RejectedException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public String getErrorCode() {
		if (errorCode == null) {
			return "0300";
		}
		return errorCode;
	}

}
