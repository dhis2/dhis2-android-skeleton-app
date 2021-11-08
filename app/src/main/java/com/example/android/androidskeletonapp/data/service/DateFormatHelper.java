package com.example.android.androidskeletonapp.data.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatHelper {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.US);
        return dateFormat.format(date);
    }

    public static String formatSimpleDate(Date date) {
        return dateFormat.format(date);
    }

    public static Date parseSimpleDate(String date) throws ParseException {
        return dateFormat.parse(date);
    }
}