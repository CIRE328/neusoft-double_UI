package util;

import java.util.Calendar;
import java.util.Date;

//日期工具类（适配 java.util.Date）
public class DateUtils {

    //获取当前日期（不含时间）
    public static Date now() {
        return new Date();
    }

    /*计算年龄（基于生日）
     *@param birthday 出生日期
     *@return 年龄
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

    /*日期加减月份
     *@param date 原日期
     *@param months 月数（正数加，负数减）
     *@return 新日期
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    //判断两个日期是否相等（只比较年月日，忽略时间）
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    //判断 date1 是否在 date2 之前（只比较日期，忽略时间）
    public static boolean isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.getTimeInMillis() < cal2.getTimeInMillis();
    }

    //判断 date1 是否在 date2 之后（只比较日期，忽略时间）
    public static boolean isAfter(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.getTimeInMillis() > cal2.getTimeInMillis();
    }
}
