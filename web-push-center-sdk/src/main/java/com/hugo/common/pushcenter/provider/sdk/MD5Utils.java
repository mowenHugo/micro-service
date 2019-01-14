package com.hugo.common.pushcenter.provider.sdk;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public MD5Utils() {
    }

    public static String md5(String content, String charset, String type)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest messageDigest = null;
        messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(content.getBytes("UTF-8"));
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; ++i) {
            if (Integer.toHexString(255 & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & byteArray[i]));
            }
        }

        if ("16".equals(type)) {
            return md5StrBuff.toString().substring(8, 24);
        } else {
            return md5StrBuff.toString();
        }
    }
}
