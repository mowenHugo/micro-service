package com.hugo.base.framework.filter;

import com.hugo.base.framework.constant.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class CustomTraceFilter extends GenericFilterBean {


    private static final Logger logger = LoggerFactory.getLogger(CustomTraceFilter.class);
    public static final int ContentLength = 40960;


    private final String METHOD_GET = "GET";
    private final String TRACE_REQUEST_BODY = "request.body";
    private final String TRACE_REQUEST_HEADER_BODY = "request.header";
    private final String TRACE_RESPONSE_LOG_BODY = "response.log";
    private final String TRACE_RESPONSE_BODY = "response.body";
    private final String SPRING_APPLICATION_NAME = "spring.application.name";

    private Tracer tracer;

    /**
     * 服务器名
     */
    private static String serverName = "";
    /**
     * 服务器IP
     */
    private static String serverAddressIp = "";

    public CustomTraceFilter(Tracer tracer) {
        this.tracer = tracer;
        init();
    }

    /**
     * 初始化主机信息
     */
    private void init() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            serverAddressIp = addr.getHostAddress().toString(); //获取本机ip
            serverName = addr.getHostName().toString(); //获取本机计算机名称
        } catch (UnknownHostException e) {
            logger.debug("init", e);
        }

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException("Filter just supports HTTP requests");
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Long startLong = System.currentTimeMillis();

        try {
//            mdc(request, response);
            Boolean tracerFlag = true;
            if (null == this.tracer || !FilterUtils.shouldTracer(request, response)) {
                tracerFlag = false;
            }
            // 包装ServletRequest
            TraceServletRequestWrapper requestWrapper = new TraceServletRequestWrapper(request);
            // 包装ServletResponse
            TraceServletResponseWrapper responseWrapper = new TraceServletResponseWrapper(response);
            //请求
            filterChain.doFilter(requestWrapper, responseWrapper);
            //是否开启Debug模式
            if (logger.isInfoEnabled()) {
                Long endTime = System.currentTimeMillis();
                // 跟踪请求参数
                String requestBody = traceRequestParam(requestWrapper, tracerFlag);
                // 跟踪相应参数
                String responseBody = traceResponseParam(responseWrapper, tracerFlag);

                StringBuilder builder = new StringBuilder();
                if (isRealEmpty(requestBody)) {
                    requestBody = "{}";
                }
                if (isRealEmpty(responseBody)) {
                    responseBody = "{}";
                }
                builder.append("{\"requestBody\":" + requestBody + ",");
                builder.append("\"responseBody\":" + responseBody + ",");
                builder.append("\"startTime\":" + startLong + ",");
                builder.append("\"endTime\":" + endTime + ",");
                builder.append("\"costTimes\":" + (endTime - startLong));
                builder.append("}");
                logger.debug(builder.toString());
            }
        } catch (Exception e) {
            logger.error("tracer request/response param error!", e);
        }
    }


    private String traceRequestParam(TraceServletRequestWrapper requestWrapper, Boolean tracerFlag) throws UnsupportedEncodingException {
        String body = "";
        String method = requestWrapper.getMethod();
        // 处理GET请求参数
        if (method.equalsIgnoreCase(METHOD_GET)) {
            body = requestWrapper.getQueryString();
//            if (StringUtils.isNotEmpty(body) && Base64.isBase64(body)) {
//                body = new String(Base64.decodeBase64(body), StandardCharsets.UTF_8);
//            }
        } else {
            // 处理POST请求参数
            int size = requestWrapper.getContentLength();
            if (size < ContentLength) {
                TraceServletInputStream traceInputStream = requestWrapper.getTraceInputStream();
                if (traceInputStream != null) {
                    body = new String(traceInputStream.getContent().getBytes(), StandardCharsets.UTF_8);
                }
            }
        }

        if (StringUtils.isNotEmpty(body)) {
            if (tracerFlag) {
                this.tracer.addTag(TRACE_REQUEST_BODY, body);
            }
        }
        return body;
    }

    private String traceResponseParam(TraceServletResponseWrapper responseWrapper, Boolean tracerFlag)
            throws UnsupportedEncodingException {
        TraceServletOutputStream traceOutputStream = responseWrapper.getTraceOutputStream();
        if (null != traceOutputStream && StringUtils.isNotEmpty(traceOutputStream.getContent())) {
            String response = new String(traceOutputStream.getContent().getBytes(), StandardCharsets.UTF_8);
            if (tracerFlag) {
                this.tracer.addTag(TRACE_RESPONSE_BODY, response);
            }
            return response;
        }
        return null;
    }

    /**
     * 增加Log MDC 参数
     *
     * @param request
     * @param response
     */
    public void mdc(HttpServletRequest request, HttpServletResponse response) {

        String traceId = getHeader(request, Constants.X_B3_TRACE_ID);
        String xuid = getHeader(request, Constants.X_UID);
        // 日志跟踪id
        MDC.put("traceId", traceId);
        // 远程IP
        MDC.put("remoteIP", getHeader(request, Constants.X_REAL_IP));
        // 请求URL
        MDC.put("requestURL", getHeader(request, Constants.X_SPAN_NAME));
        // 会话ID
        MDC.put("sessionId", getHeader(request, Constants.X_SESSION_ID));
        // 用户id
        MDC.put("userId", xuid);
        //主机名
        MDC.put("serverName", serverName);
        // 主机IP
        MDC.put("serverIp", serverAddressIp);

        String spanId = getHeader(request, Constants.X_SPAN_ID);
        if (StringUtils.isEmpty(spanId)) {
            spanId = traceId;
        } else {
            MDC.put(Constants.X_SPANPID, spanId);
            spanId = spanId + "_01";

        }
        MDC.put(Constants.X_SPANID, spanId);
    }

    /**
     * 得到请求头中的信息
     *
     * @param request
     * @param name
     * @return
     */
    private String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        } else {
            return value;
        }
    }

    public static boolean isRealEmpty(String s) {
        boolean result = isEmpty(s);
        if (!result) {
            result = s.trim().length() == 0;
        }

        return result;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}