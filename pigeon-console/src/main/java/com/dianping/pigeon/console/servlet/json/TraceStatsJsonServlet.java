package com.dianping.pigeon.console.servlet.json;

import com.dianping.pigeon.remoting.common.monitor.trace.ApplicationTraceRepository;
import com.dianping.pigeon.remoting.common.monitor.trace.MonitorDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TraceStatsJsonServlet extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ApplicationTraceRepository traceData = MonitorDataFactory.getTraceData();

        ApplicationTraceRepository old = traceData.copy();
        traceData.reset();

        String traceDataJson = mapper.writeValueAsString(old);
        response.getWriter().print(traceDataJson);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
