package cn.finull.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 */
public final class DateUtil {

    public static String DATE_TIME_FROMT = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FROMT = "yyyy-MM-dd";

    public static String format(Date date,String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date parse(String source,String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(source);
    }
}
