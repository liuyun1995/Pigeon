package com.dianping.pigeon.registry.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//服务配置
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceConfig {

    private String group = "";

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
