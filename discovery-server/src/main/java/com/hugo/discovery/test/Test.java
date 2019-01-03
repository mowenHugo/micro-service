package com.hugo.discovery.test;

import java.util.Base64;

/**
 * 加密
 */
public class Test {

    public static void main(String[] args) {
//        System.out.println(dataEncode("快递事业部 > 京津冀大区 > 业务部 > 北京业务处 > 通州操作科 > 张家湾操作室 > 通州张家湾点部").length());
//        Assert.notEmpty(Lists.newArrayList(), "查询配置未设置");
//        Assert.notNull(null, "查询配置错误，未设置索引名");

    }


    public static String dataEncode(String datas) {
        if (!datas.equals("") && datas != null) {
            String result = "";
            String[] str = new String[datas.length()];
            if (datas.length() > 1) {
                for(int i = 0; i < str.length; ++i) {
                    str[i] = String.valueOf(datas.charAt(i));
                    result = result + getXorString(str[i]);
                }
            } else {
                result = result + getXorString(datas);
            }

            return result;
        } else {
            return null;
        }
    }

    private static String getXorString(String data) {
        String dataEnc = Base64.getEncoder().encodeToString(data.getBytes());
        String result = "";
        String temp = "";
        byte[] databyte = new byte[dataEnc.length()];

        for(int i = 0; i < dataEnc.length(); ++i) {
            databyte[i] = (byte)dataEnc.charAt(i);
            int xornum2 = databyte[i] ^ "c".charAt(0);
            if (xornum2 < 10) {
                temp = "00" + xornum2;
            } else if (xornum2 < 100) {
                temp = "0" + xornum2;
            }

            result = result + temp;
        }

        return result;
    }
}
