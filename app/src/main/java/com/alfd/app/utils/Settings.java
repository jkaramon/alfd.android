package com.alfd.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alfd.app.AlfdApplication;
import com.alfd.app.data.User;

public class Settings {
    private static String ACCESS_TOKEN = "accessToken";

    private static SharedPreferences getPrefs() {
        return AlfdApplication.getAppContext().getSharedPreferences("alfd.prefs", 0);
    }

    public static String getAccessToken() {
        return getPrefs().getString(ACCESS_TOKEN, null);
    }



    public static void setAccessToken(String value) {
        getPrefs().edit().putString(ACCESS_TOKEN, value).commit();
    }
    public static void clearAccessToken() {

        getPrefs().edit().remove(ACCESS_TOKEN).commit();
        User.signOut();
    }


}