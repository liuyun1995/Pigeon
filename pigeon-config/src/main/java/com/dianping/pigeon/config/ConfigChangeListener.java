package com.dianping.pigeon.config;

//配置改变监听器
public interface ConfigChangeListener {

	//添加
	void onKeyAdded(String key, String value);

	//更新
	void onKeyUpdated(String key, String value);

	//移除
	void onKeyRemoved(String key);

}
