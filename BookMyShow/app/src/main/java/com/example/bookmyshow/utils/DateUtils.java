// DateUtils.java
package com.example.bookmyshow.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Date inconnue";
        }
        return dateTime.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Heure inconnue";
        }
        return dateTime.format(TIME_FORMATTER);
    }

    public static String calculateCheckinTime(LocalDateTime eventTime) {
        if (eventTime == null) {
            return "3 heures avant l'événement";
        }
        return formatTime(eventTime.minusHours(3));
    }
}