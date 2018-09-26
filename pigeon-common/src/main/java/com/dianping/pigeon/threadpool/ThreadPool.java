package com.dianping.pigeon.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

//线程池接口
public interface ThreadPool {

    void execute(Runnable run);

    <T> Future<T> submit(Callable<T> call);

    Future<?> submit(Runnable run);

    ThreadPoolExecutor getExecutor();

    void prestartAllCoreThreads();

    void allowCoreThreadTimeOut(boolean value);

}
