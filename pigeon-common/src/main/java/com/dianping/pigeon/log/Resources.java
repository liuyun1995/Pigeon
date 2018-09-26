package com.dianping.pigeon.log;

//资源管理器
public final class Resources extends Object {

    private static ClassLoader defaultClassLoader;

    private Resources(){}

    //获取默认加载器
    public static ClassLoader getDefaultClassLoader() {
        return defaultClassLoader;
    }

    //设置默认加载器
    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        Resources.defaultClassLoader = defaultClassLoader;
    }

    //根据名称加载类
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            clazz = getClassLoader().loadClass(className);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (clazz == null) {
            clazz = Class.forName(className);
        }
        return clazz;
    }

    //获取类加载器
    private static ClassLoader getClassLoader() {
        if (defaultClassLoader != null) {
            return defaultClassLoader;
        } else {
            return Thread.currentThread().getContextClassLoader();
        }
    }

}
