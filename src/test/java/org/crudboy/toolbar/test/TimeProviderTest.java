package org.crudboy.toolbar.test;

import org.crudboy.toolbar.time.TimeProvider;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class TimeProviderTest {

    private TimeProvider timeProvider = new TimeProvider("GMT+8:00");

    @Test
    public void test() throws ParseException {
        System.out.println(timeProvider.getDateStr(timeProvider.parseISODate("2018-10-10 10:21:10")));

        Date beginDate = timeProvider.parseISODate("2018-10-01 00:00:00");
        Date endDate = timeProvider.parseISODate("2018-10-07 23:59:59");
        System.out.printf("Random date is %s\n", timeProvider.getDateStr(timeProvider.randomDate(beginDate, endDate)));
        System.out.printf("Next hour is %s\n", timeProvider.getDateStr(timeProvider.nextHours(endDate, 1)));
        System.out.printf("Next minute is %s\n", timeProvider.getDateStr(timeProvider.nextMinutes(endDate, 1)));
        System.out.printf("Previous hour is %s\n", timeProvider.getDateStr(timeProvider.nextHours(endDate, -1)));
    }
}
