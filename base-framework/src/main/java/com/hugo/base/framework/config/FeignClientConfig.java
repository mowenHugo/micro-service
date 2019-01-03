package com.hugo.base.framework.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;


@Component
public class FeignClientConfig {

    private Logger logger = LoggerFactory.getLogger(FeignClientConfig.class);

    @Bean
    public RequestInterceptor headerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {

                /**
                 * 这里设置公共请求头参数，在使用feign调用其他微服务时，其他微服务可以获取到这些公共请求头参数
                 * 使用场景:可以传递基本信息到整个服务调用链中，如用户id。
                 */

                logger.debug("<---------------------Set Header------------------------");

                requestTemplate.header("userId", "12345678");
                requestTemplate.header("userName", "hugo");

                logger.debug("---------------------Set Header------------------------>");
            }

            private void print(String msg) {
                String[] codes = {"iso8859-1", "UTF-8", "GBK"};
                for (int i = 0; i < codes.length; i++) {
                    for (int j = 0; j < codes.length; j++) {
                        try {
                            System.out.println("----2--" + codes[i] + "->" + codes[j] + ":" + msg + "," + new String(msg.getBytes(codes[i]), codes[j]));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }
}
