package com.thejazz.chatty.util;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by TheJazz on 15/12/16.
 */

public class Utility {

    public static String VolleyErrorMessage(VolleyError error) {
        String message = null;
        if (error instanceof TimeoutError)
            message = "No Internet Connectivity :(";
        else if (error instanceof NoConnectionError)
            message = "No Connection to Server :(";
        else if (error instanceof AuthFailureError)
            message = "Authentication Failed.";
        else if (error instanceof ServerError)
            message = "Server Request Failed.";
        else if (error instanceof NetworkError)
            message = "No network connectivity.";
        else if (error instanceof ParseError)
            message = "Could not parse data.";
        return message;
    }


    public static String getTimeStamp(String dateStr) {
        String today = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}
