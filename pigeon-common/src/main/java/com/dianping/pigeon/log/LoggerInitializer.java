package com.dianping.pigeon.log;

//日志初始化器
public interface LoggerInitializer {

    void init();

    Logger getLogger(String loggerName);

}
