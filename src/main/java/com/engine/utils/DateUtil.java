package com.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    public static Map<Object, Object> setDayRange() {
        HashMap<Object, Object> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        map.put("startTime", calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.add(Calendar.SECOND, -1);
        map.put("stopTime", calendar.getTime());
        return map;
    }

    public static long calcDayLeftoverTime() {
        Calendar calendar = Calendar.getInstance();

        Date startTime = calendar.getTime();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.add(Calendar.SECOND, -1);

        Date stopTime = calendar.getTime();

        return (stopTime.getTime() - startTime.getTime()) / 1000;
    }

    public static String format(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static Date parse(String dateStr, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();


        Date startTime = calendar.getTime();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.add(Calendar.SECOND, -1);

        Date stopTime = calendar.getTime();

        System.out.println(df.format(startTime));
        System.out.println(df.format(stopTime));

        long l = stopTime.getTime() - startTime.getTime();
        long s = l / 1000;
        System.out.println(s);
    }
}
