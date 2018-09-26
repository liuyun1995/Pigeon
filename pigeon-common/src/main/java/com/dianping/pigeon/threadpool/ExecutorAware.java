package com.dianping.pigeon.threadpool;

//执行装配器
public interface ExecutorAware {

    //设置核心线程大小
    void setCorePoolSize(int corePoolSize);

    //获取核心线程大小
    int getCorePoolSize();

    //设置最大线程大小
    void setMaximumPoolSize(int maximumPoolSize);

    //获取最大线程大小
    int getMaximumPoolSize();

    //设置工作队列容量
    void setWorkQueueCapacity(int workQueueCapacity);

    //获取工作队列容量
    int getWorkQueueCapacity();

}
