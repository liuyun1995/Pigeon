package com.dianping.pigeon.threadpool;

import java.util.concurrent.BlockingQueue;

//可调节大小的阻塞队列
public interface ResizableBlockingQueue<E> extends BlockingQueue<E> {

    //获取容量
    int getCapacity();

    //设置容量
    void setCapacity(int capacity);

}
