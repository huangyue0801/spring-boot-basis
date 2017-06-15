package com.service.boot.utils.formats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeFormat {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeFormat.class);
    private static final String PATTERN_DATE = "yyyy-MM-dd";
    private static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static Date formatDate(String date) {
        return formatDate(PATTERN_DATE, date);
    }

    public static Date formatDateTime(String dateTime) {
        return formatDateTime(PATTERN_DATE_TIME, dateTime);
    }

    public static Date formatDate(String pattern, String date) {
        try {
            return Date.from(LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            LOGGER.error("格式化时间错误！ pattern=\"{}\" date=\"{}\"", pattern, date);
            return null;
        }
    }

    public static Date formatDateTime(String pattern, String dateTime) {
        try {
            return Date.from(LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern)).atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            LOGGER.error("格式化时间错误！ pattern=\"{}\" dateTime=\"{}\"", pattern, dateTime);
            return null;
        }
    }
}
