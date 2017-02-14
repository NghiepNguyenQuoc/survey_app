package com.nghiepnguyen.survey.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by W10-PRO on 14-Feb-17.
 */

public class TimestampUtils {
    /**
     * Return an ISO 8601 combined date and time string for current date/time
     *
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
     */
    public static String getISO8601StringForCurrentDate(Context mContext) {
        return getISO8601StringForDate(new Date(), mContext);
    }

    /**
     * Return an ISO 8601 combined date and time string for specified date/time
     *
     * @param date Date
     * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
     */
    public static String getISO8601StringForDate(Date date, Context mContext) {
        if (date == null)
            return "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", mContext.getResources().getConfiguration().locale);
        return dateFormat.format(date);
    }

    public static String getISO8601StringForDate(String strDate, Context mContext) {
        Date date = TimestampUtils.getDateUTC(strDate, mContext);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", mContext.getResources().getConfiguration().locale);
        return dateFormat.format(date);
    }

    /**
     * Private constructor: class cannot be instantiated
     */
    private TimestampUtils() {
    }

    /**
     * Return an Date with timezone default
     *
     * @param timestamp
     * @param pattern
     * @return String with format pattern
     */
    public static String getDateCurrentTimeZone(long timestamp, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date currentTimeZone = calendar.getTime();
            return sdf.format(currentTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Return an Date from string
     *
     * @return Date
     */
    public static Date getDate(String strDate, Context mContext) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(getMillisecond(strDate, mContext));
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            Date currentTimeZone = calendar.getTime();
            return currentTimeZone;
        } catch (Exception e) {
        }
        return null;
    }

    public static Date convertStringToDate(String strDate, String dateFormat, Context mContext) {
        DateFormat readFormat = new SimpleDateFormat(dateFormat, mContext.getResources().getConfiguration().locale);
        Date date = null;
        try {
            date = readFormat.parse(strDate);
        } catch (ParseException ex) {
        }
        return date;
    }

    public static String convertStringToMilliSecond(long milliTime, String pattern, Context mContext) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, mContext.getResources().getConfiguration().locale);
        Date date = new Date();
        if (milliTime != 0)
            date = new Date(milliTime);
        return sdf.format(date);
    }

    /**
     * Return an Date from string
     *
     * @return Date
     */
    public static Date getDateUTC(String strDate, Context mContext) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC");
            calendar.setTimeInMillis(getMillisecond(strDate, mContext));
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            return calendar.getTime();
        } catch (Exception e) {
        }
        return null;
    }


    public static long getMillisecond(String dateString, Context mContext) {
        return getMillisecond("HH:mm MMMM dd, yyyy", dateString, mContext);
    }

    // Get millisecond
    public static long getMillisecond(String pattern, String dateString, Context mContext) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, mContext.getResources().getConfiguration().locale);
        try {
            Date date = formatter.parse(dateString);
            long milli = date.getTime();
            return milli;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Date getDate(long milliTime) {
        if (milliTime != 0)
            return new Date(milliTime);
        return null;
    }


    public static String getDate(String pattern, long milliTime, Context mContext) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, mContext.getResources().getConfiguration().locale);
        Date date = getDate(milliTime);
        return sdf.format(date);
    }

    ///////////////////////////////////////////
    // Nghiep.Nguyen - Jan 27th, 2016
    public static int convertSecondToTime(int value) {
        int timeValue = 0;
        if (value / 86400 >= 1) {
            timeValue = Math.round((float) value / 86400);
        } else if (value / 3600 >= 1) {
            timeValue = Math.round((float) value / 3600);
        } else if (value / 60 >= 1) {
            timeValue = Math.round((float) value / 60);
        } else {
            timeValue = value;
        }
        return timeValue;
    }

    public static String getLocationTimeZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public static boolean checkAutoTimeAndAutoTimeZone(Context mContext, String autoPara) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (Settings.Global.getInt(mContext.getContentResolver(), autoPara) == 1) {
                    return true;
                }
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (Settings.System.getInt(mContext.getContentResolver(), autoPara) == 1) {
                    return true;
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}