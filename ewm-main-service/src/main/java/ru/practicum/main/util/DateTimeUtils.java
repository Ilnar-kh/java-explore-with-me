package ru.practicum.main.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtils() {
    }

    public static LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, FORMATTER);
    }

    public static String format(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.format(FORMATTER);
    }
}
