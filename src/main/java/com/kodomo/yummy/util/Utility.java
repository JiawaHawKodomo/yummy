package com.kodomo.yummy.util;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 15:29
 */
public class Utility {

    /**
     * SHA-256加密
     *
     * @param str
     * @return 64位加密后的密码
     */
    public static String getSHA256Str(String str) {
        if (str == null)
            return null;

        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            encdeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encdeStr;
    }

    public static double latitudeByDistance(double distance) {
        return distance / 111.0;
    }

    public static double longitudeByDisrance(double distance, double latitude) {
        return distance / (111.0 * Math.cos(Math.abs(latitude) / 180.0 * Math.PI));
    }
}
