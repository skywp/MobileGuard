package com.wp.mobileguard.mobileguard.utils;

/**
 * Created by WangPeng on 2016/4/6.
 * Email:wp20082009@foxmail.com
 */
public class EncryptTools {
    /**
     *  加密算法
     * @param seed
     *          加密的种子
     * @param string
     *          要加密的字符串
     * @return
     */
    public static String encrypt(int seed,String string) {
        byte[] bytes = string.getBytes();
        for (int i = 0;i <bytes.length;i++){
            bytes[i] ^= seed;//对字节加密
        }
        return new String(bytes);
    }

    /**
     *  解密算法
     * @param seed
     *          解密的种子
     * @param string
     *          要解密的字符串
     * @return
     */
    public static String decryption(int seed,String string){
        byte[] bytes = string.getBytes();
        for (int i = 0;i <bytes.length;i++){
            bytes[i] ^= seed;//对字节加密
        }
        return new String(bytes);
    }
}
