package com.kodomo.yummy.util;

import org.apache.commons.codec.binary.Hex;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

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

    /**
     * 去除null值
     * @param s
     * @return
     */
    @NotNull
    public static String string(String s) {
        return s == null ? "" : s;
    }

    /**
     * 去除null
     * @param i
     * @return
     */
    @NotNull
    public static int integer(Integer i) {
        return i == null ? 0 : i;
    }
}
