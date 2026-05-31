package util;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 基于 java.util.Date 提供当前日期、年龄计算、月份加减及日期比较等常用操作
 */

public class DateUtils {

    /**
     * 获取当前日期（不含时间部分）
     *
     * @return 当前日期
     */

    public static Date now() {
        return new Date();
    }

    /**
     * 根据生日计算年龄
     *
     * @param birthday 出生日期
     * @return 年龄（周岁），birthday 为 null 时返回 0
     */

    public static int calculateAge(Date birthday) {
        if (birthday == null) return 0;
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    /**
     * 对日期进行月份加减
     *
     * @param date   原日期
     * @param months 月数（正数加，负数减）
     * @return 计算后的新日期，date 为 null 时返回 null
     */

    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * 判断两个日期是否为同一天（仅比较年月日，忽略时分秒）
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 同一天返回 true，任一参数为 null 返回 false
     */

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 判断 date1 是否在 date2 之前（仅比较日期，忽略时分秒）
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return date1 早于 date2 返回 true，任一参数为 null 返回 false
     */

    public static boolean isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.getTimeInMillis() < cal2.getTimeInMillis();
    }

    /**
     * 判断 date1 是否在 date2 之后（仅比较日期，忽略时分秒）
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return date1 晚于 date2 返回 true，任一参数为 null 返回 false
     */

    public static boolean isAfter(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.getTimeInMillis() > cal2.getTimeInMillis();
    }
}
