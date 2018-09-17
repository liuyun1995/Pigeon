package com.dianping.pigeon.log;

import com.dianping.pigeon.extension.ExtensionLoader;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;

public class LoggerLoader {

    private static final String LOG_ROOT_KEY = "pigeon.log.dir";
    private static final String LOG_ROOT_DEFAULT = "/data/applogs/pigeon";

    public static String LOG_ROOT;

    private static LoggerInitializer loggerInitializer = ExtensionLoader.newExtension(LoggerInitializer.class);

    static {
        if (StringUtils.isBlank(System.getProperty(LOG_ROOT_KEY))) {
            System.setProperty(LOG_ROOT_KEY, LOG_ROOT_DEFAULT);
        }
        LOG_ROOT = System.getProperty(LOG_ROOT_KEY);

        if (loggerInitializer == null) {
            loggerInitializer = new InnerLoggerInitializer();
        }

        loggerInitializer.init();
    }

    public static Logger getLogger(Class clazz) {
        //根据全限定名获取日志
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String loggerName) {
        return loggerInitializer.getLogger(loggerName);
    }

    //内部日志初始化器
    private static class InnerLoggerInitializer implements LoggerInitializer {

        private static Constructor logConstructor;

        @Override
        public void init() {
            //获取日志类型
            String logType = System.getProperty("pigeon.logType");

            if (logType != null) {
                if (logType.equalsIgnoreCase("slf4j")) {
                    tryImplementation("org.slf4j.Logger", "com.dianping.pigeon.log.Slf4jLogger");
                } else if (logType.equalsIgnoreCase("log4j2")) {
                    tryImplementation("org.apache.logging.log4j.Logger", "com.dianping.pigeon.log.Log4j2Logger");
                } else if (logType.equalsIgnoreCase("simple")) {
                    tryImplementation(null, "com.dianping.pigeon.log.SimpleLogger");
                } else if (logType.equalsIgnoreCase("null")) {
                    tryImplementation(null, "com.dianping.pigeon.log.NullLogger");
                }
            }

            // log4j2 > slf4j > simple > null
            tryImplementation("org.apache.logging.log4j.Logger", "com.dianping.pigeon.log.Log4j2Logger");
            tryImplementation("org.slf4j.Logger", "com.dianping.pigeon.log.Slf4jLogger");

            //若构造器为空，则使用简单日志类型
            if (logConstructor == null) {
                try {
                    logConstructor = SimpleLogger.class.getConstructor(String.class);
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
        }

        @Override
        public Logger getLogger(String loggerName) {
            try {
                //使用构造器构造日志对象
                return (Logger) logConstructor.newInstance(loggerName);
            } catch (Throwable t) {
                throw new RuntimeException("failed to create logger for " + loggerName + ", cause: " + t, t);
            }
        }

        @SuppressWarnings("unchecked")
        private static void tryImplementation(String testClassName, String implClassName) {
            if (logConstructor != null) {
                return;
            }

            try {
                //若测试类型名称不为空，则尝试加载该类型
                if(testClassName != null) {
                    Resources.classForName(testClassName);
                }
                //加载实际类型
                Class implClass = Resources.classForName(implClassName);
                //获取实际日志类型的构造器
                logConstructor = implClass.getConstructor(new Class[] {
                        String.class
                });

                Class<?> declareClass = logConstructor.getDeclaringClass();
                if (!Logger.class.isAssignableFrom(declareClass)) {
                    logConstructor = null;
                }

                try {
                    if (null != logConstructor) {
                        logConstructor.newInstance(LoggerLoader.class.getName());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    logConstructor = null;
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
