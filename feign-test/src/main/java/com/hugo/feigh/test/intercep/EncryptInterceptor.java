package com.hugo.feigh.test.intercep;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

/**
 * mybatis 拦截器
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
        }
)
public class EncryptInterceptor implements Interceptor {
    private Logger logger = LoggerFactory.getLogger(EncryptInterceptor.class);

    /**
     * 判断是否为修改请求
     */
    public static String UPDATE_METHOD = "update";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        Executor executor = (Executor) invocation.getTarget();

        // 加密部分开始
//        encode(parameter);
        // 加密部分结束

        // update
        if (UPDATE_METHOD.equals(invocation.getMethod().getName())) {
            return executor.update(ms, parameter);
        }
        // query
        else {
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            CacheKey cacheKey;
            BoundSql boundSql;
            //由于逻辑关系，只会进入一次
            if (args.length == 4) {
                //4 个参数时
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                //6 个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
            List<Object> query = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);

            // 解密部分开始
            if (!CollectionUtils.isEmpty(query)) {
//                decode(query);
            }
            // 解密部分结束

            return query;
        }
    }

    private Object getValue(String type, Object value) {
        if(null == value) {
            return value;
        }
        String strValue = String.valueOf(value);
        if("java.lang.String".equals(type)) {
            return strValue;
        } else if("java.math.BigDecimal".equals(type)) {
            return BigDecimal.valueOf(Double.valueOf(strValue));
        } else if("java.lang.Integer".equals(type)) {
            return Integer.valueOf(strValue);
        } else if("java.lang.Long".equals(type)) {
            return Long.valueOf(strValue);
        }
        return value;
    }




    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}
