package com.dianping.pigeon.remoting.common.monitor.trace;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//抽象的跟踪注册中心
public abstract class AbstractTraceRepository<M extends MonitorData, T extends AbstractTraceData> implements TraceRepository<M> {

    protected volatile ConcurrentMap<PairKey<SourceKey, DestinationKey>, T> traceDatas =
            new ConcurrentHashMap<PairKey<SourceKey, DestinationKey>, T>();

    public AbstractTraceRepository() {

    }

    @Override
    public void reset() {
        this.traceDatas = new ConcurrentHashMap<PairKey<SourceKey, DestinationKey>, T>();
    }

    @Override
    public TraceRepository copy() {
        AbstractTraceRepository<M,T> allTraceData = createAllTraceData();

        allTraceData.setTraceDatas(traceDatas);
        return allTraceData;
    }

    public abstract AbstractTraceRepository<M,T> createAllTraceData();

    public ConcurrentMap<PairKey<SourceKey, DestinationKey>, T> getTraceDatas() {
        return traceDatas;
    }

    public void setTraceDatas(ConcurrentMap<PairKey<SourceKey, DestinationKey>, T> traceDatas) {
        this.traceDatas = traceDatas;
    }

}
