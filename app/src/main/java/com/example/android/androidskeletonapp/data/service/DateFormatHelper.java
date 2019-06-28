package com.example.android.androidskeletonapp.data.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatHelper {

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.US);
        return dateFormat.format(date);
    }
}