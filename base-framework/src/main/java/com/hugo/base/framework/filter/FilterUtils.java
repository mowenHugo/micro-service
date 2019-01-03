package com.hugo.base.framework.filter;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FilterUtils {
    final static String METHOD_GET = "GET";
    final static String CONTENT_TYPE = "Content-Type";
    final static String REQUEST_ACCEPT_CONTENT_TYPE = "application/x-www-form-urlencoded,application/json,text/xml";
    final static String RESPONSE_ACCEPT_CONTENT_TYPE = "text/xml,text/plain,application/json";
    final static String SEPARATOR_STR = ",";

    /**
     * 判断是否包装过滤请求
     * 当request时只tracer请求体类型为application/x-www-form-urlencoded,application/json,text/xml的请求
     * 当response时只tracer响应体类型为text/xml,text/plain,application/json的响应
     *
     * @param request
     * @param response
     * @return
     */
    public static boolean shouldTracer(HttpServletRequest request, HttpServletResponse response) {
        boolean tracerFlag = false;
        if (null != request) {
            // request请求为GET时默认tracer
            if (METHOD_GET.equalsIgnoreCase(request.getMethod())) {
                return true;
            }
            String requestContentType = request.getHeader(CONTENT_TYPE);
            // 判断请求头的CONTENT_TYPE是否符合规则
            if (StringUtils.isNotEmpty(requestContentType)) {
                String[] acceptContentTypes = REQUEST_ACCEPT_CONTENT_TYPE.split(SEPARATOR_STR);
                for (String acceptContentType : acceptContentTypes) {
                    if (requestContentType.contains(acceptContentType)) {
                        return true;
                    }
                }
            }
        }

        if (null != response) {
            String responseContentType = response.getContentType();
            // 判断响应头的CONTENT_TYPE是否符合规则
            if (StringUtils.isNotEmpty(responseContentType)) {
                String[] acceptContentTypes = RESPONSE_ACCEPT_CONTENT_TYPE.split(SEPARATOR_STR);
                for (String acceptContentType : acceptContentTypes) {
                    if (responseContentType.contains(acceptContentType)) {
                        return true;
                    }
                }
            }
        }
        return tracerFlag;
    }
}
