package com.wp.mobileguard.mobileguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by WangPeng on 2016/3/29.
 * Email:wp20082009@foxmail.com
 */
public class Md5Utils {
    public static String md5(String str) {
        StringBuilder mess = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();
            byte[] digest = messageDigest.digest(bytes);
            for (byte b : digest) {
                //把每个字节转成16进制数
                int d = b & 0xff;
                String hexString = Integer.toHexString(d);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                //把每个字节对应的2位十六进制数当做字符串拼接在一起
                mess.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return mess + "";
    }
}
