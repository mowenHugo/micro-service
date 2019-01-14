package com.hugo.common.pushcenter.provider.sdk;

public class StringUtils {

    public StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isRealEmpty(String s) {
        boolean result = isEmpty(s);
        if (!result) {
            result = s.trim().length() == 0;
        }

        return result;
    }

    public static boolean isTrimedEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isNotRealEmpty(String s) {
        return !isRealEmpty(s);
    }

    public static String defaultString(String str, String defaultStr) {
        return isTrimedEmpty(str) ? defaultStr : str.trim();
    }
}
