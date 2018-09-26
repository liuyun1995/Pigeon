package com.dianping.pigeon.console.status.checker;

import java.util.List;
import java.util.Map;

//状态检查器
public interface StatusChecker {

	List<Map<String, Object>> collectStatusInfo();

	String checkError();

}