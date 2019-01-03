package com.hugo.base.framework.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class TraceServletResponseWrapper extends HttpServletResponseWrapper {
    private TraceServletOutputStream traceOutputStream;
    private HttpServletResponse response;

    public TraceServletResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // 如果响应头的ContentType不符合规则则直接返回
        if (!FilterUtils.shouldTracer(null, response)) {
            return super.getOutputStream();
        }
        // 拦截响应流,获取响应body
        if (null == traceOutputStream) {
            traceOutputStream = new TraceServletOutputStream(super.getOutputStream());
        }
        return traceOutputStream;
    }

    /**
     * @return the traceOutputStream
     */
    public TraceServletOutputStream getTraceOutputStream() {
        return traceOutputStream;
    }

}
