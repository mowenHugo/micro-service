package com.hugo.search.configure;

import com.hugo.search.util.ElasticsearchTemplate;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ElasticsearchTemplate.class})//当ElasticsearchTemplate.class位于类路径上，才会实例化一个ElasticsearchAutoDataConfiguration
@AutoConfigureAfter(ESAutoConfiguration.class)//在加载ElasticsearchAutoConfiguration类之后再加载当前类
public class ESAutoDataConfiguration {

    @Bean
    @ConditionalOnMissingBean//仅仅在当前spring上下文中不存在ElasticsearchTemplate对象时，才会实例化一个ElasticsearchTemplate Bean
    @ConditionalOnBean(RestHighLevelClient.class)
    public ElasticsearchTemplate elasticsearchTemplate(RestHighLevelClient client) {
        try {
            return new ElasticsearchTemplate(client);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
