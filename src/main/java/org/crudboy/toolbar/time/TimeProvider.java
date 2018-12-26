package org.crudboy.toolbar.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeProvider {

    private TimeZone timezone;

    public TimeProvider(String timezoneStr) {
        this.timezone = TimeZone.getTimeZone(timezoneStr);
    }

    public Calendar getCalendar(){
        return Calendar.getInstance(timezone);
    }

    public Date parseISODate(String str) throws ParseException {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        isoDateFormat.setTimeZone(timezone);
        return isoDateFormat.parse(str);
    }

    public String getDateStr(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }

    public String getDateStr(Date date) {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        isoDateFormat.setTimeZone(timezone);
        return isoDateFormat.format(date);
    }

    public Date randomDate(Date beginDate, Date endDate) {
        long begin = beginDate.getTime();
        long end = endDate.getTime();
        long rtn = begin + (long) (Math.random() * (end - begin));
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(rtn);
        return calendar.getTime();
    }

    public Date now() {
        return getCalendar().getTime();
    }

    public Date nextHours(Date beginDate, int hours) {
        Calendar calendar = getCalendar();
        calendar.setTime(beginDate);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    public Date nextMinutes(Date beginDate, int minutes) {
        Calendar calendar = getCalendar();
        calendar.setTime(beginDate);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}
