package com.hugo.feigh.test.config;

import com.hugo.feigh.test.intercep.EncryptInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * 加密拦截器配置
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@AutoConfigureAfter(DataScopeHelperAutoConfiguration.class)
public class EncryptAutoConfiguration {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @Autowired
    private PageHelperProperties pageHelperProperties;

    @PostConstruct
    public void addDataScopeInterceptor() {
        Interceptor interceptor = new EncryptInterceptor();
        Properties properties = this.pageHelperProperties.getProperties();

        interceptor.setProperties(properties);

        Iterator var4 = this.sqlSessionFactoryList.iterator();
        while (var4.hasNext()) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) var4.next();
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }
    }
}

