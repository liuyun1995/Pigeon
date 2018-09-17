package com.dianping.pigeon.monitor;

//监控器事务
public interface MonitorTransaction {

    public void setStatusError(Throwable t);

    public void complete();

    public void complete(long startTime);

    public void setStatusOk();

    public void addData(String name, Object data);

    public void readMonitorContext(String serverDomain);

    public void writeMonitorContext();

    public void logEvent(String name, String event, String desc);

    public String getParentRootMessage();

}
