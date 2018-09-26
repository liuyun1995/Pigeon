package com.dianping.pigeon.console.status;

import java.util.Map;

//状态信息
public interface StatusInfo {

	String getSource();

	Map<String, String> getStatusInfo();

}
