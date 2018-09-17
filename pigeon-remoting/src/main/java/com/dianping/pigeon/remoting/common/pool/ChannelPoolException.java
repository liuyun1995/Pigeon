package com.dianping.pigeon.remoting.common.pool;

import com.dianping.pigeon.remoting.common.exception.RpcException;

//通道池异常
public class ChannelPoolException extends RpcException {

    private static final long serialVersionUID = -1L;

    public ChannelPoolException() {
        super();
    }

    public ChannelPoolException(String message) {
        super(message);
    }

    public ChannelPoolException(String message, Throwable cause) {
        super(message, cause);
    }

}