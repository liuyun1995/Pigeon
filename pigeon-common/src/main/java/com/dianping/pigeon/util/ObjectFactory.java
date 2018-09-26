package com.dianping.pigeon.util;

//对象工厂
public interface ObjectFactory<T> {

    T createObject();

    Class<T> getObjectClass();

}