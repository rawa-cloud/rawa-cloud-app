package com.rawa.cloud.helper;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateHelper {

    public static Date addDay (int interval, Date now) {
        if (now == null) now = new Date();
        LocalDate localDate = toLocalDate(now);
        return toDate(localDate.plusDays(interval));
    }

    public static Date toDate (LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static LocalDate toLocalDate (Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    public static Date trunc (Date date) {
        return toDate(toLocalDate(new Date()));
    }

    public static Date parse (String date, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static String formatDate (Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format) ; //使用了默认的格式创建了一个日期格式化对象。
        return dateFormat.format(date);
    }

    public static void main(String[] args) {
        System.out.println(toDate(toLocalDate(new Date())));
    }
}
