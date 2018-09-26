package com.dianping.pigeon.util;

import org.apache.commons.lang.StringUtils;

//服务工具类
public class ServiceUtils {

    public static String getServiceId(String serviceName, String suffix) {
        String serviceId = serviceName;
        if (StringUtils.isNotBlank(suffix)) {
            serviceId = serviceId + ":" + suffix;
        }
        return serviceId;
    }

}
