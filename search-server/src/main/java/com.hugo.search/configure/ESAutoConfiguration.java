package com.hugo.search.configure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration //声明一个配置
@ConditionalOnClass({ESClientFactoryBean.class}) //当在classpath中存在ElasticsearchClientFactoryBean时，则将当前配置装载到spring容器
@EnableConfigurationProperties(ESProperties.class) //启动ElasticsearchProperties属性配置
public class ESAutoConfiguration {

    private static final Log logger = LogFactory.getLog(ESAutoConfiguration.class);
    private final ESProperties properties;
    private RestHighLevelClient releasable;
    private final String CLUSTER_NAME = "cluster.name";

    public ESAutoConfiguration(ESProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean //仅仅在当前spring上下文中不存在RestHighLevelClient对象时，才会实例化一个RestHighLevelClient Bean
    public RestHighLevelClient elasticsearchClient() {
        try {
            return createClient();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private RestHighLevelClient createClient() throws Exception {
        return createTransportClient();
    }

    private RestHighLevelClient createTransportClient() throws Exception {
        ESClientFactoryBean factory = new ESClientFactoryBean();
        factory.setClusterNodes(this.properties.getClusterNodes());
        factory.afterPropertiesSet();
        RestHighLevelClient client = factory.getObject();
        return client;
    }

    private Properties createProperties() {
        Properties properties = new Properties();
        properties.put(CLUSTER_NAME, this.properties.getClusterName());
        properties.putAll(this.properties.getProperties());
        return properties;
    }
}
