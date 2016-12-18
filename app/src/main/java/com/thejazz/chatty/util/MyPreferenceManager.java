package com.thejazz.chatty.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.thejazz.chatty.models.User;

import static android.R.attr.id;
import static android.R.attr.name;

public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context mContext;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "chatty_prefs";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "username";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_USERNAME = "username";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_NOTIFICATIONS = "notifications";

    public MyPreferenceManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storeUser(User user, String token) {
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_USERNAME, user.getUsername());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_TOKEN, token);
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_TOKEN, null) != null) {
            String username, name, email;
            int id;
            id = pref.getInt(KEY_USER_ID, 0);
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);
            username = pref.getString(KEY_USER_USERNAME, null);
            User user = new User(id, name, email, username);
            return user;
        }
        return null;
    }

    public String getToken(){
        if (pref.getString(KEY_USER_TOKEN, null) != null) {
            return pref.getString(KEY_USER_TOKEN, null);
        }
        return null;
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}