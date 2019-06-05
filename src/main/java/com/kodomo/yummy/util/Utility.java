package com.kodomo.yummy.util;

import org.apache.commons.codec.binary.Hex;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
     *
     * @param s
     * @return
     */
    @NotNull
    public static String string(String s) {
        return s == null ? "" : s;
    }

    /**
     * 去除null
     *
     * @param i
     * @return
     */
    @NotNull
    public static int integer(Integer i) {
        return i == null ? 0 : i;
    }

    /**
     * 根据字符串返回两个时间
     *
     * @param s
     * @return
     * @throws Exception
     */
    @NotNull
    public static Time[] formatTimes(String s) throws Exception {
        try {
            String[] tmp = s.split("-");
            String time0 = tmp[0];
            String time1 = tmp[1];
            String[] tmp0 = time0.split(":");
            int t0h = Integer.parseInt(tmp0[0]);
            int t0m = Integer.parseInt(tmp0[1]);
            String[] tmp1 = time1.split(":");
            int t1h = Integer.parseInt(tmp1[0]);
            int t1m = Integer.parseInt(tmp1[1]);
            if (t0h > 23 || t1h > 23 || t0m > 59 || t1m > 59)
                throw new Exception();
            Time r0 = Time.valueOf(tmp[0] + ":00");
            Time r1 = Time.valueOf(tmp[1] + ":00");
            if (r0.after(r1))
                throw new Exception();
            return new Time[]{r0, r1};
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @NotNull
    public static Date parseTime(String s) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(s);
    }
}
