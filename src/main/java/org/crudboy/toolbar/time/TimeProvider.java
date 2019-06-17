package org.crudboy.toolbar.time;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Time Helper class
 */
public class TimeProvider implements Serializable {

    private TimeZone timezone;

    public TimeProvider(String timezoneStr) {
        this.timezone = TimeZone.getTimeZone(timezoneStr);
    }

    public Calendar getCalendar() {
        return Calendar.getInstance(timezone);
    }

    /**
     * Parse date string with ISO format and return Date instance
     *
     * @param str The time string with ISO format
     * @return
     * @throws ParseException
     */
    public Date parseISODate(String str) throws ParseException {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        isoDateFormat.setTimeZone(timezone);
        return isoDateFormat.parse(str);
    }

    public Date parseDate(String str, SimpleDateFormat simpleDateFormat) throws ParseException {
        simpleDateFormat.setTimeZone(timezone);
        return simpleDateFormat.parse(str);
    }

    /**
     * Parse date object and return date string
     *
     * @param date       Date instance
     * @param dateFormat SimpleDateFormat
     * @return
     */
    public String getDateStr(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }

    /**
     * Parse date object and return date string with ISO format
     *
     * @param date Date instance
     * @return
     */
    public String getDateStr(Date date) {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        isoDateFormat.setTimeZone(timezone);
        return isoDateFormat.format(date);
    }

    /**
     * Generate a random date in a range
     *
     * @param beginDate Begin date
     * @param endDate   End date
     * @return
     */
    public Date randomDate(Date beginDate, Date endDate) {
        long begin = beginDate.getTime();
        long end = endDate.getTime();
        long rtn = begin + (long) (Math.random() * (end - begin));
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(rtn);
        return calendar.getTime();
    }

    /**
     * Get current system time
     *
     * @return
     */
    public Date now() {
        return getCalendar().getTime();
    }

    /**
     * Add/Minus n hours on a date and return the result
     *
     * @param beginDate The given date
     * @param hours     The hours that will be added/subtracted on given date
     * @return Result date
     */
    public Date nextHours(Date beginDate, int hours) {
        Calendar calendar = getCalendar();
        calendar.setTime(beginDate);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    /**
     * Add/Minus n minutes on a given date
     *
     * @param beginDate The given date
     * @param minutes   The minutes that will be added/subtracted on given date
     * @return Result date
     */
    public Date nextMinutes(Date beginDate, int minutes) {
        Calendar calendar = getCalendar();
        calendar.setTime(beginDate);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}
