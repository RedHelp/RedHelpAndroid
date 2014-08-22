package org.redhelp.util;

import org.redhelp.common.SlotsCommonFields;
import org.redhelp.common.types.JodaTimeFormatters;
import org.redhelp.helpers.DateTimeHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harshis on 7/5/14.
 */
public class DateHelper {

    public static String createSlotString(SlotsCommonFields slot, int index) {
        String start_time = DateHelper.getTimeInViewableFormat(slot.getStart_datetime());
        String end_time = DateHelper.getTimeInViewableFormat(slot.getEnd_datetime());
        String slot_string = String.format("%d.) %s - %s", index, start_time, end_time);

        return slot_string;

    }

    public static String getTimeInViewableFormat(String date_str) {
        Date start_datetime = DateTimeHelper.convertStringToJavaDate(date_str, JodaTimeFormatters.dateTimeFormatter);
        if(start_datetime == null)
            return null;
        String newDateFormat = new SimpleDateFormat("hh:mm a").format(start_datetime);
        return newDateFormat;

    }

    public static String getDateTimeInViewableFormat(String date_str) {
        Date start_datetime = DateTimeHelper.convertStringToJavaDate(date_str, JodaTimeFormatters.dateTimeFormatter);
        if(start_datetime == null)
            return null;
        String newDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(start_datetime);
        return newDateFormat;
    }

    public static String getDateInViewableFormat(String date_str) {
        Date start_datetime = DateTimeHelper.convertStringToJavaDate(date_str, JodaTimeFormatters.dateFormatter);
        if(start_datetime == null)
            return null;
        String newDateFormat = new SimpleDateFormat("dd-MM-yyyy").format(start_datetime);
        return newDateFormat;
    }
}
