package com.dianping.pigeon.registry.listener;

import com.dianping.pigeon.registry.config.RegistryConfig;

//分组改变监听器
public interface GroupChangeListener {

    void onGroupChange(String ip, RegistryConfig oldRegistryConfig, RegistryConfig newRegistryConfig);

}
