package com.wgcloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @version v2.3
 * @ClassName:DateUtil.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: DateUtil.java
 * @Copyright: 2017-2022 wgcloud. All rights reserved.
 */
public class DateUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间单位枚举
     */
    public enum TimeUnit {
        SECOND(Calendar.SECOND),
        MINUTE(Calendar.MINUTE),
        HOUR(Calendar.HOUR_OF_DAY),
        DAY(Calendar.DATE),
        MONTH(Calendar.MONTH),
        YEAR(Calendar.YEAR);

        private final int calendarField;

        TimeUnit(int calendarField) {
            this.calendarField = calendarField;
        }

        public int getCalendarField() {
            return calendarField;
        }
    }


    /**
     * 获取当前时间
     *
     * @return 当前日期
     */
    public static Timestamp getNowTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        Timestamp nowTime = Timestamp.valueOf(dateFormat.format(new Date()));
        return nowTime;
    }


    /**
     * 获取当前系统时间.
     * 默认模板格式yyyy-MM-dd hh:mm:ss.
     *
     * @return 当前系统时间
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime(DATETIME_PATTERN);
    }

    /**
     * 获取当前系统同期。
     *
     * @return 当前系统日期
     * @author zhenggz 2003-11-09
     */
    public static String getCurrentDate() {
        return getCurrentDateTime(DATE_PATTERN);
    }

    /**
     * 获取当前系统时间.
     *
     * @param pattern 时间模板
     * @return 当前系统时间
     */
    public static String getCurrentDateTime(String pattern) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(cal.getTime());
    }

    public static Date getDate(String dateStr) throws ParseException {
        return getDate(dateStr, DATETIME_PATTERN);
    }

    public static Date getDate(String dateStr, String pattern) throws
            ParseException {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        date = dateFormat.parse(dateStr);

        return date;
    }

    public static String getDateString(Date date) {
        return getString(date, DATE_PATTERN);
    }

    public static String getDateTimeString(Date date) {
        return getString(date, DATETIME_PATTERN);
    }

    public static String getString(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static long secsOf2Day(String day1, String day2) {
        try {
            Date date1 = getDate(day1);
            Date date2 = getDate(day2);
            long secs = Math.abs(date1.getTime() - date2.getTime()) / 1000;
            return secs;
        } catch (Exception e) {
            return -1;
        }
    }


    public static String getDateBefore(String datetimes, int day) {
        Calendar now = Calendar.getInstance();
        try {
            now.setTime(getDate(datetimes));
        } catch (ParseException e) {
            logger.error("时间格式 [ " + datetimes + " ]  无法被解析：" + e.toString());
            return null;
        }
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return getString(now.getTime(), DATETIME_PATTERN);
    }

    /**
     * 获取从当前时间开始偏移指定天数的日期
     * @param day 偏移天数（正数表示未来，负数表示过去）
     * @return 偏移后的日期时间字符串
     */
    public static String getDateBefore(int day) {
        return getDateBefore(getCurrentDateTime(), day);
    }

    /**
     * 获取从当前时间开始偏移指定时间单位的日期
     * @param amount 偏移量（正数表示未来，负数表示过去）
     * @param unit 时间单位
     * @return 偏移后的日期时间字符串
     */
    public static String getDateBefore(int amount, TimeUnit unit) {
        return getDateBefore(getCurrentDateTime(), amount, unit);
    }

    /**
     * 获取从指定时间开始偏移指定时间单位的日期
     * @param datetimes 基准时间字符串
     * @param amount 偏移量（正数表示未来，负数表示过去）
     * @param unit 时间单位
     * @return 偏移后的日期时间字符串
     */
    public static String getDateBefore(String datetimes, int amount, TimeUnit unit) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(getDate(datetimes));
        } catch (ParseException e) {
            logger.error("时间格式 [ " + datetimes + " ] 无法被解析：" + e.toString());
            return null;
        }
        
        // 根据时间单位进行偏移
        calendar.add(unit.getCalendarField(), -amount);
        
        return getString(calendar.getTime(), DATETIME_PATTERN);
    }

    public static Date getBeforeDay(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 获取从指定日期开始偏移指定时间单位的日期
     * @param date 基准日期
     * @param amount 偏移量（正数表示未来，负数表示过去）
     * @param unit 时间单位
     * @return 偏移后的日期
     */
    public static Date getBeforeTime(Date date, int amount, TimeUnit unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(unit.getCalendarField(), -amount);
        return calendar.getTime();
    }

    /**
     * 获取从当前时间开始偏移指定时间单位的日期
     * @param amount 偏移量（正数表示未来，负数表示过去）
     * @param unit 时间单位
     * @return 偏移后的日期
     */
    public static Date getBeforeTime(int amount, TimeUnit unit) {
        return getBeforeTime(new Date(), amount, unit);
    }

    /**
     * 获取指定日期的开始时间（00:00:00.000）
     * @param date 指定日期
     * @return 当天的开始时间
     */
    public static Date getDailyStartTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 设置时分秒毫秒为0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 获取指定日期的结束时间（23:59:59.999）
     * @param date 指定日期
     * @return 当天的结束时间
     */
    public static Date getDailyEndTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 设置时分秒毫秒为最大值
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * 获取指定日期的开始时间（返回Timestamp类型）
     * @param date 指定日期
     * @return 当天的开始时间Timestamp
     */
    public static Timestamp getDailyStartTimeAsTimestamp(Date date) {
        Date startTime = getDailyStartTime(date);
        return startTime != null ? new Timestamp(startTime.getTime()) : null;
    }

    /**
     * 获取指定日期的结束时间（返回Timestamp类型）
     * @param date 指定日期
     * @return 当天的结束时间Timestamp
     */
    public static Timestamp getDailyEndTimeAsTimestamp(Date date) {
        Date endTime = getDailyEndTime(date);
        return endTime != null ? new Timestamp(endTime.getTime()) : null;
    }
}
