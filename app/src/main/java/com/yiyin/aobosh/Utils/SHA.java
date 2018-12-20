package com.yiyin.aobosh.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ALIY on 2018/12/9 0009.
 * 对外提供getSHA(String str)方法
 */

public class SHA {

    //SHA1 加密实例
    public static String encryptToSHA(String info) {
        byte[] digesta = new byte[0];
        try {
            // 得到一个SHA-1的消息摘要
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            // 添加要进行计算摘要的信息
            sha.update(info.getBytes());
            // 得到该摘要
            digesta = sha.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 将摘要转为字符串
        String rs = hexString(digesta);
        return rs;
    }

    //byte字节转换成16进制的字符串MD5Utils.hexString
    public static String hexString(byte[] bytes){
        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            int val = ((int) bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}



