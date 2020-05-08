package org.mybatis.generator.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description：日期工具类
 * guos
 * Date: 2019/1/18 16:56
 **/
public class DateUtils {

    public static String date2Str(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(date);
    }
}
