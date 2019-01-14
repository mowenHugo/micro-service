package com.hugo.feigh.test.intercep;

import com.github.pagehelper.PageException;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class DataScopeInterceptor implements Interceptor {

    private Field additionalParametersField;
    private final String SQL_DATA_SCOPE = "sqldatascope";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
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

        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (sqlCommandType == SqlCommandType.SELECT) {
            String originalSql = boundSql.getSql();
            if (originalSql.matches("[\\s\\S]*<datascope[^>]*>[\\s\\S]*")) {
                String dataScopeSql = invokeBoundSql(originalSql);

                BoundSql dataScopeBoundSql = new BoundSql(ms.getConfiguration(), dataScopeSql, boundSql.getParameterMappings(), parameter);
                //当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
                Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
                for (String key : additionalParameters.keySet()) {
                    dataScopeBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
                }
                return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, dataScopeBoundSql);
            }
        }
        return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    private String invokeBoundSql(String originalSql) throws Exception {
        String convertSql = "";

//        Map<String, Object> objectMap = ContextUtils.get().getGlobalVariableMap();
//        Object dataScopeObj = objectMap.get(Context.DATASCOPE);
//        if (null != dataScopeObj) {
//            Map<String, Object> dataScopeMap = (Map<String, Object>) dataScopeObj;
//            Map<String, Object> sqlDataScopeMap = (Map<String, Object>) dataScopeMap.get(SQL_DATA_SCOPE);
//
//            Set<String> keys = sqlDataScopeMap.keySet();
//            for (String key : keys) {
//                originalSql = originalSql.replaceAll(key, sqlDataScopeMap.get(key).toString());
//            }
//            convertSql = originalSql;
//        } else {
//            if (isRealEmpty(ContextUtils.get().getPermissionId())) {
//                convertSql = originalSql.replaceAll("<datascope>", "1 = 1");
//            } else {
//                convertSql = originalSql.replaceAll("<datascope>", "1 <> 1");
//            }
//
//        }

        return convertSql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        try {
            //反射获取 BoundSql 中的 additionalParameters 属性
            additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new PageException(e);
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