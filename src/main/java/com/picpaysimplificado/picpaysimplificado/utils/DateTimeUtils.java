package com.picpaysimplificado.picpaysimplificado.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateTimeUtils {

    public static int getHour(LocalDateTime dateTime) {
        return dateTime.getHour();
    }

    public static int getDay(LocalDateTime dateTime) {
        return dateTime.getDayOfMonth();
    }

    public static String getMonthName(LocalDateTime dateTime, Locale locale) {
        return dateTime.getMonth().getDisplayName(TextStyle.FULL, locale);
    }

    public static int getMonthNumber(LocalDateTime dateTime) {
        return dateTime.getMonthValue();
    }

    public static int getYear(LocalDateTime dateTime) {
        return dateTime.getYear();
    }

    public static LocalDateTime getStartOfMonth(int year, int month) {
        return LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MIN);
    }

    public static LocalDateTime getEndOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
                .atTime(LocalTime.MAX);
    }

    public static String formatToFullDate(LocalDateTime dateTime, Locale locale) {
        int day = getDay(dateTime);
        String month = getMonthName(dateTime, locale);
        int year = getYear(dateTime);
        return String.format("%d %s %d", day, month, year);
    }
}
