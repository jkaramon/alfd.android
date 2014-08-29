package com.alfd.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alfd.app.AlfdApplication;
import com.alfd.app.data.User;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Settings {
    private static String ACCESS_TOKEN = "accessToken";
    private static String ENV = "staging";

    private static Map<String, String> staging = ImmutableMap.of(
            "googleClientId", "215330885955-343lvh1rirtn2ohap3ii1lpv0c3ssped.apps.googleusercontent.com",
            "serverUrl", "http://alfd-staging.herokuapp.com/api"
    );
    private static Map<String, String> dev = ImmutableMap.of(
            "googleClientId", "215330885955-3r9oh088v2t3nqd8ii8s6r88rss8ckja.apps.googleusercontent.com",
            "serverUrl", "http://192.168.14.6:3000/api"
    );
    private static Map<String, Map<String, String>> envs = ImmutableMap.of(
            "staging", staging,
            "dev", dev
    );

    private static SharedPreferences getPrefs() {
        return AlfdApplication.getAppContext().getSharedPreferences("alfd.prefs", 0);
    }

    public static String getAccessToken() {
        return getPrefs().getString(ACCESS_TOKEN, null);
    }

    private static Map<String, String> env = envs.get(ENV);


    public static String getGoogleClientId() {
        return env.get("googleClientId");
    }
    public static String getServerUrl() {
        return  env.get("serverUrl");
    }

    public static void setAccessToken(String value) {
        getPrefs().edit().putString(ACCESS_TOKEN, value).commit();
    }
    public static void clearAccessToken() {

        getPrefs().edit().remove(ACCESS_TOKEN).commit();
        User.signOut();
    }


}