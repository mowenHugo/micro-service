package com.hugo.search.configure;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ESClientFactoryBean implements FactoryBean<RestHighLevelClient>,
        InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ESClientFactoryBean.class);
    private String clusterNodes = "127.0.0.1:9300";
    private Boolean clientSniff = true;
    private static final String SCHEMA = "http";
    private static final int CONNECT_TIME_OUT = 1000;
    private static final int SOCKET_TIME_OUT = 30000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 500;
    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;

    private static RestHighLevelClient restHighLevelClient;

    @Override
    public void destroy() throws Exception {
        try {
            logger.info("Closing elasticSearch client");
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (final Exception e) {
            logger.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public RestHighLevelClient getObject() throws Exception {
        return restHighLevelClient;
    }

    @Override
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        buildClient();
    }

    protected void buildClient() throws Exception {
        RestClientBuilder builder = RestClient.builder(initHttpHost());
        setConnectTimeOutConfig(builder);
        setMutiConnectConfig(builder);

        restHighLevelClient = new RestHighLevelClient(builder);
//        if (clientSniff) {
//            Sniffer sniffer = Sniffer.builder(restHighLevelClient.getLowLevelClient()).build();
//        }
    }

    public static void setConnectTimeOutConfig(RestClientBuilder builder) {

//        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
//            @Override
//            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
//                requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);
//                requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);
//                requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
//                return requestConfigBuilder;
//            }
//        });

        //以上注释部分和以下代码作用一样
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);
            requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);
            requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
            return requestConfigBuilder;
        });

        //httpClient中设置timeout
//        connectionRequestTimeout是获取连接池连接的超时时间
//        connectionTimeout是建立连接的超时时间，
//        socketTimeout是等待服务器响应的超时时间
    }

    //使用异步httpclient时设置并发连接数
    public static void setMutiConnectConfig(RestClientBuilder builder) {
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM); //设置总的最大连接个数
            httpClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE); //单独为某站点设置最大连接个数
            return httpClientBuilder;
        });
    }

    public HttpHost[] initHttpHost() {
        HttpHost[] httpHosts = new HttpHost[]{};
        String[] clusterNodesAttr = this.clusterNodes.split(",");
        if (clusterNodesAttr.length > 0) {
            for (String clusterNode : clusterNodesAttr) {
                String[] clusterNodeAttr = clusterNode.split(":");
                if (clusterNodeAttr.length > 0) {
                    String hostName = clusterNodeAttr[0];
                    int port = Integer.parseInt(clusterNodeAttr[1]);
                    HttpHost httpHost = new HttpHost(hostName, port, SCHEMA);
                    httpHosts = ArrayUtils.add(httpHosts, httpHost);
                }
            }
        }
        return httpHosts;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }
}
