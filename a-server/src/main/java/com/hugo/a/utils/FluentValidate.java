package com.hugo.a.utils;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FluentValidate {

    /**
     * 验证器列表，接受{@link }实现类的数组
     */
    Class[] value() default {};

    /**
     * 分组验证，运行时只有在列表内的才验证
     *
     * @return 分组列表
     */
    Class<?>[] groups() default {};

    /**
     * 容器Bean id
     */
    String beanName() default "";

    /**
     * 错误编码
     */
    String[] appCodeEnumKey() default {};

    /**
     * 数据字典类型编码
     */
    String lookupCode() default "";

    /**
     * 最小长度
     *
     * @return
     */
    int minLength() default 0;

    int maxLength() default 16;

}