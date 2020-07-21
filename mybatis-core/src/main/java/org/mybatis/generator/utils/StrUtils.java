package org.mybatis.generator.utils;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Description
 * Author: guos
 * Date: create in 2018-01-23 13:52
 */
public class StrUtils {


    /**
     * 判空
     * param str
     * return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    /**
     * 非空
     * param str
     * return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * 字符串去空格
     *
     * param str
     * return
     */
    public static String getStr(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return str.trim();
    }


    /**
     * 获取字符串长度
     *
     * param str
     * return
     */
    public static int getLength(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }
        return str.trim().length();
    }


    /**
     * 校验是对象是否全是大写
     *
     * param str
     * return
     */
    public static boolean isUpper(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return getStr(str).matches("^[A-Z]+$");
    }


    /**
     * 校验是对象是否全是小写
     *
     * param str
     * return
     */
    public static boolean isLower(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return getStr(str).matches("^[a-z]+$");
    }

    /**
     * 校验是对象是否全是汉字
     *
     * param str
     * return
     */
    public static boolean isChinese(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return getStr(str).matches("^[\u4e00-\u9fa5]+$");
    }


    /**
     * 校验是对象是否全是数字
     *
     * param str
     * return
     */
    public static boolean isNumber(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return getStr(str).matches("^[0-9]+$");
    }



    /**
     * length表示生成字符串的长度
     *
     * param length
     * return
     */
    public static String getRandomString(int length) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        int number;
        for (int i = 0; i < length; i++) {
            number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成指定length的随机字符串（A-Z，a-z，0-9）
     * 排除L,i,0,o,l
     *
     * param length
     * author: guos
     * date: 2019/3/15 9:36
     * return:
     **/
    public static String getRandomStr(int length) {
        String str = "abcdefghjklmnpqrstuvwxyzABCDEFGHIJKMNOPQRSTUVWXYZ23456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    public static String getRandomNum(int length) {
        String str = "123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        System.out.println(getRandomNum(19));
    }
}
