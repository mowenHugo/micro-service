package com.hugo.a.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class Test1 {

    public static <A extends Annotation> A getAnnotation(Field field, Class<A> annotationType) {
        if (field == null || annotationType == null) {
            return null;
        }
        return field.getAnnotation(annotationType);
    }

    public static Field[] getAnnotationFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        if (clazz == null || annotationClass == null) {
            return null;
        }

        List<Field> fields = getAllFieldsOfClass0(clazz);
        if (CollectionUtil.isEmpty(fields)) {
            return null;
        }

        List<Field> list = CollectionUtil.createArrayList();
        for (Field field : fields) {
            if (null != field.getAnnotation(annotationClass)) {
                list.add(field);
                field.setAccessible(true);
            }
        }

        return list.toArray(new Field[0]);
    }

    static List<Field> getAllFieldsOfClass0(Class<?> clazz) {
        List<Field> fields = CollectionUtil.createArrayList();
        for (Class<?> itr = clazz; hasSuperClass(itr); ) {
            fields.addAll(Arrays.asList(itr.getDeclaredFields()));
            itr = itr.getSuperclass();
        }

        return fields;
    }

    public static boolean hasSuperClass(Class<?> clazz) {
        return (clazz != null) && !clazz.equals(Object.class);
    }




    public static void main(String[] args) {
        Field[] fields = Test1.getAnnotationFields(A.class, FluentValidate.class);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            FluentValidate fluentValidateAnnt = Test1.getAnnotation(field, FluentValidate.class);
        }


    }

    class A {
        @FluentValidate(lookupCode = "crm_customer_type",
                appCodeEnumKey = "CRM_CUSTOMER_TYPE_ENUM_NULL", groups = {Test1.class})
        private String name;
        @FluentValidate(beanName = "stringLengthValidator", minLength = 0, maxLength = 256
                ,appCodeEnumKey = "COMPANY_AREA_LENGTH")
        private Integer max;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMax() {
            return max;
        }

        public void setMax(Integer max) {
            this.max = max;
        }
    }
}
