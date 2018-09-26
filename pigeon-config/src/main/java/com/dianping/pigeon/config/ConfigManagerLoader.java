package com.dianping.pigeon.config;

import com.dianping.pigeon.config.file.PropertiesFileConfigManager;
import com.dianping.pigeon.extension.ExtensionLoader;
import com.dianping.pigeon.log.Log4j2Logger;
import com.dianping.pigeon.log.Logger;
import com.dianping.pigeon.log.LoggerLoader;

//配置管理器加载器
public class ConfigManagerLoader {

	private static ConfigManager configManager = ExtensionLoader.getExtension(ConfigManager.class);
	private static final Logger logger = LoggerLoader.getLogger(ConfigManagerLoader.class);
	private static final String KEY_LOG_DEBUG_ENABLE = "pigeon.log.debug.enable";

	static {
		//默认使用配置文件配置管理器
		if (configManager == null) {
			configManager = new PropertiesFileConfigManager();
		}
		logger.info("config manager:" + configManager);
		configManager.init();
		initLoggerConfig();
	}

	//初始化日志配置
	private static void initLoggerConfig() {
		try {
			Log4j2Logger.setDebugEnabled(configManager.getBooleanValue(KEY_LOG_DEBUG_ENABLE, false));
		} catch (RuntimeException e) {
		}
		ConfigManagerLoader.getConfigManager().registerConfigChangeListener(new InnerConfigChangeListener());
	}

	private static class InnerConfigChangeListener implements ConfigChangeListener {

		@Override
		public void onKeyUpdated(String key, String value) {
			if (key.endsWith(KEY_LOG_DEBUG_ENABLE)) {
				try {
					Log4j2Logger.setDebugEnabled(Boolean.valueOf(value));
				} catch (RuntimeException e) {
				}
			}
		}

		@Override
		public void onKeyAdded(String key, String value) {

		}

		@Override
		public void onKeyRemoved(String key) {

		}

	}

	//获取配置管理器
	public static ConfigManager getConfigManager() {
		return configManager;
	}

}
